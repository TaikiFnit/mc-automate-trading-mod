package com.villagertrading.config;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class VillagerTradingConfig {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_AUTO_TRADING;
    public static final ForgeConfigSpec.BooleanValue RESTRICT_EMERALD_TRADES;
    public static final ForgeConfigSpec.IntValue TRADING_RANGE;
    public static final ForgeConfigSpec.IntValue TRADING_COOLDOWN;
    public static final ForgeConfigSpec.BooleanValue GIVE_EXPERIENCE;
    public static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;

    static {
        BUILDER.push("villager_trading");

        ENABLE_AUTO_TRADING = BUILDER
                .comment("Enable automatic trading when items are dropped near villagers")
                .define("enable_auto_trading", true);

        RESTRICT_EMERALD_TRADES = BUILDER
                .comment("Restrict emerald trades for safety (prevents accidental expensive trades)")
                .define("restrict_emerald_trades", true);

        TRADING_RANGE = BUILDER
                .comment("Trading range in blocks")
                .defineInRange("trading_range", 3, 1, 10);

        TRADING_COOLDOWN = BUILDER
                .comment("Trading cooldown time in ticks")
                .defineInRange("trading_cooldown", 20, 1, 100);

        GIVE_EXPERIENCE = BUILDER
                .comment("Give experience points for automatic trades")
                .define("give_experience", true);

        DEBUG_LOGGING = BUILDER
                .comment("Enable debug logging")
                .define("debug_logging", false);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}