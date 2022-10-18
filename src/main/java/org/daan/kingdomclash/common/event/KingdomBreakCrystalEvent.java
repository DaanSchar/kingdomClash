package org.daan.kingdomclash.common.event;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.daan.kingdomclash.common.*;
import org.daan.kingdomclash.common.block.ModBlocks;
import org.daan.kingdomclash.common.data.kingdom.*;
import org.daan.kingdomclash.common.network.PacketHandler;
import org.daan.kingdomclash.common.network.packets.kingdom.*;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = KingdomClash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KingdomBreakCrystalEvent {

    private static int counter;
    private static boolean activeExplosion = false;
    private static SPacketCrystalBreak lastBreakPacket;

    @SubscribeEvent
    public static void onBreakPowerCrystal(BlockEvent.BreakEvent event) {
        var world = event.getWorld();
        var block = event.getWorld().getBlockState(event.getPos()).getBlock();

        if (world.isClientSide() || !block.equals(ModBlocks.POWER_CRYSTAL_BLOCK.get())) {
            return;
        }

        event.setCanceled(true);
        Player player = event.getPlayer();

        getKingdom(player).ifPresentOrElse(
                kingdomOfPlayer -> handleBlockBreak(event, kingdomOfPlayer),
                () -> Messenger.sendClientError(player, "You are not in a kingdom")
        );
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world.isClientSide() || event.phase == TickEvent.Phase.START) {
            return;
        }

        counter++;

        int ticksPerExplosion = 5;
        int totalTicksDuration = 40;

        if (activeExplosion) {
            if (counter > totalTicksDuration) {
                toggleExplosion(false);
            } else if (counter % ticksPerExplosion == 0) {
                PacketHandler.sendPacketToAllPlayers(event.world, new SPacketExplosion(
                        lastBreakPacket.getKingdomNameUnderAttack(),
                        lastBreakPacket.getCrystalPos())
                );
//                event.world.explode(e, crystalPos.getX(), crystalPos.getY(), crystalPos.getZ(), 3f, Explosion.BlockInteraction.NONE);
            }
        }

        KingdomManager kingdomManager = KingdomManager.get(event.world);
        kingdomManager.tick(event.world);
    }

    private static void handleBlockBreak(BlockEvent.BreakEvent event, Kingdom kingdomOfPlayer) {
        Player player = event.getPlayer();
        KingdomManager manager = KingdomManager.get(player.level);
        manager.getKingdom(event.getPos()).ifPresentOrElse(
                kingdomOfBlock -> {
                    if (!kingdomOfBlock.equals(kingdomOfPlayer)) {
                        handleEnemyBreakCrystal(event, kingdomOfBlock);

                        if (kingdomOfBlock.isDead()) {
                            handleEnemyBreakCrystalToDeath(event, kingdomOfBlock);
                        }

                        applyDamageToMainHandItem(player);
                    } else {
                        handleMemberBreakCrystal(event, kingdomOfPlayer);
                    }
                },
                () -> event.setCanceled(false)
        );
    }

    private static void handleEnemyBreakCrystal(BlockEvent.BreakEvent event, Kingdom kingdom) {
        Player player = event.getPlayer();
        KingdomManager.get(player.level).decrementLives(kingdom, 1);
        Messenger.sendClientSuccess(player, kingdom.getName() + " (" + kingdom.getLives() + "/" + kingdom.getMaxLives() + ")");

        lastBreakPacket = new SPacketCrystalBreak(kingdom.getName(), event.getPos(), kingdom.getLives());
        PacketHandler.sendPacketToAllPlayers(player.getLevel(), lastBreakPacket);
    }

    private static void handleEnemyBreakCrystalToDeath(BlockEvent.BreakEvent event, Kingdom kingdom) {
        KingdomManager manager = KingdomManager.get(event.getPlayer().level);
        event.setCanceled(false);
        manager.setCrystalPosition(kingdom, null);
        toggleExplosion(true);
    }

    private static void handleMemberBreakCrystal(BlockEvent.BreakEvent event, Kingdom kingdom) {
        Player player = event.getPlayer();
        KingdomManager manager = KingdomManager.get(player.level);

        if (player.isCreative()) {
            Messenger.sendClientSuccess(player, "Removed crystal from kingdom");
            event.setCanceled(false);
            manager.setCrystalPosition(kingdom, null);
        } else {
            Messenger.sendClientError(player, "Can't break your own kingdom's block");
        }
    }

    private static void toggleExplosion(boolean enable) {
        counter = 0;
        activeExplosion = enable;
    }

    private static Optional<Kingdom> getKingdom(Player player) {
        KingdomManager manager = KingdomManager.get(player.level);
        return manager.getKingdom(player.getGameProfile());
    }

    private static void applyDamageToMainHandItem(Player player) {
        ItemStack mainHandItem = player.getMainHandItem();
        mainHandItem.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(EquipmentSlot.MAINHAND));
    }

}
