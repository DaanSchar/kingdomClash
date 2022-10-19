package org.daan.kingdomclash.common.event;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.daan.kingdomclash.common.*;
import org.daan.kingdomclash.common.block.powercrystal.PowerCrystal;
import org.daan.kingdomclash.common.data.kingdom.*;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = KingdomClash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class KingdomBlockEvents {

    @SubscribeEvent
    public static void onPlaceBlock(BlockEvent.EntityPlaceEvent event) {
        var world = event.getWorld();
        var block = event.getPlacedBlock().getBlock();

        if (world.isClientSide()) {
            return;
        }

        if (event.getEntity() instanceof Player player) {
            if (KingdomManager.isKingdomBlock(block)) {
                handleBlockPlace(block.getClass(), event, player);
            }
        }
    }

    @SubscribeEvent
    public static void onBreakBlock(BlockEvent.BreakEvent event) {
        var world = event.getWorld();
        var block = event.getWorld().getBlockState(event.getPos()).getBlock();

        if (world.isClientSide()) {
            return;
        }

        if (KingdomManager.isKingdomBlock(block) && !(block instanceof PowerCrystal)) {
            handleBlockBreak(event, block.getClass());
        }
    }


    private static void handleBlockPlace(Class<? extends Block> blockClass, BlockEvent.EntityPlaceEvent event, Player player) {
        String blockName = blockClass.getSimpleName();

        getKingdom(player).ifPresentOrElse(
                kingdom -> {
                    if (kingdom.getBlockPos(blockClass).isPresent()) {
                        Messenger.sendClientError(
                                player,
                                kingdom.getName() + " already has a " + blockName + ", Remove it first"
                        );
                        event.setCanceled(true);
                    } else {
                        var manager = KingdomManager.get(player.getLevel());
                        manager.setBlockPos(blockClass, kingdom, event.getPos());
                        Messenger.sendClientSuccess(player, "Added " + blockName + " to " + kingdom.getName());
                    }
                },
                () -> {
                    Messenger.sendClientError(
                            player,
                            "Cannot place a " + blockName + " When you are not in a kingdom"
                    );
                    event.setCanceled(true);
                }
        );
    }

    private static void handleBlockBreak(BlockEvent.BreakEvent event, Class<? extends Block> blockClass) {
        Player player = event.getPlayer();

        String blockName = blockClass.getSimpleName();

        if (player.isCreative()) {
            var manager = KingdomManager.get(player.level);

            manager.getKingdom(blockClass, event.getPos()).ifPresent(
                    kingdom -> {
                        manager.removeBlock(blockClass, kingdom);
                        Messenger.sendClientSuccess(
                                player,
                                "Removed " + blockName + " from " + kingdom.getName()
                        );
                    }
            );
        } else {
            Messenger.sendClientError(player, "You can not break a " + blockName);
            event.setCanceled(true);
        }
    }

    private static Optional<Kingdom> getKingdom(Player player) {
        KingdomManager manager = KingdomManager.get(player.level);
        return manager.getKingdom(player.getGameProfile());
    }

}
