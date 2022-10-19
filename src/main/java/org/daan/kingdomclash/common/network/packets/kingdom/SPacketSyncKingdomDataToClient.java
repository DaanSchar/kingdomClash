package org.daan.kingdomclash.common.network.packets.kingdom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.daan.kingdomclash.client.data.ClientKingdomData;
import org.daan.kingdomclash.common.block.powercrystal.PowerCrystal;
import org.daan.kingdomclash.common.data.kingdom.Kingdom;

import java.util.*;
import java.util.function.Supplier;

public class SPacketSyncKingdomDataToClient {

    private final List<Kingdom> kingdoms;

    public SPacketSyncKingdomDataToClient(Collection<Kingdom> kingdoms) {
        this.kingdoms = new ArrayList<>();
        this.kingdoms.addAll(kingdoms);
    }

    public SPacketSyncKingdomDataToClient(FriendlyByteBuf buf) {
        this.kingdoms = new ArrayList<>();
        int totalKingdoms = buf.readInt();

        for (int i = 0; i < totalKingdoms; i++) {
            Kingdom kingdom = new Kingdom(buf.readUtf());
            kingdom.setLives(buf.readInt());

            BlockPos crystalPos = buf.readBlockPos();
            if (!crystalPos.equals(BlockPos.ZERO)) {
                kingdom.setBlockPos(PowerCrystal.class, crystalPos);
            }
            String color = buf.readUtf();
            kingdom.setColor(ChatFormatting.getByName(color));

            kingdoms.add(kingdom);
        }
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(kingdoms.size());

        for (Kingdom kingdom : kingdoms) {
            buf.writeUtf(kingdom.getName());
            buf.writeInt(kingdom.getLives());
            BlockPos crystalPos = kingdom.getBlockPos(PowerCrystal.class).orElse(BlockPos.ZERO);
            buf.writeBlockPos(crystalPos);
            buf.writeUtf(kingdom.getColor().getName());
        }

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        context.enqueueWork(() -> ClientKingdomData.setKingdoms(this.kingdoms));
        context.setPacketHandled(true);

        return true;
    }

}
