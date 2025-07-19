package com.villagertrading.trading;

import com.villagertrading.VillagerTradingMod;
import com.villagertrading.config.VillagerTradingConfig;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VillagerTradeManager {
    private final Random random = new Random();
    
    public boolean attemptTrade(Villager villager, ItemEntity itemEntity) {
        if (!canTrade(villager, itemEntity)) {
            return false;
        }
        
        ItemStack droppedItem = itemEntity.getItem();
        MerchantOffers offers = villager.getOffers();
        
        List<MerchantOffer> validOffers = findValidOffers(offers, droppedItem);
        if (validOffers.isEmpty()) {
            return false;
        }
        
        // Select a random valid offer
        MerchantOffer selectedOffer = validOffers.get(random.nextInt(validOffers.size()));
        
        return executeTrade(villager, itemEntity, selectedOffer);
    }
    
    private boolean canTrade(Villager villager, ItemEntity itemEntity) {
        if (villager == null || itemEntity == null || !itemEntity.isAlive()) {
            return false;
        }
        
        if (villager.isBaby() || villager.getOffers().isEmpty()) {
            return false;
        }
        
        // Check if villager can trade (not sleeping, not in conversation, etc.)
        if (villager.isSleeping() || villager.isTrading()) {
            return false;
        }
        
        ItemStack droppedItem = itemEntity.getItem();
        
        // Safety check: restrict emerald trades if configured
        if (VillagerTradingConfig.RESTRICT_EMERALD_TRADES.get() && 
            droppedItem.is(Items.EMERALD)) {
            if (VillagerTradingConfig.DEBUG_LOGGING.get()) {
                VillagerTradingMod.LOGGER.info("Blocked emerald trade for safety");
            }
            return false;
        }
        
        return true;
    }
    
    private List<MerchantOffer> findValidOffers(MerchantOffers offers, ItemStack droppedItem) {
        List<MerchantOffer> validOffers = new ArrayList<>();
        
        for (MerchantOffer offer : offers) {
            if (offer.isOutOfStock()) {
                continue;
            }
            
            // Check if the dropped item matches the first cost item
            ItemStack firstCost = offer.getCostA();
            if (ItemStack.isSameItemSameTags(droppedItem, firstCost) && 
                droppedItem.getCount() >= firstCost.getCount()) {
                
                // Check if we need a second item and if it's available
                ItemStack secondCost = offer.getCostB();
                if (!secondCost.isEmpty()) {
                    // For now, skip trades that require two items
                    // This could be enhanced later to check player inventory
                    continue;
                }
                
                validOffers.add(offer);
            }
        }
        
        return validOffers;
    }
    
    private boolean executeTrade(Villager villager, ItemEntity itemEntity, MerchantOffer offer) {
        Level level = villager.level();
        ItemStack droppedItem = itemEntity.getItem();
        ItemStack requiredItem = offer.getCostA();
        
        // Check if this is a food item that affects villager breeding
        boolean isBreedingFood = isBreedingFood(droppedItem);
        
        // If it's breeding food, let the villager pick it up naturally for breeding
        // instead of consuming it through trading
        if (isBreedingFood && !villager.isTrading()) {
            // Allow natural villager AI to handle food pickup for breeding
            return false;
        }
        
        // Calculate how many trades we can perform
        int maxTrades = Math.min(
            droppedItem.getCount() / requiredItem.getCount(),
            offer.getMaxUses() - offer.getUses()
        );
        
        if (maxTrades <= 0) {
            return false;
        }
        
        // Perform the trade
        int actualTrades = Math.min(maxTrades, 1); // Limit to 1 trade per attempt for balance
        
        // Update the offer usage
        for (int i = 0; i < actualTrades; i++) {
            offer.increaseUses();
        }
        
        // Calculate items consumed and produced
        int itemsConsumed = requiredItem.getCount() * actualTrades;
        ItemStack resultItem = offer.getResult().copy();
        resultItem.setCount(resultItem.getCount() * actualTrades);
        
        // Update the dropped item
        droppedItem.shrink(itemsConsumed);
        if (droppedItem.isEmpty()) {
            itemEntity.discard();
        }
        
        // Create the result item entity
        ItemEntity resultEntity = new ItemEntity(level, 
            villager.getX(), villager.getY() + 0.5, villager.getZ(), 
            resultItem);
        level.addFreshEntity(resultEntity);
        
        // Give experience to the villager if configured
        if (VillagerTradingConfig.GIVE_EXPERIENCE.get()) {
            // Villager gains experience from successful trades
            villager.setVillagerXp(villager.getVillagerXp() + offer.getXp() * actualTrades);
        }
        
        // Play trading sound
        level.playSound(null, villager.blockPosition(), 
            SoundEvents.VILLAGER_YES, SoundSource.NEUTRAL, 
            1.0F, 1.0F);
        
        // Update villager trading data
        villager.notifyTrade(offer);
        
        if (VillagerTradingConfig.DEBUG_LOGGING.get()) {
            VillagerTradingMod.LOGGER.info("Executed {} trades: {} {} -> {} {}", 
                actualTrades,
                itemsConsumed, requiredItem.getDisplayName().getString(),
                resultItem.getCount(), resultItem.getDisplayName().getString());
        }
        
        return true;
    }
    
    private boolean isBreedingFood(ItemStack itemStack) {
        // Check if the item is food that villagers use for breeding
        return itemStack.is(Items.BREAD) || 
               itemStack.is(Items.POTATO) || 
               itemStack.is(Items.CARROT) || 
               itemStack.is(Items.BEETROOT);
    }
}