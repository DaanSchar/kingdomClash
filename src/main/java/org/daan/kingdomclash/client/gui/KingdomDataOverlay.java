package org.daan.kingdomclash.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import org.daan.kingdomclash.client.data.ClientKingdomData;
import org.daan.kingdomclash.common.data.kingdom.Kingdom;

import java.util.Comparator;
import java.util.List;

public class KingdomDataOverlay {

    public static final IIngameOverlay HUD_DATA = (gui, poseStack, partialTicks, width, height) -> {
        int dY = 0;

        if (ClientKingdomData.playerHasKingdom()) {
            List<Kingdom> kingdoms = ClientKingdomData.getKingdoms();
            kingdoms.sort(Comparator.comparingInt(Kingdom::getLives).reversed());
            for (Kingdom kingdom : kingdoms) {
                drawKingdom(gui, poseStack, kingdom, dY);
                dY += 20;
            }
        }
    };

    private static void drawKingdom(ForgeIngameGui gui, PoseStack poseStack, Kingdom kingdom, int deltaY) {
        String string = String.format("%s (%d/%d)", kingdom.getName(), kingdom.getLives(), kingdom.getMaxLives());
        gui.getFont().draw(poseStack, string, 10, 10 + deltaY, 0xbb919191);
    }


}
