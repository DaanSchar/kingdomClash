package org.daan.kingdomclash.index;

import com.simibubi.create.content.contraptions.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.block.transformer.MechanicalBeaconTileEntity;

public class KCTileEntities {

    public static final BlockEntityEntry<MechanicalBeaconTileEntity> MECHANICAL_BEACON = KingdomClash.registrate()
            .tileEntity("mechanical_beacon", MechanicalBeaconTileEntity::new)
            .instance(() -> HalfShaftInstance::new)
            .validBlocks(KCBlocks.MECHANICAL_BEACON)
            .register();

    public static void register() {}
}
