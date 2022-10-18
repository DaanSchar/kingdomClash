package org.daan.kingdomclash.common.data;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.data.kingdom.KingdomManager;

public class DataEvents {

    public static void onAttachCapabilitiesPlayer(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            if (!event.getObject().getCapability(PlayerDataProvider.PLAYER_DATA).isPresent()) {
                event.addCapability(
                        new ResourceLocation(KingdomClash.MOD_ID, "playerdata"),
                        new PlayerDataProvider()
                );
            }
        }
    }

    public static void onPlayerCloned(PlayerEvent.Clone event) {
        if (event.isWasDeath()) {
            event.getOriginal().getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(oldStore -> {
                event.getPlayer().getCapability(PlayerDataProvider.PLAYER_DATA).ifPresent(newStore -> {
                    newStore.copyFrom(oldStore);
                });
            });
        }
    }

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.register(PlayerData.class);
    }

    public static void onWorldTick(TickEvent.WorldTickEvent event) {
//        if (event.world.isClientSide()) {
//            return;
//        }
//        if (event.phase == TickEvent.Phase.START) {
//            return;
//        }
//        DataManager manager = DataManager.get(event.world);
//        manager.tick(event.world);
//        KingdomManager kingdomManager = KingdomManager.get(event.world);
//        kingdomManager.tick(event.world);
    }

}
