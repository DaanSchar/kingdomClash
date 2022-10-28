package org.daan.kingdomclash.client.events;

import net.minecraft.core.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;


/**
 * Cube Area in front of a directionalBlock.
 */
public class DirectionalBlockArea {

    private final BlockPos directionalBlockPosition;
    private final Level level;
    private final int size;

    public DirectionalBlockArea(BlockPos directionalBlockPosition, Level level, int size) {
        this.directionalBlockPosition = directionalBlockPosition;
        this.level = level;
        this.size = size;
    }

    /**
     * Checks if a position is inside the Cube area.
     */
    public boolean isInArea(BlockPos blockPos) {
        AABB area = getArea();

        float x = blockPos.getX();
        float y = blockPos.getY();
        float z = blockPos.getZ();

        if (x >= area.minX && x < area.maxX) {
            if (y >= area.minY && y < area.maxY) {
                return z >= area.minZ && z < area.maxZ;
            }
        }

        return false;
    }

    public AABB getArea() {
        Direction facing = getReinforcerDirection();

        int offset = (size / 2);
        BlockPos pos = this.directionalBlockPosition;
        double yMin = pos.getY() - offset;
        double yMax = pos.getY() + offset + 1d;

        return switch (facing) {
            case NORTH -> new AABB(
                    pos.getX() - offset,
                    yMin,
                    pos.getZ() + 1,
                    pos.getX() + offset + 1,
                    yMax,
                    pos.getZ() + this.size + 1
            );
            case SOUTH -> new AABB(
                    pos.getX() - offset,
                    yMin,
                    pos.getZ(),
                    pos.getX() + offset + 1,
                    yMax,
                    pos.getZ() - this.size - 1
            );
            case WEST -> new AABB(
                    pos.getX() + 1,
                    yMin,
                    pos.getZ() - offset,
                    pos.getX() + this.size + 1,
                    yMax,
                    pos.getZ() + offset + 1
            );
            case EAST -> new AABB(
                    pos.getX(),
                    yMin,
                    pos.getZ() - offset,
                    pos.getX() - this.size,
                    yMax,
                    pos.getZ() + offset + 1
            );
            case DOWN -> new AABB(
                    pos.getX() - offset,
                    pos.getY() + 1,
                    pos.getZ() - offset,

                    pos.getX() + offset + 1,
                    pos.getY() + this.size + 1,
                    pos.getZ() + offset + 1
            );
            case UP -> new AABB(
                    pos.getX() - offset,
                    pos.getY(),
                    pos.getZ() - offset,

                    pos.getX() + offset + 1,
                    pos.getY() - this.size - 1,
                    pos.getZ() + offset + 1
            );
        };
    }

    private Direction getReinforcerDirection() {
        return this.level.getBlockState(this.directionalBlockPosition).getValue(BlockStateProperties.FACING);
    }

}
