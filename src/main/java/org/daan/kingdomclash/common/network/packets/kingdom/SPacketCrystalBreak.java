package org.daan.kingdomclash.common.network.packets.kingdom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import org.daan.kingdomclash.client.data.ClientKingdomData;

import java.util.Random;
import java.util.function.Supplier;

public class SPacketCrystalBreak {

    private final String kingdomNameUnderAttack;
    private final int currentLives;
    private final BlockPos crystalPos;

    public SPacketCrystalBreak(String kingdomNameUnderAttack, BlockPos crystalPos, int currentLives) {
        this.kingdomNameUnderAttack = kingdomNameUnderAttack;
        this.currentLives = currentLives;
        this.crystalPos = crystalPos;
    }

    public SPacketCrystalBreak(FriendlyByteBuf buf) {
        this.kingdomNameUnderAttack = buf.readUtf();
        this.currentLives = buf.readInt();
        this.crystalPos = buf.readBlockPos();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(this.kingdomNameUnderAttack);
        buf.writeInt(this.currentLives);
        buf.writeBlockPos(this.crystalPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> DistExecutor.unsafeCallWhenOn(Dist.CLIENT, () -> () -> {
            ClientKingdomData.getPlayer().ifPresent(this::handlePacket);
            return 0;
        }));

        context.setPacketHandled(true);
        return false;
    }

    private void handlePacket(Player player) {
        ClientKingdomData.getKingdomName().ifPresent(
                kingdomNameOfPlayer -> {
                    boolean playerIsKingdomMember = kingdomNameOfPlayer.equalsIgnoreCase(kingdomNameUnderAttack);

                    if (playerIsKingdomMember) {
                        notifyKingdomMember(player);
                    }

                    if (!playerIsKingdomMember) {
                        notifyNonKingdomMember(player);
                    }

                    notifyAnyone(player);
                }
        );
    }

    private void spawnSoulParticles(Level level, BlockPos position) {
        for (int i = 0; i < 360; i++) {
//            if (i % 30 == 0) {
//                level.addParticle(
//                        ParticleTypes.SOUL,
//                        position.getX() + 0.5d + rand(),
//                        position.getY() + 1d + rand(),
//                        position.getZ() + 0.5d + rand(),
//                        Math.cos(i) * (0.6d + rand() / 3d),
//                        0.2 + rand() / 2.0,
//                        Math.sin(i) * (0.6d + rand() / 3d)
//                );
//                level.addParticle(
//                        ParticleTypes.SOUL,
//                        position.getX() + 0.5d + rand(),
//                        position.getY() + 1d + rand(),
//                        position.getZ() + 0.5d + rand(),
//                        Math.cos(i) * (0.6d + rand() / 3d),
//                        -(0.2 + rand() / 2.0),
//                        Math.sin(i) * (0.6d + rand() / 3d)
//                );
//            }
//
//            if (i % 60 == 0) {
//                level.addParticle(
//                        ParticleTypes.SOUL,
//                        position.getX() + 0.5 + rand(),
//                        position.getY() + 0.5 + rand(),
//                        position.getZ() + 0.5 + rand(),
//                        Math.cos(i) * (0.1 + rand() / 3.0),
//                        0.6 + rand() / 3.0,
//                        Math.sin(i) * (0.1 + rand() / 3.0)
//                );
//            }
//            level.addParticle(
//                    ParticleTypes.DRAGON_BREATH,
//                    position.getX() + 0.5d + rand(),
//                    position.getY() + 1d + rand(),
//                    position.getZ() + 0.5d + rand(),
//                    Math.cos(i) * (0.6d + rand() / 3d),
//                    0.2 + rand() / 2.0,
//                    Math.sin(i) * (0.6d + rand() / 3d)
//            );

            double distanceFromCenter = 3;

            if (i % 20 == 0) {
                level.addParticle(
                        ParticleTypes.ELECTRIC_SPARK,
                        position.getX() + 0.5d + rand() * distanceFromCenter,
                        position.getY() + 0.5d + rand() * distanceFromCenter,
                        position.getZ() + 0.5d + rand() * distanceFromCenter,
                        Math.cos(i) * (0.1d + rand() / 3d),
                        0.1 + rand() / 2.0,
                        Math.sin(i) * (0.1d + rand() / 3d)
                );
            }

            distanceFromCenter = 3;
            if (i % 30 == 0) {
                level.addParticle(
                        ParticleTypes.FIREWORK,
                        position.getX() + 0.5d + rand() * distanceFromCenter,
                        position.getY() + 0.5d + rand() * distanceFromCenter,
                        position.getZ() + 0.5d + rand() * distanceFromCenter,
                        Math.cos(i) * (0.1d + rand() / 3d),
                        0.1 + rand() / 2.0,
                        Math.sin(i) * (0.1d + rand() / 3d)
                );
            }
        }
    }

    private void notifyKingdomMember(Player player) {
        float randomSoundPitch = ((float) Math.random() / 10f) + 0.9f;
        player.playSound(SoundEvents.ANVIL_PLACE, 1f, randomSoundPitch);

        String message = "Your Kingdom is under attack!";

        if (kingdomIsDead()) {
            message = "Your Kingdom has fallen!";
        }

        player.displayClientMessage(new TextComponent(message).withStyle(ChatFormatting.RED), true);
    }

    private void notifyNonKingdomMember(Player player) {
        player.level.playSound(player, crystalPos, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundSource.BLOCKS, 1f, 1f);
    }

    private void notifyAnyone(Player player) {
        if (kingdomIsDead()) {
            player.playSound(SoundEvents.WITHER_DEATH, 1f, 1f);
        }

        spawnSoulParticles(player.getLevel(), crystalPos);
    }

    private double rand() {
        Random rand = new Random();
        return (rand.nextDouble() * 2 - 1) / 3.0;
    }

    private boolean kingdomIsDead() {
        return this.currentLives == 0;
    }

    public BlockPos getCrystalPos() {
        return crystalPos;
    }

    public String getKingdomNameUnderAttack() {
        return kingdomNameUnderAttack;
    }
}
