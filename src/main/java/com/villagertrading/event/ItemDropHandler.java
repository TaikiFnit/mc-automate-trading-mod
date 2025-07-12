package com.villagertrading.event;

import com.villagertrading.VillagerTradingMod;
import com.villagertrading.config.VillagerTradingConfig;
import com.villagertrading.trading.VillagerTradeManager;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.entity.item.ItemExpireEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = VillagerTradingMod.MODID)
public class ItemDropHandler {
    
    private static final VillagerTradeManager tradeManager = new VillagerTradeManager();
    
    @SubscribeEvent
    public static void onLivingTick(LivingEvent.LivingTickEvent event) {
        if (!VillagerTradingConfig.ENABLE_AUTO_TRADING.get()) {
            return;
        }
        
        if (!(event.getEntity() instanceof Villager villager)) {
            return;
        }
        
        Level level = villager.level();
        if (level.isClientSide()) {
            return;
        }
        
        // Check for items near the villager every 20 ticks (1 second)
        if (level.getGameTime() % VillagerTradingConfig.TRADING_COOLDOWN.get() == 0) {
            checkForNearbyItems(villager);
        }
    }
    
    private static void checkForNearbyItems(Villager villager) {
        Level level = villager.level();
        double range = VillagerTradingConfig.TRADING_RANGE.get();
        
        AABB searchArea = new AABB(
            villager.getX() - range, villager.getY() - range, villager.getZ() - range,
            villager.getX() + range, villager.getY() + range, villager.getZ() + range
        );
        
        List<ItemEntity> nearbyItems = level.getEntitiesOfClass(ItemEntity.class, searchArea);
        
        for (ItemEntity itemEntity : nearbyItems) {
            if (itemEntity.isAlive() && itemEntity.getAge() > 20) { // Item must exist for at least 1 second
                if (tradeManager.attemptTrade(villager, itemEntity)) {
                    if (VillagerTradingConfig.DEBUG_LOGGING.get()) {
                        VillagerTradingMod.LOGGER.info("Successfully traded {} with villager at {}", 
                            itemEntity.getItem().getDisplayName().getString(),
                            villager.blockPosition());
                    }
                    break; // Only process one trade per tick
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onItemExpire(ItemExpireEvent event) {
        // This event can be used to clean up any tracking data if needed
        // Currently not needed but available for future use
    }
}