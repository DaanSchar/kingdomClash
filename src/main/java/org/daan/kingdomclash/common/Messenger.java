package org.daan.kingdomclash.common;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public class Messenger {

    private static Map<String, ChatFormatting> colors;

    private Messenger(){
    }

    public static void sendError(Player player, String message) {
        sendMessage(player, message, ChatFormatting.RED);
    }

    public static void sendSuccess(Player player, String message) {
        sendMessage(player, message, ChatFormatting.GREEN);
    }

    public static void sendMessage(Player player, String message) {
        sendMessage(player, message, ChatFormatting.WHITE);
    }

    public static void sendMessage(Player player, String message, ChatFormatting format) {
        player.sendMessage(
                new TextComponent("[Kingdom Clash] ").withStyle(ChatFormatting.GREEN).append(
                        new TextComponent(message).withStyle(format)),
                player.getUUID()
        );
    }

    public static void sendClientError(Player player, String message) {
        sendClientMessage(player, message, ChatFormatting.RED);
    }

    public static void sendClientSuccess(Player player, String message) {
        sendClientMessage(player, message, ChatFormatting.GREEN);
    }

    public static void sendClientMessage(Player player, String message) {
        sendClientMessage(player, message, ChatFormatting.WHITE);
    }

    public static void sendClientMessage(Player player, String message, ChatFormatting format) {
        player.displayClientMessage(
                new TextComponent(message).withStyle(format),
                true
        );
    }

    public static Map<String, ChatFormatting> getColors() {
        if (colors == null)  {
            colors = new HashMap<>();

            for (ChatFormatting formatting : ChatFormatting.values()) {
                colors.put(formatting.getName(), formatting);
            }
        }

        return colors;
    }

}
