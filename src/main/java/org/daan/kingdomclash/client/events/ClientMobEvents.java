package org.daan.kingdomclash.client.events;

import net.minecraft.client.renderer.entity.IronGolemRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.mobs.KCMobs;

@Mod.EventBusSubscriber(modid = KingdomClash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientMobEvents {

    @SubscribeEvent
    public static void render(EntityRenderersEvent.RegisterRenderers event) {
//        event.registerEntityRenderer(KCMobs.EXAMPLE_ENTITY.get(), IronGolemRenderer::new);
    }

}
