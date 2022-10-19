package org.daan.kingdomclash.common.block.transformer;

import com.simibubi.create.content.contraptions.base.KineticTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.daan.kingdomclash.common.data.kingdom.Kingdom;
import org.daan.kingdomclash.common.data.kingdom.KingdomManager;
import org.daan.kingdomclash.common.network.PacketHandler;
import org.daan.kingdomclash.common.network.packets.kingdom.SPacketActivatedMechBeacon;
import org.daan.kingdomclash.common.network.packets.kingdom.SPacketDeactivatedMechBeacon;
import org.daan.kingdomclash.server.config.ServerConfig;

import java.util.*;


public class MechanicalBeaconTileEntity extends KineticTileEntity {

    private boolean firstTick;
    private final float STRESS = 512;
    private float lastTickImpact;
    private int tick;
    private final int TICK_THRESH_HOLD = 20;
    private final int effectDuration = TICK_THRESH_HOLD * 15;

    private final HashMap<MobEffect, Integer> currentEffects;

    public MechanicalBeaconTileEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.firstTick = true;
        this.tick = 0;
        this.currentEffects = new HashMap<>();

        currentEffects.put(MobEffects.DAMAGE_RESISTANCE, ServerConfig.MECHANICAL_BEACON_RESISTANCE_STRESS.get());
        currentEffects.put(MobEffects.REGENERATION, ServerConfig.MECHANICAL_BEACON_REGENERATION_STRESS.get());
        currentEffects.put(MobEffects.DAMAGE_BOOST, ServerConfig.MECHANICAL_BEACON_STRENGTH_STRESS.get());
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
            float impact = Math.abs(getSpeed() * STRESS);
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
            }

            lastTickImpact = 0;
        }
    }

    private void onRotationTick(float impact) {
        if (!isValid(level)) {
            return;
        }

        getKingdom(level).ifPresent(
                kingdom -> {
                    for (Player player : kingdom.getPlayers(level)) {
                        if (player != null) {
                            for (Map.Entry<MobEffect, Integer> entry : currentEffects.entrySet()) {
                                int minImpact = entry.getValue();
                                MobEffect effect = entry.getKey();

                                if (impact >= minImpact) {
                                    player.addEffect(getInstance(effect));
                                }
                            }
                        }
                    }
                }
        );

        if (impact != lastTickImpact) {
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

        getKingdom(level).ifPresent(
                kingdom -> {
                    for (Player player : kingdom.getPlayers(level)) {
                        if (player instanceof ServerPlayer serverPlayer) {
                            PacketHandler.sendToPlayer(message, serverPlayer);
                        }
                    }
                }
        );
    }

    private boolean isRotating() {
        return Math.abs(getSpeed()) > 0 && isSpeedRequirementFulfilled();
    }

    private boolean isValid(Level level) {
        if (level == null) {
            return false;
        }

        return !level.isClientSide;
    }

    private MobEffectInstance getInstance(MobEffect effect) {
        return new MobEffectInstance(effect, effectDuration);
    }

    private Optional<Kingdom> getKingdom(Level level) {
        if (!isValid(level)) {
            return Optional.empty();
        }

        return KingdomManager.get(level).getKingdom(MechanicalBeacon.class, getBlockPos());
    }

}
