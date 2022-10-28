package org.daan.kingdomclash.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector4f;
import com.simibubi.create.content.contraptions.goggles.GogglesItem;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.daan.kingdomclash.client.data.ClientKingdomData;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.block.mechanicalreinforcer.*;
import org.daan.kingdomclash.common.data.kingdom.Kingdom;

@Mod.EventBusSubscriber(modid = KingdomClash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientEvents {

    private static int breakTick = 0;

    @SubscribeEvent
    public static void reinforcerBreakEvent(PlayerEvent.BreakSpeed event) {
        float speedScalar = 0.5f;
        Level level = event.getPlayer().level;

        if (!level.isClientSide) {
            return;
        }

        for (Kingdom kingdom : ClientKingdomData.getKingdoms()) {
            var reinforcer = kingdom.getBlockPos(MechanicalReinforcer.class);

            if (reinforcer.isEmpty()) {
                return;
            }

            BlockPos breakPosition = event.getPos();
            var entity = level.getBlockEntity(reinforcer.get());

            if (entity instanceof MechanicalReinforcerTileEntity tileEntity) {
                float impact = Math.abs(tileEntity.calculateStressApplied() * tileEntity.getSpeed());
                boolean isRotating = Math.abs(tileEntity.getSpeed()) > 0;
                int range = (int) (Math.sqrt(impact) / 10d);

                boolean playerInKingdom = ClientKingdomData.getPlayerKingdom().map(
                        playerKingdom -> playerKingdom.equals(kingdom)
                ).orElse(false);
                playerInKingdom = false; // TODO REMOVE THIS LINE IN PRODUCTION

                if (isRotating && !playerInKingdom) {
                    DirectionalBlockArea area = new DirectionalBlockArea(reinforcer.get(), level, range);

                    if (area.isInArea(breakPosition)) {
                        float speed = event.getOriginalSpeed();
                        event.setNewSpeed(speed * speedScalar);
                        givePlayerBreakFeedback(event.getPlayer(), event.getPos());
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public static void renderReinforcedArea(RenderLevelLastEvent event) {
        var kingdom = ClientKingdomData.getPlayerKingdom();

        if (kingdom.isEmpty()) {
            return;
        }

        var reinforcer = kingdom.get().getBlockPos(MechanicalReinforcer.class);
        Level level = Minecraft.getInstance().level;

        if (reinforcer.isEmpty() || level == null) {
            return;
        }

        var entity = level.getBlockEntity(reinforcer.get());

        if (entity instanceof MechanicalReinforcerTileEntity tileEntity) {
            var area = tileEntity.getArea();

            if (area.isEmpty()) {
                return;
            }

            boolean wearingGoggles = GogglesItem.isWearingGoggles(Minecraft.getInstance().player);

            if (wearingGoggles) {
                renderCube(area.get(), event.getPoseStack(), new Vector4f(1f, 1f, 1f, 1f), true);
                renderBlock(reinforcer.get(), event.getPoseStack(), new Vector4f(0.85f, 0.7f, 0f, 0.8f));
            }
        }
    }

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
    public static void onRenderOverlay(RenderGameOverlayEvent.Pre event) {
        // disables f3 menu
//        if (event.getType() == RenderGameOverlayEvent.ElementType.DEBUG)
//        {
//            event.setCanceled(true);
//        }
    }

    private static void renderBlock(BlockPos blockPos, PoseStack pose, Vector4f color) {
        renderCube(
                new AABB(
                        blockPos.getX(),
                        blockPos.getY(),
                        blockPos.getZ(),
                        blockPos.getX() + 1,
                        blockPos.getY() + 1,
                        blockPos.getZ() + 1
                ),
                pose,
                color,
                false
        );
    }

    private static void renderCube(AABB cube, PoseStack pose, Vector4f color, boolean depthMask) {
        Camera camera = Minecraft.getInstance().gameRenderer.getMainCamera();
        pose.pushPose();

        Vec3 vec = camera.getPosition();
        pose.translate(-vec.x, -vec.y, -vec.z);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vc = buffer.getBuffer(RenderType.lines());

        RenderSystem.depthMask(depthMask);

        LevelRenderer.renderLineBox(
                pose,
                vc,
                cube,
                color.x(), color.y(), color.z(), color.w()
        );

        pose.popPose();
    }

    private static void givePlayerBreakFeedback(Player player, BlockPos blockpos) {
        breakTick++;

        if (breakTick >= 5) {
            breakTick = 0;
            player.playSound(SoundEvents.CHAIN_HIT, 0.6f, (float) ((Math.random() / 10f) + 0.9f));
//            spawnSoulParticles(player.level, blockpos);
        }
    }

}
