package com.villagertrading.registry;

import com.villagertrading.VillagerTradingMod;
import com.villagertrading.menu.VillagerTradingStationMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS = 
        DeferredRegister.create(ForgeRegistries.MENU_TYPES, VillagerTradingMod.MODID);
    
    public static final RegistryObject<MenuType<VillagerTradingStationMenu>> VILLAGER_TRADING_STATION = 
        MENUS.register("villager_trading_station", 
            () -> IForgeMenuType.create(VillagerTradingStationMenu::new));
    
    public static void register(IEventBus eventBus) {
        MENUS.register(eventBus);
    }
}