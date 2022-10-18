package org.daan.kingdomclash.client;

import net.minecraftforge.client.event.InputEvent;
import org.daan.kingdomclash.common.network.PacketHandler;
import org.daan.kingdomclash.common.network.PacketExample;

public class KeyInputHandler {

    public static void onKeyInput(InputEvent.KeyInputEvent event) {
        if (KeyBindings.exampleKeyMapping.consumeClick()) {
            PacketHandler.sendToServer(new PacketExample());
        }
    }

}
