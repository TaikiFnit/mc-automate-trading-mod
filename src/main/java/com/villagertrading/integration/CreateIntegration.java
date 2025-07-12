package com.villagertrading.integration;

import com.villagertrading.VillagerTradingMod;
import com.villagertrading.config.VillagerTradingConfig;
import com.villagertrading.trading.VillagerTradeManager;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.fml.ModList;

import java.util.List;

public class CreateIntegration {
    private static final VillagerTradeManager tradeManager = new VillagerTradeManager();
    
    public static boolean isCreateLoaded() {
        return ModList.get().isLoaded("create");
    }
    
    /**
     * Handles deployer interactions with villagers
     * This method should be called when a deployer attempts to use an item on a villager
     */
    public static boolean handleDeployerVillagerInteraction(Level level, BlockPos deployerPos, 
                                                          ItemStack heldItem, Villager villager) {
        if (!VillagerTradingConfig.ENABLE_AUTO_TRADING.get()) {
            return false;
        }
        
        if (!isCreateLoaded()) {
            return false;
        }
        
        if (level.isClientSide()) {
            return false;
        }
        
        // Check if the deployer is close enough to the villager
        double maxDistance = VillagerTradingConfig.TRADING_RANGE.get();
        double distance = deployerPos.distSqr(villager.blockPosition());
        
        if (distance > maxDistance * maxDistance) {
            return false;
        }
        
        // Create a temporary item entity for the trade system
        // This is a bit of a hack, but allows us to reuse the existing trade logic
        return tryDeployerTrade(villager, heldItem);
    }
    
    private static boolean tryDeployerTrade(Villager villager, ItemStack heldItem) {
        if (heldItem.isEmpty()) {
            return false;
        }
        
        // Check if this item can be traded
        if (!canTradeItem(villager, heldItem)) {
            return false;
        }
        
        // Find matching trades
        var offers = villager.getOffers();
        for (var offer : offers) {
            if (offer.isOutOfStock()) {
                continue;
            }
            
            ItemStack costA = offer.getCostA();
            if (ItemStack.isSameItemSameTags(heldItem, costA) && 
                heldItem.getCount() >= costA.getCount()) {
                
                // Check if second cost is required
                if (!offer.getCostB().isEmpty()) {
                    continue; // Skip trades requiring two items for now
                }
                
                // Execute the trade
                return executeDeployerTrade(villager, heldItem, offer);
            }
        }
        
        return false;
    }
    
    private static boolean canTradeItem(Villager villager, ItemStack item) {
        if (villager.isBaby() || villager.isSleeping() || villager.isTrading()) {
            return false;
        }
        
        // Safety check for emerald trades
        if (VillagerTradingConfig.RESTRICT_EMERALD_TRADES.get() && 
            item.getItem().toString().toLowerCase().contains("emerald")) {
            return false;
        }
        
        return true;
    }
    
    private static boolean executeDeployerTrade(Villager villager, ItemStack heldItem, 
                                               net.minecraft.world.item.trading.MerchantOffer offer) {
        ItemStack costA = offer.getCostA();
        int requiredCount = costA.getCount();
        
        if (heldItem.getCount() < requiredCount) {
            return false;
        }
        
        // Check if the villager can still perform this trade
        if (offer.getUses() >= offer.getMaxUses()) {
            return false;
        }
        
        // Perform the trade
        offer.increaseUses();
        
        // The deployer system should handle item consumption and result placement
        // This method just handles the villager side of the trade
        
        // Give experience to villager if configured
        if (VillagerTradingConfig.GIVE_EXPERIENCE.get()) {
            // Villager gains experience from successful trades
            villager.setVillagerXp(villager.getVillagerXp() + offer.getXp());
        }
        
        // Update villager trading data
        villager.notifyTrade(offer);
        
        // Play trading sound
        villager.level().playSound(null, villager.blockPosition(), 
            net.minecraft.sounds.SoundEvents.VILLAGER_YES, 
            net.minecraft.sounds.SoundSource.NEUTRAL, 1.0F, 1.0F);
        
        if (VillagerTradingConfig.DEBUG_LOGGING.get()) {
            VillagerTradingMod.LOGGER.info("Deployer trade executed: {} -> {}", 
                costA.getDisplayName().getString(),
                offer.getResult().getDisplayName().getString());
        }
        
        return true;
    }
    
    /**
     * Find villagers near a deployer position
     */
    public static List<Villager> findNearbyVillagers(Level level, BlockPos deployerPos, double range) {
        AABB searchArea = new AABB(
            deployerPos.getX() - range, deployerPos.getY() - range, deployerPos.getZ() - range,
            deployerPos.getX() + range, deployerPos.getY() + range, deployerPos.getZ() + range
        );
        
        return level.getEntitiesOfClass(Villager.class, searchArea);
    }
}