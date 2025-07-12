package com.villagertrading.registry;

import com.villagertrading.VillagerTradingMod;
import com.villagertrading.block.VillagerTradingStationBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = 
        DeferredRegister.create(ForgeRegistries.BLOCKS, VillagerTradingMod.MODID);
    
    public static final RegistryObject<Block> VILLAGER_TRADING_STATION = 
        BLOCKS.register("villager_trading_station", VillagerTradingStationBlock::new);
    
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}