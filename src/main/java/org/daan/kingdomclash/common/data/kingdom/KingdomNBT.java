package org.daan.kingdomclash.common.data.kingdom;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class KingdomNBT {

    public static ListTag save(Kingdom kingdom)  {
        ListTag tag = new ListTag();

        CompoundTag infoTag = new CompoundTag();
        infoTag.putString("kingdomName", kingdom.getName());
        infoTag.putInt("lives", kingdom.getLives());
        infoTag.putString("color", kingdom.getColor().getName());

        saveBlockPos(kingdom.getCrystalPosition(), "crystalPos",infoTag);
        savePosition(kingdom.getSpawnPoint(), "spawnPoint", infoTag);

        tag.add(infoTag);
        tag.addAll(saveMembers(kingdom));

        return tag;
    }

    public static Kingdom load(ListTag kingdomTag) {
        CompoundTag infoTag = (CompoundTag) kingdomTag.get(0);
        Kingdom kingdom = new Kingdom(infoTag.getString("kingdomName"));

        kingdom.setSpawnPoint(loadPosition(infoTag, "spawnPoint"));
        kingdom.setCrystalPosition(loadBlockPos(infoTag, "crystalPos"));
        kingdom.setLives(infoTag.getInt("lives"));
        kingdom.setColor(ChatFormatting.getByName(infoTag.getString("color")));
        kingdom.setMembers(loadMembers(kingdomTag));

        return kingdom;
    }


    private static void saveBlockPos(Optional<BlockPos> block, String name, CompoundTag tag) {
        block.ifPresentOrElse(
                blockPos -> saveBlockPosition(blockPos, name, tag),
                () -> saveBlockPosition(BlockPos.ZERO, name, tag)
        );
    }

    private static void savePosition(Optional<Vec3> position, String name, CompoundTag tag) {
        position.ifPresentOrElse(
                pos -> savePosition(pos, name, tag),
                () -> savePosition(Vec3.ZERO, name, tag)
        );
    }

    private static void savePosition(Vec3 position, String name, CompoundTag tag) {
        tag.putIntArray(
                name,
                new int[]{
                        (int) position.x(),
                        (int) position.y(),
                        (int) position.z()
                }
        );
    }

    private static void saveBlockPosition(BlockPos position, String name, CompoundTag tag) {
        tag.putIntArray(
                name,
                new int[]{position.getX(), position.getY(), position.getZ()}
        );
    }

    private static ListTag saveMembers(Kingdom kingdom) {
        ListTag listTag = new ListTag();

        kingdom.getMembers().forEach(member -> {
            CompoundTag memberTag = new CompoundTag();
            memberTag.putUUID("uuid", member.getId());
            memberTag.putString("playerName", member.getName());
            listTag.add(memberTag);
        });

        return listTag;
    }

    private static BlockPos loadBlockPos(CompoundTag tag, String name) {
        int[] blockPosArray = tag.getIntArray(name);
        BlockPos blockPos = new BlockPos(
                blockPosArray[0],
                blockPosArray[1],
                blockPosArray[2]
        );

        if (!blockPos.equals(BlockPos.ZERO)) {
            return blockPos;
        }

        return null;
    }

    private static Vec3 loadPosition(CompoundTag tag, String name) {
        int[] posArray = tag.getIntArray(name);
        Vec3 position = new Vec3(
                posArray[0],
                posArray[1],
                posArray[2]
        );

        if (!position.equals(Vec3.ZERO)) {
            return position;
        }

        return null;
    }

    private static Set<GameProfile> loadMembers(ListTag tag) {
        Set<GameProfile> members = new HashSet<>();

        for (int i = 1; i < tag.size(); i++) {
            CompoundTag memberTag = (CompoundTag) tag.get(i);
            members.add(
                    new GameProfile(
                            memberTag.getUUID("uuid"),
                            memberTag.getString("playerName")
                    )
            );
        }

        return members;
    }

}
