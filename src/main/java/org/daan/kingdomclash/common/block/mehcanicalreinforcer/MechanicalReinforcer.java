package org.daan.kingdomclash.common.block.mehcanicalreinforcer;

import com.simibubi.create.content.contraptions.base.DirectionalKineticBlock;
import com.simibubi.create.foundation.block.ITE;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.daan.kingdomclash.index.KCTileEntities;
import org.jetbrains.annotations.Nullable;

public class MechanicalReinforcer extends DirectionalKineticBlock implements ITE<MechanicalReinforcerTileEntity> {

    public MechanicalReinforcer(Properties properties) {
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
    public Class<MechanicalReinforcerTileEntity> getTileEntityClass() {
        return MechanicalReinforcerTileEntity.class;
    }

    @Override
    public BlockEntityType<? extends MechanicalReinforcerTileEntity> getTileEntityType() {
        return KCTileEntities.MECHANICAL_REINFORCER.get();
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return KCTileEntities.MECHANICAL_REINFORCER.create(pos, state);
    }


}
