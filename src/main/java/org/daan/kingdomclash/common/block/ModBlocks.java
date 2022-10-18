package org.daan.kingdomclash.common.block;

import com.simibubi.create.content.contraptions.base.KineticBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.block.custom.CustomRotationBlock;
import org.daan.kingdomclash.common.block.custom.PowerCrystalBlock;
import org.daan.kingdomclash.common.item.ModItems;

import java.util.function.Supplier;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, KingdomClash.MOD_ID);

    public static final RegistryObject<Block> POWER_CRYSTAL_BLOCK = registerBlock(
            "power_crystal_block",
            () -> new PowerCrystalBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5f, 6f)),
            CreativeModeTab.TAB_MISC
    );

    public static final RegistryObject<KineticBlock> CUSTOM_ROTATION_BLOCK = registerBlock(
            "custom_rotation_block",
            () -> new CustomRotationBlock(BlockBehaviour.Properties.of(Material.STONE, MaterialColor.STONE).requiresCorrectToolForDrops().strength(1.5f, 6f)),
            CreativeModeTab.TAB_MISC
    );

    private static <T extends Block>RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block>RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
