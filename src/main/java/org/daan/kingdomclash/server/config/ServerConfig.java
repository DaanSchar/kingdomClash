package org.daan.kingdomclash.server.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {

    public static ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Integer> KINGDOM_MAX_LIVES;
    public static final ForgeConfigSpec.ConfigValue<Integer> KINGDOM_START_LIVES;
    public static final ForgeConfigSpec.ConfigValue<Integer> KINGDOM_DAMAGE_AMOUNT;

    public static final ForgeConfigSpec.ConfigValue<Integer> MECHANICAL_BEACON_RESISTANCE_STRESS;
    public static final ForgeConfigSpec.ConfigValue<Integer> MECHANICAL_BEACON_REGENERATION_STRESS;
    public static final ForgeConfigSpec.ConfigValue<Integer> MECHANICAL_BEACON_STRENGTH_STRESS;

    static {
        BUILDER.push("KingdomClash");

        KINGDOM_MAX_LIVES = BUILDER.comment("Total amount of lives a kingdom may have.")
                .define("max lives", 200);

        KINGDOM_START_LIVES = BUILDER.comment("Total amount of lives a kingdom may start with.")
                .define("start lives", 200);

        KINGDOM_DAMAGE_AMOUNT = BUILDER.comment("Total amount of damage dealt when breaking the PowerCrystal.")
                .define("damage on break", 1);

        MECHANICAL_BEACON_RESISTANCE_STRESS = BUILDER.comment("The minimum amount of stress needed to give the strength effect. \n" + "The default value is equal to a level 1 steam-engine at 24 rpm")
                .define("Resistance Stress", 16_384);

        MECHANICAL_BEACON_REGENERATION_STRESS = BUILDER.comment("The minimum amount of stress needed to give the strength effect. \nThe default value is equal to a level 4 steam-engine at 128 rpm")
                .define("Regeneration Stress", 65_536);

        MECHANICAL_BEACON_STRENGTH_STRESS = BUILDER.comment("The minimum amount of stress needed to give the strength effect. \nThe default value is equal to a level 8 steam-engine at 256 rpm")
                        .define("Strength Stress", 131_072);


        BUILDER.pop();
        SPEC = BUILDER.build();
    }

}
