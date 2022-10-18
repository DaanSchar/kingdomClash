package org.daan.kingdomclash.common.data;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import org.daan.kingdomclash.common.network.PacketHandler;
import org.daan.kingdomclash.common.network.PacketSyncDataToClient;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class DataManager extends SavedData {

    private final Map<ChunkPos, Data> dataMap = new HashMap<>();
    private final Random random = new Random();

    private int counter;

    @Nonnull
    public static DataManager get(Level level) {
        if (level.isClientSide()) {
            throw new RuntimeException("Don't access this client-side");
        }
        DimensionDataStorage storage = ((ServerLevel)level).getDataStorage();
        return storage.computeIfAbsent(DataManager::new, DataManager::new, "datamanager");
    }

    public DataManager() {
    }

    public void tick(Level level) {
        counter--;

        if (counter <= 0) {
            counter = 10;

            level.players().forEach(player -> {
                if (player instanceof ServerPlayer serverPlayer) {
                    int playerData = serverPlayer.getCapability(PlayerDataProvider.PLAYER_DATA)
                            .map(PlayerData::getData)
                            .orElse(-1);

                    int chunkData = getData(serverPlayer.blockPosition());
//                    PacketHandler.sendToPlayer(new PacketSyncDataToClient(playerData, chunkData), serverPlayer);
                }
            });
        }
    }

    public DataManager(CompoundTag tag) {
        ListTag list = tag.getList("data", Tag.TAG_COMPOUND);

        for (Tag t : list) {
            CompoundTag dataTag = (CompoundTag) t;
            Data data = new Data(dataTag.getInt("data"));
            ChunkPos pos = new ChunkPos(dataTag.getInt("x"), dataTag.getInt("z"));
            dataMap.put(pos, data);
        }
    }

    public int getData(BlockPos pos) {
        Data data = getDataInternal(pos);
        return data.getData();
    }

    public int extractData(BlockPos pos) {
        Data data = getDataInternal(pos);
        int count = data.getData();

        if (count > 0) {
             data.setData(count - 1);
             setDirty();
             return 1;
        } else {
            return 0;
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();

        dataMap.forEach((key, value) -> {
            CompoundTag dataTag = new CompoundTag();
            dataTag.putInt("x", key.x);
            dataTag.putInt("z", key.z);
            dataTag.putInt("data", value.getData());
            list.add(dataTag);
        });

        tag.put("data", list);
        return tag;
    }

    @Nonnull
    private Data getDataInternal(BlockPos pos) {
        ChunkPos chunkPos = new ChunkPos(pos);
        return dataMap.computeIfAbsent(chunkPos, chunkpos -> new Data(random.nextInt(100)));
    }

}
