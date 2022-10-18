package org.daan.kingdomclash.common.block.custom;

import com.simibubi.create.content.contraptions.base.KineticBlock;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class CustomRotationBlock extends KineticBlock {

    public CustomRotationBlock(Properties properties) {
        super(properties);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return null;
    }

}
