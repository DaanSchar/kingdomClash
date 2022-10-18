package org.daan.kingdomclash.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.daan.kingdomclash.client.ClientDataData;

import java.util.function.Supplier;

public class PacketSyncDataToClient {

    private final int playerData;
    private final int chunkData;

    public PacketSyncDataToClient(int playerData, int chunkData) {
        this.chunkData = chunkData;
        this.playerData = playerData;
    }

    public PacketSyncDataToClient(FriendlyByteBuf buf) {
        playerData = buf.readInt();
        chunkData = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(playerData);
        buf.writeInt(chunkData);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> ClientDataData.set(playerData, chunkData));

        return true;
    }
}
