package org.daan.kingdomclash.client;

import net.minecraftforge.client.gui.IIngameOverlay;

public class DataOverlay {

    public static final IIngameOverlay HUD_DATA = (gui, poseStack, partialTicks, width, height) -> {
        String toDisplay = ClientDataData.getPlayerData() + " / " + ClientDataData.getChunkData();
        gui.getFont().draw(poseStack, toDisplay, 10, 10, 0xffffffff);
    };

}
