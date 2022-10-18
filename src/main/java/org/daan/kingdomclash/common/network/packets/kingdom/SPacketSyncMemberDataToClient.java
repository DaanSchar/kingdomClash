package org.daan.kingdomclash.common.network.packets.kingdom;

import com.mojang.authlib.GameProfile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.daan.kingdomclash.client.data.ClientKingdomData;

import java.util.function.Supplier;

public class SPacketSyncMemberDataToClient {

    private final String kingdomName;
    private final GameProfile member;

    public SPacketSyncMemberDataToClient(String kingdomName, GameProfile member) {
        this.kingdomName = kingdomName;
        this.member = member;
    }

    public SPacketSyncMemberDataToClient(FriendlyByteBuf buf) {
        this.kingdomName = buf.readUtf();
        this.member = new GameProfile(buf.readUUID(), buf.readUtf());
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeUtf(kingdomName);
        buf.writeUUID(member.getId());
        buf.writeUtf(member.getName());
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ClientKingdomData.getKingdom(this.kingdomName).ifPresent(
                    kingdom -> kingdom.addMember(this.member)
            );
        });

        return true;
    }

}
