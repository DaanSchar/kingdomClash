package org.daan.kingdomclash.index;

import com.simibubi.create.AllTags;
import com.simibubi.create.Create;
import com.simibubi.create.content.AllSections;
import com.simibubi.create.foundation.block.BlockStressDefaults;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.util.entry.BlockEntry;
import net.minecraft.world.item.CreativeModeTab;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.block.mechanicalreinforcer.MechanicalReinforcer;
import org.daan.kingdomclash.common.block.powercrystal.PowerCrystal;
import org.daan.kingdomclash.common.block.mechanicalbeacon.MechanicalBeacon;
import org.daan.kingdomclash.common.data.kingdom.KingdomManager;

import static com.simibubi.create.foundation.data.ModelGen.customItemModel;

public class KCBlocks {

    private static final CreateRegistrate REGISTRATE = KingdomClash
            .registrate()
            .creativeModeTab(() -> CreativeModeTab.TAB_MISC);

    public static final BlockEntry<MechanicalBeacon> MECHANICAL_BEACON = REGISTRATE.block("mechanical_beacon", MechanicalBeacon::new)
            .initialProperties(SharedProperties::stone)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .transform(BlockStressDefaults.setImpact(512))
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<PowerCrystal> POWER_CRYSTAL = REGISTRATE.block("power_crystal_block", PowerCrystal::new)
            .initialProperties(SharedProperties::stone)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static final BlockEntry<MechanicalReinforcer> MECHANICAL_REINFORCER = REGISTRATE.block("mechanical_reinforcer", MechanicalReinforcer::new)
            .initialProperties(SharedProperties::stone)
            .tag(AllTags.AllBlockTags.SAFE_NBT.tag)
            .item()
            .transform(customItemModel())
            .register();

    public static void register() {
        Create.registrate().addToSection(MECHANICAL_BEACON, AllSections.KINETICS);
        Create.registrate().addToSection(MECHANICAL_REINFORCER, AllSections.KINETICS);

        KingdomManager.register(MechanicalBeacon.class);
        KingdomManager.register(PowerCrystal.class);
        KingdomManager.register(MechanicalReinforcer.class);
    }
}
