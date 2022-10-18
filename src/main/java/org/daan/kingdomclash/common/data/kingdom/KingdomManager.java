package org.daan.kingdomclash.common.data.kingdom;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.server.level.*;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.daan.kingdomclash.common.network.PacketHandler;
import org.daan.kingdomclash.common.network.packets.kingdom.SPacketSyncKingdomDataToClient;
import org.daan.kingdomclash.common.network.packets.kingdom.SPacketSyncMemberDataToClient;

import javax.annotation.Nonnull;
import java.util.*;


public class KingdomManager extends SavedData {

    private final HashMap<String, Kingdom> kingdoms = new HashMap<>();

    private int counter = 0;

    @Nonnull
    public static KingdomManager get(Level level) {
        if (level.isClientSide()) {
            throw new RuntimeException("Can't access data from client side");
        }

        DimensionDataStorage storage = ((ServerLevel) level).getDataStorage();
        return storage.computeIfAbsent(KingdomManager::new, KingdomManager::new, "kingdommanager");
    }

    public KingdomManager() {
    }

    public KingdomManager(CompoundTag savedTag) {
        ListTag kingdomTagList = savedTag.getList("kingdoms", Tag.TAG_LIST);

        for (Tag tag : kingdomTagList) {
            Kingdom kingdom = KingdomNBT.load((ListTag) tag);
            kingdoms.put(kingdom.getName(), kingdom);
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();

        kingdoms.forEach((name, kingdom) -> list.add(KingdomNBT.save(kingdom)));

        tag.put("kingdoms", list);
        return tag;
    }

    public void tick(Level level) {
        counter++;

        if (counter > 40) {
            counter = 0;

            updateAllPlayerClients(level);
        }
    }

    public void updateAllPlayerClients(Level level) {
        level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                updatePlayer(serverPlayer);
            }
        });
    }

    public void createNewKingdom(String name) {
        Kingdom kingdom = new Kingdom(name);
        kingdoms.put(name, kingdom);
        setDirty();
    }

    public boolean addMember(@Nonnull Kingdom kingdom, GameProfile player) {
        var playerKingdom = getKingdom(player);
        boolean playerAlreadyInAKingdom = playerKingdom.isPresent();

        if (playerAlreadyInAKingdom) {
            return false;
        }

        kingdom.addMember(player);
        setDirty();

        return true;
    }

    public boolean removeMember(@Nonnull Kingdom kingdom, GameProfile player) {
        boolean removed = kingdom.getMembers().remove(player);

        if (removed) {
            setDirty();
        }
        return removed;
    }

    public Optional<Kingdom> getKingdom(GameProfile player) {
        for (Map.Entry<String, Kingdom> entry : kingdoms.entrySet()) {
            Kingdom kingdom = entry.getValue();
            for (GameProfile member : kingdom.getMembers()) {
                if (member.equals(player)) {
                    return Optional.of(kingdom);
                }
            }
        }

        return Optional.empty();
    }

    public Optional<Kingdom> getKingdom(String kingdomName) {
        for (Map.Entry<String, Kingdom> kingdom : kingdoms.entrySet()) {
            if (kingdom.getValue().getName().equalsIgnoreCase(kingdomName)) {
                return Optional.of(kingdom.getValue());
            }
        }

        return Optional.empty();
    }

    public Optional<Kingdom> getKingdom(BlockPos crystalPos) {
        for (Kingdom kingdom : kingdoms.values()) {
            var kingdomCrystalPos = kingdom.getCrystalPosition();

            if (kingdomCrystalPos.isPresent()) {
                if (kingdomCrystalPos.get().equals(crystalPos)) {
                    return Optional.of(kingdom);
                }
            }
        }

        return Optional.empty();
    }

    public Map<String, Kingdom> getKingdoms() {
        return this.kingdoms;
    }


    public boolean removeKingdom(String name) {
        for (Kingdom kingdom : kingdoms.values()) {
            if (kingdom.getName().equalsIgnoreCase(name)) {
                kingdoms.remove(kingdom.getName());
                setDirty();
                return true;
            }
        }

        return false;
    }

    public void setCrystalPosition(@Nonnull Kingdom kingdom, BlockPos pos) {
        kingdom.setCrystalPosition(pos);
        setDirty();
    }

    public void incrementLives(@Nonnull Kingdom kingdom, int total) {
        kingdom.setLives(kingdom.getLives() + total);
        setDirty();
    }

    public void decrementLives(@Nonnull Kingdom kingdom, int total) {
        incrementLives(kingdom, -total);
    }

    public void setColor(@Nonnull Kingdom kingdom, ChatFormatting color) {
        kingdom.setColor(color);
        setDirty();
    }

    private void updatePlayer(ServerPlayer player) {
        sendPackets(player);
        player.refreshDisplayName();
        player.refreshTabListName();
    }

    private void sendPackets(ServerPlayer player) {
        PacketHandler.sendToPlayer(new SPacketSyncKingdomDataToClient(kingdoms.values()), player);

        for (Kingdom kingdom : kingdoms.values()) {
            for (GameProfile member : kingdom.getMembers()) {
                PacketHandler.sendToPlayer(new SPacketSyncMemberDataToClient(kingdom.getName(), member), player);
            }
        }
    }

    public void setLives(Kingdom kingdom, int lives) {
        kingdom.setLives(lives);
        setDirty();
    }
}
