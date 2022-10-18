package org.daan.kingdomclash.common.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public class PowerCrystalBlock extends Block {

    public PowerCrystalBlock(Properties properties) {
        super(properties);
    }

    @Override
    public void animateTick(@NotNull BlockState blockState, @NotNull Level level, @NotNull BlockPos blockPos, Random rand) {
        float chance = 0.2f;

        if (chance < rand.nextFloat()) {
            level.addParticle(
                    ParticleTypes.SOUL,
                    blockPos.getX() + 0.5 + (rand(rand) / 2.0),
                    blockPos.getY() + 0.5,
                    blockPos.getZ() + 0.5 + (rand(rand) / 2.0),
                    rand(rand) / 20.0,
                    0.07,
                    rand(rand) / 20.0
            );
        }


        super.animateTick(blockState, level, blockPos, rand);
    }

    private double rand(Random rand) {
        return (rand.nextDouble() * 2.0) - 1;
    }
}
