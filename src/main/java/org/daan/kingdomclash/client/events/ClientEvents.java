package org.daan.kingdomclash.client.events;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderNameplateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.daan.kingdomclash.client.data.ClientKingdomData;
import org.daan.kingdomclash.common.KingdomClash;

@Mod.EventBusSubscriber(modid = KingdomClash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void namePlate(RenderNameplateEvent event) {
        if (event.getEntity() instanceof Player player) {
            ClientKingdomData.getKingdom(player.getGameProfile()).ifPresent(
                    kingdom -> event.setContent(new TextComponent("[").withStyle(ChatFormatting.WHITE)
                                    .append(new TextComponent(kingdom.getName()).withStyle(kingdom.getColor()))
                                    .append(new TextComponent("] ").withStyle(ChatFormatting.WHITE))
                                    .append(new TextComponent(player.getGameProfile().getName())).withStyle(ChatFormatting.WHITE)
                    )
            );
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event)
    {
        // disables f3 menu
//        if (event.getType() == RenderGameOverlayEvent.ElementType.DEBUG)
//        {
//            event.setCanceled(true);
//        }
    }

}
