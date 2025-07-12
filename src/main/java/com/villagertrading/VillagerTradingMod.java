package com.villagertrading;

import com.villagertrading.config.VillagerTradingConfig;
import com.villagertrading.event.ItemDropHandler;
import com.villagertrading.registry.ModBlockEntities;
import com.villagertrading.registry.ModBlocks;
import com.villagertrading.registry.ModItems;
import com.villagertrading.registry.ModMenuTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(VillagerTradingMod.MODID)
public class VillagerTradingMod {
    public static final String MODID = "villagertrading";
    public static final Logger LOGGER = LogManager.getLogger();

    public VillagerTradingMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        // Register mod content
        ModBlocks.register(modEventBus);
        ModItems.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        
        // Register configuration
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, VillagerTradingConfig.SPEC);
        
        // Register event handlers
        MinecraftForge.EVENT_BUS.register(new ItemDropHandler());
        
        LOGGER.info("Villager Trading Automation mod initialized");
    }
}