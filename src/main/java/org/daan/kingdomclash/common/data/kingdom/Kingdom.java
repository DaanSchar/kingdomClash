package org.daan.kingdomclash.common.data.kingdom;

import com.mojang.authlib.GameProfile;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.daan.kingdomclash.server.config.ServerConfig;

import java.util.*;
import java.util.stream.Collectors;

public class Kingdom {

    private String name;
    private GameProfile Leader;
    private Set<GameProfile> members;
    private int lives;
    private Vec3 spawnPoint;
    private ChatFormatting color;
    private final HashMap<Class<?>, BlockPos> blocks;

    public Kingdom(String name) {
        this.name = name;
        this.members = new HashSet<>();
        this.lives = ServerConfig.KINGDOM_START_LIVES.get();
        this.color = ChatFormatting.WHITE;
        blocks = new HashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public GameProfile getLeader() {
        return Leader;
    }

    public void setLeader(GameProfile leader) {
        Leader = leader;
    }

    public Collection<GameProfile> getMembers() {
        return members;
    }

    public Collection<Player> getPlayers(Level level) {
        if (level.isClientSide()) {
            return new ArrayList<>();
        }

        return members.stream().map(GameProfile::getId).map(level::getPlayerByUUID).collect(Collectors.toList());
    }

    public void setMembers(Set<GameProfile> members) {
        this.members = members;
    }

    public void addMember(GameProfile player) {
        this.members.add(player);
    }

    public Optional<BlockPos> getBlockPos(Class<?> blockClass) {
        return Optional.ofNullable(this.blocks.get(blockClass));
    }

    public void setBlockPos(Class<?> blockClass, BlockPos pos) {
        this.blocks.put(blockClass, pos);
    }

    public int getLives() {
        return lives;
    }

    public void setLives(int lives) {
        if (lives > getMaxLives() || lives < 0) {
            return;
        }

        this.lives = lives;
    }

    public int getMaxLives() {
        return ServerConfig.KINGDOM_MAX_LIVES.get();
    }

    public boolean isDead() {
        return this.lives <= 0;
    }

    public Optional<Vec3> getSpawnPoint() {
        return Optional.ofNullable(spawnPoint);
    }

    public void setSpawnPoint(Vec3 spawnPoint) {
        this.spawnPoint = spawnPoint;
    }

    @Override
    public String toString() {
        return name;
    }

    public ChatFormatting getColor() {
        if (this.color == null) {
            return ChatFormatting.WHITE;
        }

        return this.color;
    }

    public void setColor(ChatFormatting color) {
        this.color = color;
    }

}
