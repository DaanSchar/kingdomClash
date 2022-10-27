package org.daan.kingdomclash.common.block.mechanicalreinforcer;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.daan.kingdomclash.client.events.DirectionalBlockArea;
import org.daan.kingdomclash.common.network.PacketHandler;
import org.daan.kingdomclash.common.network.packets.kingdom.*;
import org.daan.kingdomclash.index.KCBlockProperties;

import java.util.Optional;


public class MechanicalReinforcerTileEntity extends KineticTileEntity {

    private boolean firstTick;
    private final float STRESS = 512;
    private float lastTickImpact;
    private int tick;
    private final int TICK_THRESH_HOLD = 20;
    private float impact = 0;

    public MechanicalReinforcerTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.firstTick = true;
        this.tick = 0;
    }

    @Override
    public float calculateStressApplied() {
        return STRESS;
    }

    @Override
    public void tick() {
        super.tick();

        if (tick < TICK_THRESH_HOLD) {
            tick++;
            return;
        }

        tick = 0;

        if (!isValid(level)) {
            return;
        }

        if (isRotating()) {
            impact = Math.abs(getSpeed() * STRESS);
            onRotationTick(impact);

            if (firstTick) {
                onFirstTick();
                firstTick = false;
            }

            lastTickImpact = impact;
        } else {
            firstTick = true;

            if (lastTickImpact != 0) {
                announceDeactivation();
                setRotatingInBlockState(false);
                setChanged();
            }

            lastTickImpact = 0;
        }
    }

    private void onRotationTick(float impact) {
        if (!isValid(level)) {
            return;
        }

        if (!getBlockState().getValue(KCBlockProperties.ROTATING)) {
            setRotatingInBlockState(true);
        }

        if (impact != lastTickImpact) {
            setChanged();
        }

    }

    private void onFirstTick() {
        announceActivation();
    }

    private void announceDeactivation() {
        announce(new SPacketDeactivatedMechBeacon(getBlockPos()));

    }

    private void announceActivation() {
        announce(new SPacketActivatedMechBeacon(getBlockPos()));
    }

    private <MSG> void announce(MSG message) {
        if (!isValid(level)) {
            return;
        }

//        getKingdom(level).ifPresent(
//                kingdom -> {
//                    for (Player player : kingdom.getPlayers(level)) {
//                        if (player instanceof ServerPlayer serverPlayer) {
//                            PacketHandler.sendToPlayer(message, serverPlayer);
//                        }
//                    }
//                }
//        );

        for (Player player : level.players()) {
            if (player instanceof ServerPlayer serverPlayer) {
                PacketHandler.sendToPlayer(message, serverPlayer);
            }
        }
    }

    public boolean isRotating() {
        return Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled();
    }

    private boolean isValid(Level level) {
        if (level == null) {
            return false;
        }

        return !level.isClientSide;
    }

    private void setRotatingInBlockState(boolean rotating) {
        this.level.setBlock(getBlockPos(), this.getBlockState().setValue(KCBlockProperties.ROTATING, rotating), 3);
    }

    public Optional<AABB> getArea() {
        float impact = Math.abs(calculateStressApplied() * getSpeed());

        if (getSpeed() == 0) {
            return Optional.empty();
        }

        int range = (int) (Math.sqrt(impact) / 10d);
        return Optional.of(new DirectionalBlockArea(getBlockPos(), level, range).getArea());
    }



}
