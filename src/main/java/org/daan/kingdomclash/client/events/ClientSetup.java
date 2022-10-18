package org.daan.kingdomclash.client.events;

import com.mojang.logging.LogUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.OverlayRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.daan.kingdomclash.client.KeyBindings;
import org.daan.kingdomclash.client.KeyInputHandler;
import org.daan.kingdomclash.client.gui.KingdomDataOverlay;
import org.daan.kingdomclash.common.KingdomClash;

@Mod.EventBusSubscriber(modid = KingdomClash.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {

    @SubscribeEvent
    public static void init(FMLClientSetupEvent event) {
        KeyBindings.init();
        MinecraftForge.EVENT_BUS.addListener(KeyInputHandler::onKeyInput);
        OverlayRegistry.registerOverlayAbove(ForgeIngameGui.HOTBAR_ELEMENT, "name", KingdomDataOverlay.HUD_DATA);
        LogUtils.getLogger().info("REGISTERED CLIENT SIDE STUFF");
    }


}
