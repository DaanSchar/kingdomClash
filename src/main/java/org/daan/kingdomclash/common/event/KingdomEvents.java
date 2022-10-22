package org.daan.kingdomclash.common.event;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.*;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.daan.kingdomclash.common.*;
import org.daan.kingdomclash.common.block.mechanicalbeacon.MechanicalBeacon;
import org.daan.kingdomclash.common.data.kingdom.*;
import org.daan.kingdomclash.common.network.PacketHandler;
import org.daan.kingdomclash.common.network.packets.kingdom.*;
import org.daan.kingdomclash.index.KCBlocks;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = KingdomClash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KingdomEvents {

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        var world = event.getWorld();
        var block = event.getWorld().getBlockState(event.getPos()).getBlock();

        if (world.isClientSide() || !block.equals(KCBlocks.MECHANICAL_BEACON.get())) {
            return;
        }

        Player player = event.getPlayer();

        if (player.isCreative()) {
            KingdomManager.get(player.level).getKingdom(MechanicalBeacon.class, event.getPos()).ifPresent(
                    kingdom -> Messenger.sendClientSuccess(
                            player,
                            "Removed Transformer from " + kingdom.getName()
                    )
            );
        } else {
            Messenger.sendClientError(player, "You can not break a Transformer");
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void chat(ServerChatEvent event) {
        // TODO: Kingdom only chat 17-9-2022
    }

    @SubscribeEvent
    public static void renderName(PlayerEvent.NameFormat event) {
        if (!event.getPlayer().level.isClientSide()) {
            KingdomManager manager = KingdomManager.get(event.getPlayer().level);
            manager.getKingdom(event.getPlayer().getGameProfile()).ifPresent(
                    kingdom -> event.setDisplayname(formatPlayerName(
                                    event.getPlayer().getGameProfile().getName(),
                                    kingdom
                            )
                    )
            );
        }
    }

    @SubscribeEvent
    public static void renderTabName(PlayerEvent.TabListNameFormat event) {
        if (!event.getPlayer().level.isClientSide()) {
            KingdomManager manager = KingdomManager.get(event.getPlayer().level);
            manager.getKingdom(event.getPlayer().getGameProfile()).ifPresent(
                    kingdom -> event.setDisplayName(formatPlayerName(
                                    event.getPlayer().getGameProfile().getName(),
                                    kingdom
                            )
                    )
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.level.isClientSide()) {
                return;
            }

            KingdomManager manager = KingdomManager.get(player.level);
            manager.getKingdom(player.getGameProfile()).ifPresent(
                    kingdom -> {
                        if (kingdom.isDead()) {
                            manager.removeMember(kingdom, player.getGameProfile());
                        } else {
                            manager.decrementLives(kingdom, 1);
                            sendPacketToAllPlayers(player.getLevel(), new SPacketMemberDied(kingdom.getName()));
                        }
                    }
            );

        }
    }

    @SubscribeEvent
    public static void cancelSetSpawn(PlayerSetSpawnEvent event) {
        if (event.getPlayer().level.isClientSide()) {
            return;
        }

        getKingdom(event.getPlayer()).ifPresent(
                kingdom -> {
                    if (!kingdom.isDead()) {
                        event.setCanceled(true);
                    }
                }
        );
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getPlayer().level.isClientSide()) {
            return;
        }

        getKingdom(event.getPlayer()).ifPresent(
                kingdom -> {
                    if (!kingdom.isDead()) {
                        kingdom.getSpawnPoint().ifPresent(spawnPos -> {
                            event.getPlayer().teleportTo(spawnPos.x, spawnPos.y, spawnPos.z);
                        });
                    }
                }
        );
    }

    private static MutableComponent formatPlayerName(String playerName, Kingdom kingdom) {
        return new TextComponent("[").withStyle(ChatFormatting.WHITE)
                .append(new TextComponent(kingdom.getName()).withStyle(kingdom.getColor()))
                .append(new TextComponent("] ").withStyle(ChatFormatting.WHITE))
                .append(new TextComponent(playerName).withStyle(ChatFormatting.WHITE));
    }

    private static void sendPacketToAllPlayers(Level level, Object packet) {
        level.players().forEach(player -> {
            if (player instanceof ServerPlayer serverPlayer) {
                PacketHandler.sendToPlayer(packet, serverPlayer);
            }
        });
    }

    private static Optional<Kingdom> getKingdom(Player player) {
        KingdomManager manager = KingdomManager.get(player.level);
        return manager.getKingdom(player.getGameProfile());
    }

}
