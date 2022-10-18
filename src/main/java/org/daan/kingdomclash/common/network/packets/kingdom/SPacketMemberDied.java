package org.daan.kingdomclash.common.network.packets.kingdom;

import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.daan.kingdomclash.client.data.ClientKingdomData;
import org.daan.kingdomclash.common.data.kingdom.Kingdom;

import java.util.Random;
import java.util.function.Supplier;

public class SPacketMemberDied {

    private final String kingdomName;

    public SPacketMemberDied(String kingdomName) {
        this.kingdomName = kingdomName;
    }

    public SPacketMemberDied(FriendlyByteBuf buf) {
        this.kingdomName = buf.readUtf();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.kingdomName);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
                ClientKingdomData.getKingdomName().ifPresent(
                        playerKingdomName -> {
                            ClientKingdomData.getPlayer().ifPresent(
                                    player -> {
                                        if (playerKingdomName.equals(this.kingdomName)) {
                                            player.playSound(SoundEvents.LIGHTNING_BOLT_THUNDER, 0.1f, 1f);
                                        }

                                        ClientKingdomData.getKingdom(this.kingdomName).flatMap(Kingdom::getCrystalPosition).ifPresent(
                                                crystalPos -> {
                                                    player.getLevel().playSound(
                                                            player,
                                                            crystalPos,
                                                            SoundEvents.GHAST_DEATH,
                                                            SoundSource.BLOCKS,
                                                            1f,
                                                            1f
                                                    );
                                                    spawnSoulParticles(player.getLevel(), crystalPos);
                                                }
                                        );
                                    }
                            );
                        }
                );

                return 0;
            });
        });

        context.setPacketHandled(true);

        return true;
    }

    private void spawnSoulParticles(Level level, BlockPos position) {
        for (int i = 0; i < 360; i++) {
            if (i % 60 == 0) {
                level.addParticle(
                        ParticleTypes.SOUL,
                        position.getX() + 0.5d + rand(),
                        position.getY() + 1d + rand(),
                        position.getZ() + 0.5d + rand(),
                        Math.cos(i) * (0.05d + rand() / 3d),
                        0.1d,
                        Math.sin(i) * (0.05d + rand() / 3d)
                );
            }
        }
    }


    private double rand() {
        Random rand = new Random();
        return (rand.nextDouble() * 2 - 1) / 3.0;
    }

}
