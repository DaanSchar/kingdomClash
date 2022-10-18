package org.daan.kingdomclash.common.effects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

public class SoulBleedingEffect extends MobEffect {

    protected SoulBleedingEffect(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        if (entity.getLevel().isClientSide()) {
            return;
        }

        entity.setHealth(entity.getHealth());

        if (entity instanceof Player player) {
            var foodData = player.getFoodData();
        }

        super.applyEffectTick(entity, amplifier);
    }
}
