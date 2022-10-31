package org.daan.kingdomclash.common.event;

import com.mojang.logging.LogUtils;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.mobs.ExampleEntity;
import org.daan.kingdomclash.common.mobs.KCMobs;

@Mod.EventBusSubscriber(modid = KingdomClash.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class KingdomMobEvents {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        LogUtils.getLogger().info("BRUUUUUUH");
        event.put(KCMobs.EXAMPLE_ENTITY.get(), ExampleEntity.createAttributes().build());
    }

}
