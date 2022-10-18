package org.daan.kingdomclash.common.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.data.*;
import org.slf4j.Logger;

import java.util.function.Supplier;

public class PacketExample {

    private static Logger log = KingdomClash.LOGGER;

    public static final String EXAMPLE_MESSAGE = "message.kingdomclash.example";

    public PacketExample() {

    }

    public PacketExample(FriendlyByteBuf buf) {

    }

    public void toBytes(FriendlyByteBuf buf) {

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();

            int count = DataManager.get(player.level).extractData(player.blockPosition());

            if (count <= 0) {
                //do nothing
            } else {
                player.getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(playerData -> playerData.addData(count));
            }

           log.info("Received package from {}", player.getDisplayName().getContents());
        });

        return true;
    }

}
