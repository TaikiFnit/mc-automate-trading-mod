package com.villagertrading.registry;

import com.villagertrading.VillagerTradingMod;
import com.villagertrading.blockentity.VillagerTradingStationBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = 
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, VillagerTradingMod.MODID);
    
    public static final RegistryObject<BlockEntityType<VillagerTradingStationBlockEntity>> VILLAGER_TRADING_STATION = 
        BLOCK_ENTITIES.register("villager_trading_station", 
            () -> BlockEntityType.Builder.of(VillagerTradingStationBlockEntity::new, 
                ModBlocks.VILLAGER_TRADING_STATION.get()).build(null));
    
    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}