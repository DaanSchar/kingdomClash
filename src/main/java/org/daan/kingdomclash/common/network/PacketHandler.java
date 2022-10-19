package org.daan.kingdomclash.common.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.network.packets.kingdom.*;
public class PacketHandler {

    private static SimpleChannel instance;

    private static int id = 0;

    public static void register() {
        SimpleChannel net = NetworkRegistry.ChannelBuilder
                .named(new ResourceLocation(KingdomClash.MOD_ID, "messages"))
                .networkProtocolVersion(() -> "1.0")
                .clientAcceptedVersions(s -> true)
                .serverAcceptedVersions(s -> true)
                .simpleChannel();

        instance = net;

        net.messageBuilder(PacketExample.class, id(), NetworkDirection.PLAY_TO_SERVER)
                .decoder(PacketExample::new)
                .encoder(PacketExample::toBytes)
                .consumer(PacketExample::handle)
                .add();

        net.messageBuilder(SPacketSyncKingdomDataToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SPacketSyncKingdomDataToClient::new)
                .encoder(SPacketSyncKingdomDataToClient::toBytes)
                .consumer(SPacketSyncKingdomDataToClient::handle)
                .add();

        net.messageBuilder(SPacketCrystalBreak.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SPacketCrystalBreak::new)
                .encoder(SPacketCrystalBreak::toBytes)
                .consumer(SPacketCrystalBreak::handle)
                .add();

        net.messageBuilder(SPacketExplosion.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SPacketExplosion::new)
                .encoder(SPacketExplosion::toBytes)
                .consumer(SPacketExplosion::handle)
                .add();

        net.messageBuilder(SPacketMemberDied.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SPacketMemberDied::new)
                .encoder(SPacketMemberDied::toBytes)
                .consumer(SPacketMemberDied::handle)
                .add();

        net.messageBuilder(SPacketSyncMemberDataToClient.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SPacketSyncMemberDataToClient::new)
                .encoder(SPacketSyncMemberDataToClient::toBytes)
                .consumer(SPacketSyncMemberDataToClient::handle)
                .add();

        net.messageBuilder(SPacketActivatedMechBeacon.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SPacketActivatedMechBeacon::new)
                .encoder(SPacketActivatedMechBeacon::toBytes)
                .consumer(SPacketActivatedMechBeacon::handle)
                .add();

        net.messageBuilder(SPacketDeactivatedMechBeacon.class, id(), NetworkDirection.PLAY_TO_CLIENT)
                .decoder(SPacketDeactivatedMechBeacon::new)
                .encoder(SPacketDeactivatedMechBeacon::toBytes)
                .consumer(SPacketDeactivatedMechBeacon::handle)
                .add();
    }

    public static int id() {
        return id++;
    }

    public static <MSG> void sendToServer(MSG message) {
        instance.sendToServer(message);
    }

    public static <MSG> void sendToPlayer(MSG message, ServerPlayer player) {
        instance.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    public static void sendPacketToAllPlayers(Level level, Object packet) {
        level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                PacketHandler.sendToPlayer(packet, serverPlayer);
            }
        });
    }

}
