package org.daan.kingdomclash.common.mobs;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.daan.kingdomclash.common.KingdomClash;

public class KCMobs {

    private KCMobs() {}

    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(
            ForgeRegistries.ENTITIES,
            KingdomClash.MOD_ID
    );

    public static final RegistryObject<EntityType<ExampleEntity>> EXAMPLE_ENTITY = ENTITIES.register("example_entity",
            () -> EntityType.Builder.of(ExampleEntity::new, MobCategory.MISC)
                    .sized(0.8f, 0.6f)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(KingdomClash.MOD_ID, "example_entity").toString()));

}
