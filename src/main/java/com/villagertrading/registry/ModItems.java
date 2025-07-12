package com.villagertrading.registry;

import com.villagertrading.VillagerTradingMod;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
        DeferredRegister.create(ForgeRegistries.ITEMS, VillagerTradingMod.MODID);
    
    public static final RegistryObject<Item> VILLAGER_TRADING_STATION = 
        ITEMS.register("villager_trading_station", 
            () -> new BlockItem(ModBlocks.VILLAGER_TRADING_STATION.get(), 
                new Item.Properties()));
    
    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}