package org.daan.kingdomclash.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

public class RenderLines  {

    public static void drawLineBox(PoseStack matrixStack, AABB aabb, float r, float g, float b, float a) {
        Entity camEntity = Minecraft.getInstance().getCameraEntity();
        double d0 = camEntity.getX();
        double d1 = camEntity.getY() + camEntity.getEyeHeight();
        double d2 = camEntity.getZ();

        RenderSystem.depthMask(false); // disable showing lines through blocks
        VertexConsumer vertexConsumer = Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.lines());

        matrixStack.pushPose();
        matrixStack.translate(-d0, -d1, -d2); // because we start at 0,0,0 relative to camera
        LevelRenderer.renderLineBox(matrixStack, vertexConsumer, aabb, r, g, b, a);
        matrixStack.popPose();
    }

}