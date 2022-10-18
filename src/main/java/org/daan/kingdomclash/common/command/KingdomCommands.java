package org.daan.kingdomclash.common.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.*;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.*;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.daan.kingdomclash.common.Messenger;
import org.daan.kingdomclash.common.data.kingdom.*;
import org.daan.kingdomclash.server.config.ServerConfig;
import org.slf4j.Logger;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class KingdomCommands {

    private static Logger log = LogUtils.getLogger();

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        // create [kingdom]
        dispatcher.register(
                Commands.literal("kingdom").requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("create").then(Commands.argument("name", StringArgumentType.string())
                                .executes(KingdomCommands::createNewKingdom)
                        ))
        );

        // remove [kingdom]
        dispatcher.register(
                Commands.literal("kingdom").requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("remove").then(Commands.argument("name", StringArgumentType.string())
                                .suggests(KingdomCommands::recommendKingdoms)
                                .executes(KingdomCommands::removeKingdom)
                        ))
        );

        // addmember [player] [kingdom]
        dispatcher.register(
                Commands.literal("kingdom").requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("addmember")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .suggests(KingdomCommands::recommendKingdoms)
                                                .executes(KingdomCommands::addMember)
                                        )
                                )
                        )
        );

        // removemember [player] [kingdom]
        dispatcher.register(
                Commands.literal("kingdom").requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("removemember")
                                .then(Commands.argument("player", GameProfileArgument.gameProfile())
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .suggests(KingdomCommands::recommendKingdoms)
                                                .executes(KingdomCommands::removePlayer)
                                        )
                                )
                        )
        );

        // list
        dispatcher.register(
                Commands.literal("kingdom").requires(sourceStack -> sourceStack.hasPermission(0))
                        .then(Commands.literal("list").executes(KingdomCommands::showList))
        );

        // members [kingdom]
        dispatcher.register(
                Commands.literal("kingdom").requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("members").then(Commands.argument("name", StringArgumentType.string())
                                .suggests(KingdomCommands::recommendKingdoms)
                                .executes(KingdomCommands::showMembers)
                        ))
        );

        // setspawn [kingdom]
        dispatcher.register(
                Commands.literal("kingdom").requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("setspawn").then(Commands.argument("name", StringArgumentType.string())
                                .suggests(KingdomCommands::recommendKingdoms)
                                .executes(KingdomCommands::setSpawn)
                        ))
        );

        // setLives [lives] [kingdom]
        dispatcher.register(
                Commands.literal("kingdom").requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("setlives")
                                .then(Commands.argument("lives", IntegerArgumentType.integer(0, ServerConfig.KINGDOM_MAX_LIVES.get()))
                                        .then(Commands.argument("name", StringArgumentType.string())
                                                .suggests(KingdomCommands::recommendKingdoms)
                                                .executes(KingdomCommands::setLives)
                                        )
                                )
                        )
        );

        // setcolor [kingdom] [color]
        dispatcher.register(
                Commands.literal("kingdom").requires(sourceStack -> sourceStack.hasPermission(3))
                        .then(Commands.literal("setcolor")
                                .then(Commands.argument("name", StringArgumentType.string())
                                        .suggests(KingdomCommands::recommendKingdoms)
                                        .then(Commands.argument("color", StringArgumentType.string())
                                                .suggests(KingdomCommands::recommendColors)
                                                .executes(KingdomCommands::setColor)
                                        )
                                )
                        )
        );

    }

    private static int createNewKingdom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        String kingdomName = StringArgumentType.getString(context, "name");
        var manager = KingdomManager.get(player.level);

        manager.getKingdom(kingdomName).ifPresentOrElse(
                kingdom -> Messenger.sendError(player, "Kingdom " + kingdomName + " already exists"),
                () -> {
                    manager.createNewKingdom(kingdomName);

                    manager.getKingdom(kingdomName).ifPresentOrElse(
                            kingdom -> Messenger.sendSuccess(player, "Successfully created Kingdom " + kingdomName),
                            () -> Messenger.sendError(player, "Could not create Kingdom " + kingdomName)
                    );
                }
        );

        updateAllPlayerClients(player.level);

        return 0;
    }

    private static int removeKingdom(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        String kingdomName = StringArgumentType.getString(context, "name");

        if (KingdomManager.get(player.level).removeKingdom(kingdomName)) {
            Messenger.sendSuccess(player, "Removed Kingdom " + kingdomName);
        } else {
            Messenger.sendError(player, "Kindom " + kingdomName + " was not found");
        }
        return 0;
    }

    private static int showList(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Map<String, Kingdom> kingdoms = KingdomManager.get(player.level).getKingdoms();

        Messenger.sendMessage(player, kingdoms.values().toString());
        return 0;
    }

    private static int showMembers(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String kingdomName = StringArgumentType.getString(context, "name");

        KingdomManager.get(player.level).getKingdom(kingdomName).ifPresentOrElse(
                kingdom -> {
                    StringBuilder msg = new StringBuilder("[");
                    for (GameProfile profile : kingdom.getMembers()) {
                        msg.append(profile.getName()).append(", ");
                    }
                    msg.append("]");
                    Messenger.sendMessage(player, msg.toString());
                },
                () -> Messenger.sendError(player, "Kingdom " + kingdomName + " Does not exist")
        );

        return 0;
    }

    private static int addMember(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player sourcePlayer = context.getSource().getPlayerOrException();
        String kingdomName = StringArgumentType.getString(context, "name");
        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");
        var manager = KingdomManager.get(sourcePlayer.level);

        for (GameProfile addedPlayer : profiles) {

            manager.getKingdom(kingdomName).ifPresentOrElse(kingdom -> {
                        String playerName = addedPlayer.getName();

                        if (kingdom.getMembers().contains(addedPlayer)) {
                            Messenger.sendError(sourcePlayer, "Player " + playerName + " is already in Kingdom " + kingdom.getName());
                        } else {
                            if (manager.addMember(kingdom, addedPlayer)) {
                                Messenger.sendSuccess(sourcePlayer, "Successfully added Player " + playerName + " to " + kingdom.getName());
                            } else {
                                manager.getKingdom(addedPlayer).ifPresent(playerKingdom -> {
                                    Messenger.sendError(sourcePlayer, playerName + " Is already in Kingdom " + playerKingdom.getName());
                                });
                            }
                        }
                    },
                    () -> Messenger.sendError(sourcePlayer, "Could not find Kingdom " + kingdomName));
        }

        updateAllPlayerClients(sourcePlayer.level);

        return 0;
    }

    private static int removePlayer(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        String kingdomName = StringArgumentType.getString(context, "name");
        Collection<GameProfile> profiles = GameProfileArgument.getGameProfiles(context, "player");
        var manager = KingdomManager.get(player.level);

        for (GameProfile profile : profiles) {
            manager.getKingdom(kingdomName).ifPresentOrElse(kingdom -> {
                        String playerName = profile.getName();

                        if (kingdom.getMembers().contains(profile)) {
                            if (manager.removeMember(kingdom, profile)) {
                                Messenger.sendSuccess(player, "Successfully removed Player " + playerName + " from Kingdom " + kingdom.getName());
                            } else {
                                Messenger.sendError(player, "Something went wrong using that command");
                            }
                        } else {
                            Messenger.sendError(player, "Player " + playerName + " Is not a member of Kingdom " + kingdom.getName());
                        }
                    },
                    () -> Messenger.sendError(player, "Kingdom " + kingdomName + " Does not exist")
            );
        }

        updateAllPlayerClients(player.level);

        return 0;
    }

    private static int setSpawn(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String kingdomName = StringArgumentType.getString(context, "name");

        KingdomManager.get(player.level).getKingdom(kingdomName).ifPresentOrElse(
                kingdom -> {
                    Vec3 playerPos = player.getPosition(0f);
                    kingdom.setSpawnPoint(playerPos);
                    Messenger.sendSuccess(player, String.format(
                            "Set spawn for %s To (%d, %d, %d)",
                            kingdom,
                            Math.round(playerPos.x()),
                            Math.round(playerPos.y()),
                            Math.round(playerPos.z()))
                    );
                },
                () -> Messenger.sendError(player, "Could not find kingdom " + kingdomName)
        );

        updateAllPlayerClients(player.level);

        return 0;
    }

    private static int setLives(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String kingdomName = StringArgumentType.getString(context, "name");
        int lives = IntegerArgumentType.getInteger(context, "lives");
        KingdomManager manager = KingdomManager.get(player.level);

        manager.getKingdom(kingdomName).ifPresentOrElse(
                kingdom -> {
                    manager.setLives(kingdom, lives);
                    Messenger.sendSuccess(player, "Set lives of " + kingdomName + " to " + lives);
                },
                () -> Messenger.sendError(player, "Could not find kingdom " + kingdomName)

        );

        updateAllPlayerClients(player.level);

        return 0;
    }

    private static int setColor(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String kingdomName = StringArgumentType.getString(context, "name");
        String colorName = StringArgumentType.getString(context, "color");
        ChatFormatting color = ChatFormatting.getByName(colorName.toUpperCase());
        KingdomManager manager = KingdomManager.get(player.level);

        if (color == null || !color.isColor()) {
            Messenger.sendError(player, colorName + " is not a color");
            return 0;
        }

        manager.getKingdom(kingdomName).ifPresentOrElse(
                kingdom -> {
                    manager.setColor(kingdom, color);
                    player.sendMessage(new TextComponent("[Kingdom Clash] Set color of " + kingdomName + " to ")
                            .withStyle(ChatFormatting.GREEN).append(new TextComponent(colorName).withStyle(color)),
                            player.getUUID()
                    );
                },
                () -> Messenger.sendError(player, "Could not find kingdom " + kingdomName)

        );

        updateAllPlayerClients(player.level);

        return 0;
    }


    private static CompletableFuture<Suggestions> recommendKingdoms(CommandContext<CommandSourceStack> context, SuggestionsBuilder suggestionsBuilder) {
        var manager = KingdomManager.get(context.getSource().getLevel());
        Collection<String> kingdomList = manager.getKingdoms().keySet();

        return SharedSuggestionProvider.suggest(kingdomList, suggestionsBuilder);
    }

    private static CompletableFuture<Suggestions> recommendColors(CommandContext<CommandSourceStack> context, SuggestionsBuilder suggestionsBuilder) {
        var formats = Arrays.stream(ChatFormatting.values()).toList();
        var formatNames = formats.stream()
                .filter(ChatFormatting::isColor)
                .map(ChatFormatting::getName)
                .collect(Collectors.toList());

        return SharedSuggestionProvider.suggest(formatNames, suggestionsBuilder);
    }

    private static void updateAllPlayerClients(Level level) {
        KingdomManager.get(level).updateAllPlayerClients(level);
    }

}
