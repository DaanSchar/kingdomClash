package org.daan.kingdomclash.common.block.mechanicalbeacon;

import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.core.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import org.daan.kingdomclash.index.KCTileEntities;
import org.jetbrains.annotations.Nullable;

public class MechanicalBeacon extends DirectionalKineticBlock implements ITE<MechanicalBeaconTileEntity> {

    public MechanicalBeacon(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction preferred = getPreferredFacing(context);
        if ((context.getPlayer() != null && context.getPlayer()
                .isShiftKeyDown()) || preferred == null)
            return super.getStateForPlacement(context);
        return defaultBlockState().setValue(FACING, preferred);
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState blockState) {
        return blockState.getValue(FACING)
                .getAxis();
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        return face == state.getValue(FACING);
    }

    @Override
    public Class<MechanicalBeaconTileEntity> getTileEntityClass() {
        return MechanicalBeaconTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalBeaconTileEntity> getTileEntityType() {
        return KCTileEntities.MECHANICAL_BEACON.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return KCTileEntities.MECHANICAL_BEACON.create(pos, state);
    }


}
