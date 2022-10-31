package org.daan.kingdomclash.common.mixin;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.DefendVillageTargetGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(IronGolem.class)
public abstract class IronGolemMixin {

    @Inject(at = @At(value = "HEAD"), method = "registerGoals")
    protected void registerGoals(CallbackInfo ci) {
//        IronGolem golem = (IronGolem)((Object)this);
//
//        golem.goalSelector.addGoal(1, new MeleeAttackGoal(golem, 1.0D, true));
//        golem.goalSelector.addGoal(2, new MoveTowardsTargetGoal(golem, 0.9D, 32.0F));
//        golem.goalSelector.addGoal(2, new MoveBackToVillageGoal(golem, 0.6D, false));
//        golem.goalSelector.addGoal(4, new GolemRandomStrollInVillageGoal(golem, 0.6D));
//        golem.goalSelector.addGoal(5, new OfferFlowerGoal(golem));
//        golem.goalSelector.addGoal(7, new LookAtPlayerGoal(golem, Player.class, 6.0F));
//        golem.goalSelector.addGoal(8, new RandomLookAroundGoal(golem));
//        golem.targetSelector.addGoal(1, new DefendVillageTargetGoal(golem));
//        golem.targetSelector.addGoal(2, new HurtByTargetGoal(golem));
//        golem.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(golem, Player.class, 10, true, false, golem::isAngryAt));
//        golem.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(golem, Mob.class, 5, false, false, (p_28879_) -> {
//            return p_28879_ instanceof Enemy && !(p_28879_ instanceof Creeper);
//        }));
//        golem.targetSelector.addGoal(4, new ResetUniversalAngerTargetGoal<>(golem, false));
//        golem.goalSelector.addGoal(0, new FollowOwnerGoal(golem, 1.0D, 10.0F, 2.0F, false));
    }

}
