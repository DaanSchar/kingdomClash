package org.daan.kingdomclash.common.network.packets.kingdom;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.network.NetworkEvent;
import org.daan.kingdomclash.client.data.ClientKingdomData;
import org.daan.kingdomclash.common.data.kingdom.Kingdom;
import org.daan.kingdomclash.common.data.kingdom.KingdomManager;

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
            String color = buf.readUtf();
            kingdom.setColor(ChatFormatting.getByName(color));

            for (Class<? extends Block> blockClass : KingdomManager.getBlockClasses()) {
                BlockPos pos = buf.readBlockPos();
                if (!pos.equals(BlockPos.ZERO)) {
                    kingdom.setBlockPos(blockClass, pos);
                }
            }

            kingdoms.add(kingdom);
        }

    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(kingdoms.size());

        for (Kingdom kingdom : kingdoms) {
            buf.writeUtf(kingdom.getName());
            buf.writeInt(kingdom.getLives());
            buf.writeUtf(kingdom.getColor().getName());

            for (Class<? extends Block> blockClass : KingdomManager.getBlockClasses()) {
                kingdom.getBlockPos(blockClass).ifPresentOrElse(
                        buf::writeBlockPos,
                        () -> buf.writeBlockPos(BlockPos.ZERO)
                );
            }
        }

    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();

        if (this.kingdoms == null) {
            LogUtils.getLogger().info("NULL ERROR INBOUND");
        }

        context.enqueueWork(() -> ClientKingdomData.setKingdoms(this.kingdoms));
        context.setPacketHandled(true);

        return true;
    }

}
