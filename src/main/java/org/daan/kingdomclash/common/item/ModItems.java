package org.daan.kingdomclash.common.item;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.daan.kingdomclash.common.KingdomClash;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, KingdomClash.MOD_ID);

    public static final RegistryObject<Item> CRYSTALLIZED_DUST = ITEMS.register(
            "crystallized_dust",
            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_MISC))
    );

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}
