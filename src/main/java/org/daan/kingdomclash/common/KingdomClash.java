package org.daan.kingdomclash.common;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.daan.kingdomclash.common.mobs.KCMobs;
import org.daan.kingdomclash.index.KCBlockProperties;
import org.daan.kingdomclash.index.KCTileEntities;
import org.daan.kingdomclash.common.data.DataEvents;
import org.daan.kingdomclash.common.network.PacketHandler;
import org.daan.kingdomclash.index.KCBlocks;
import org.daan.kingdomclash.server.config.ServerConfig;
import org.slf4j.Logger;

import java.util.stream.Collectors;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("kingdomclash")
public class KingdomClash
{
    public static final String MOD_ID = "kingdomclash";

    public static final Logger LOGGER = LogUtils.getLogger();

    private static final NonNullSupplier<CreateRegistrate> registrate = CreateRegistrate.lazy(MOD_ID);

    public KingdomClash()
    {
        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();

//        ModBlocks.register(eventBus);
//        ModItems.register(eventBus);
//        ModEffects.register(eventBus);
        KCMobs.ENTITIES.register(eventBus);

        eventBus.addListener(this::setup);
        eventBus.addListener(this::enqueueIMC);
        eventBus.addListener(this::processIMC);

        IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addGenericListener(Entity.class, DataEvents::onAttachCapabilitiesPlayer);
        bus.addListener(DataEvents::onPlayerCloned);
        bus.addListener(DataEvents::onRegisterCapabilities);
        bus.addListener(DataEvents::onWorldTick);

        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfig.SPEC, "example-mod-server.toml");

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
        KCBlockProperties.register();
        KCBlocks.register();
        KCTileEntities.register();
    }

    private void setup(final FMLCommonSetupEvent event)
    {
        PacketHandler.register();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
        InterModComms.sendTo(MOD_ID, "helloworld", () -> { LOGGER.info("Hello world from the MDK"); return "Hello world";});
    }

    private void processIMC(final InterModProcessEvent event)
    {
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m->m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        LOGGER.info("HELLO from server starting");
    }

    public static CreateRegistrate registrate() {
        LogUtils.getLogger().info("BRO WE ARE DOING THIS REGISTRATE THING");
        return registrate.get();
    }

}
