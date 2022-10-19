package org.daan.kingdomclash.common.network.packets.kingdom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.daan.kingdomclash.client.data.ClientKingdomData;

import java.util.Random;
import java.util.function.Supplier;

public class SPacketDeactivatedMechBeacon {

    private final BlockPos pos;

    public SPacketDeactivatedMechBeacon(BlockPos pos) {
        this.pos = pos;
    }

    public SPacketDeactivatedMechBeacon(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
            ClientKingdomData.getPlayer().ifPresent(
                    player -> {
                        spawnSoulParticles(player.level, this.pos);
                        player.playSound(SoundEvents.BEACON_DEACTIVATE, 1f, 1f);
                    }
            );

            return 0;
        }));

        context.setPacketHandled(true);
        return false;
    }

    private void spawnSoulParticles(Level level, BlockPos position) {
        for (int i = 0; i < 360; i++) {
            level.addParticle(
                    ParticleTypes.DRAGON_BREATH,
                    position.getX() + 0.5d + rand(),
                    position.getY() + 1d + rand(),
                    position.getZ() + 0.5d + rand(),
                    Math.cos(i) * (0.6d + rand() / 3d),
                    0.2 + rand() / 2.0,
                    Math.sin(i) * (0.6d + rand() / 3d)
            );
        }
    }

    private double rand() {
        Random rand = new Random();
        return (rand.nextDouble() * 2 - 1) / 3.0;
    }

}
