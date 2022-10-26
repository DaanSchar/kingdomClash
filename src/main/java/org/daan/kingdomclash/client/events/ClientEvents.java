package org.daan.kingdomclash.client.events;

import net.minecraft.ChatFormatting;
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

        if (!event.getPlayer().level.isClientSide) {
            return;
        }

        for (Kingdom kingdom : ClientKingdomData.getKingdoms()) {
            var reinforcer = kingdom.getBlockPos(MechanicalReinforcer.class);

            if (reinforcer.isEmpty()) {
                return;
            }

            Level level = event.getPlayer().level;
            BlockPos breakPosition = event.getPos();
            var entity = level.getBlockEntity(reinforcer.get());

            if (entity instanceof MechanicalReinforcerTileEntity tileEntity) {
                float impact = Math.abs(tileEntity.calculateStressApplied() * tileEntity.getSpeed());
                boolean isRotating = tileEntity.getSpeed() > 0;
                int range = (int) (Math.sqrt(impact) / 10d);

                boolean playerInKingdom = ClientKingdomData.getPlayerKingdom().map(
                        playerKingdom -> playerKingdom.equals(kingdom)
                ).orElse(false);

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
    public static void bruh(RenderLevelLastEvent event) {
        ClientKingdomData.getPlayerKingdom().ifPresent(
                kingdom -> {
                    var reinforcer = kingdom.getBlockPos(MechanicalReinforcer.class);

                    if (reinforcer.isEmpty()) {
                        return;
                    }

                    Vec3 pos = new Vec3(
                            reinforcer.get().getX(),
                            reinforcer.get().getY(),
                            reinforcer.get().getZ()
                    );

                    RenderLines.drawLineBox(
                            event.getPoseStack(),
                            AABB.unitCubeFromLowerCorner(pos),
                            1, 1, 1, 1
                    );
                }
        );
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

    private static void givePlayerBreakFeedback(Player player, BlockPos blockpos) {
        breakTick++;

        if (breakTick >= 5) {
            breakTick = 0;
            player.playSound(SoundEvents.CHAIN_HIT, 0.6f, (float) ((Math.random() / 10f) + 0.9f));
//            spawnSoulParticles(player.level, blockpos);
        }
    }

}
