package org.daan.kingdomclash.common.event;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.command.KingdomCommands;

@Mod.EventBusSubscriber(modid = KingdomClash.MOD_ID)

public class RegisterCommands {

    @SubscribeEvent
    public static void register(RegisterCommandsEvent event) {
        KingdomCommands.register(event.getDispatcher());
    }

}
