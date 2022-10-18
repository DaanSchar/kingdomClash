package org.daan.kingdomclash.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.daan.kingdomclash.common.KingdomClash;

public class ModEffects {

    public static final DeferredRegister<MobEffect> MOD_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, KingdomClash.MOD_ID);


    public static void register(IEventBus eventBus) {
        MOD_EFFECTS.register(eventBus);
    }

}
