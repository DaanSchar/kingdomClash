package org.daan.kingdomclash.server.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> KINGDOM_MAX_LIVES;
    public static final ForgeConfigSpec.ConfigValue<Integer> KINGDOM_START_LIVES;
    public static final ForgeConfigSpec.ConfigValue<Integer> KINGDOM_DAMAGE_AMOUNT;

    static {
        BUILDER.push("KingdomClash");

        KINGDOM_MAX_LIVES = BUILDER.comment("Total amount of lives a kingdom may have.")
                .define("max lives", 200);

        KINGDOM_START_LIVES = BUILDER.comment("Total amount of lives a kingdom may start with.")
                .define("start lives", 200);

        KINGDOM_DAMAGE_AMOUNT = BUILDER.comment("Total amount of damage dealt when breaking the PowerCrystal.")
                .define("damage on break", 1);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
