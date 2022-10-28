package org.daan.kingdomclash.client.data;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.world.entity.player.Player;
import org.daan.kingdomclash.common.data.kingdom.Kingdom;

import java.util.*;

public class ClientKingdomData {

    private static List<Kingdom> kingdoms = new ArrayList<>();

    public static Optional<String> getKingdomName() {
        User player = Minecraft.getInstance().getUser();
        return getKingdom(player.getGameProfile()).map(Kingdom::getName);
    }

    public static List<Kingdom> getKingdoms() {
        return kingdoms;
    }

    public static void setKingdoms(List<Kingdom> kingdoms) {
        ClientKingdomData.kingdoms = kingdoms;
    }

    public static Optional<Kingdom> getKingdom(String name) {
        for (Kingdom kingdom : kingdoms) {
            if (kingdom.getName().equalsIgnoreCase(name)) {
                return Optional.of(kingdom);
            }
        }

        return Optional.empty();
    }

    public static Optional<Kingdom> getKingdom(GameProfile member) {
        for (Kingdom kingdom : kingdoms) {
            for (GameProfile kingdomMember : kingdom.getMembers()) {
                if (kingdomMember.equals(member)) {
                    return Optional.of(kingdom);
                }
            }
        }

        return Optional.empty();
    }

    public static boolean playerHasKingdom() {
        var player = getPlayer();

        if (player.isPresent()) {
            var kingdom = getKingdom(player.get().getGameProfile());

            return kingdom.isPresent();
        }

        return false;
    }

    public static Optional<Kingdom> getPlayerKingdom() {
        return getPlayer().flatMap(player -> getKingdom(player.getGameProfile()));
    }

    public static Optional<Player> getPlayer() {
        return Optional.ofNullable(Minecraft.getInstance().player);
    }

}
