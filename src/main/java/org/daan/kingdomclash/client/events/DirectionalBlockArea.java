package org.daan.kingdomclash.client.events;

import net.minecraft.core.*;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class DirectionalBlockArea {

    private final BlockPos directionalBlockPosition;
    private final Level level;
    private final int size;
    private static final boolean RENDER_TO_PLAYER = true;

    /**
     * Cube Area in front of a directionalBlock.
     * Only works if block is horizontally directed.
     */
    public DirectionalBlockArea(BlockPos directionalBlockPosition, Level level, int size) {
        this.directionalBlockPosition = directionalBlockPosition;
        this.level = level;
        this.size = size;
    }

    /**
     * Checks if a position is inside the Cube area.
     */
    public boolean isInArea(BlockPos blockPos) {
        if (isOnYAxis()) {
            return false;
        }

        boolean inRange = false;

        for (int i = 0; i < this.size; i++) {
            for (int j = 0; j < this.size; j++) {
                if (isInArea(i, j, blockPos)) {

                    // instead of returning true, we save the bool value so that
                    // both loops finish. That way renderPos gets called for each position in the area.
                    inRange = true;
                }
            }
        }

        return inRange;
    }

    private boolean isInArea(int i, int j, BlockPos blockPos) {
        Direction facing = getReinforcerDirection();

        int x = directionalBlockPosition.getX();
        int z = directionalBlockPosition.getZ();

        if (isOnZAxis()) {
            z -= (i + 1) * facing.getStepZ();
            x -= -(size / 2) + j;
        } else if (isOnXAxis()) {
            x -= (i + 1) * facing.getStepX();
            z -= -(size / 2) + j;
        }

        if (RENDER_TO_PLAYER) {
            renderPos(new BlockPos(x, blockPos.getY(), z));
        }

        return (x == blockPos.getX()) && (z == blockPos.getZ()) &&
                (blockPos.getY() - directionalBlockPosition.getY() <= size);
    }

    private boolean isOnZAxis() {
        return getReinforcerDirection().getStepZ() != 0;
    }

    private boolean isOnXAxis() {
        return getReinforcerDirection().getStepX() != 0;
    }

    private boolean isOnYAxis() {
        return getReinforcerDirection().getStepY() != 0;
    }

    private Direction getReinforcerDirection() {
        return this.level.getBlockState(this.directionalBlockPosition).getValue(BlockStateProperties.FACING);
    }

    // visual stuff

    private void renderPos(BlockPos pos) {
        spawnSoulParticles(this.level, pos);
    }

    private static void spawnSoulParticles(Level level, BlockPos position) {
        for (int i = 0; i < 1; i++) {
            level.addParticle(
                    ParticleTypes.SMOKE,
                    position.getX() + 0.5d + rand(),
                    position.getY() + 0.5d + rand(),
                    position.getZ() + 0.5d + rand(),
                    Math.cos(i) * (rand() / 10d),
                    0.2 + rand() / 10.0,
                    Math.sin(i) * (rand() / 10d)
            );
        }
    }

    private static double rand() {
        return ((Math.random() * 2) - 1) / 2.0;
    }

}
