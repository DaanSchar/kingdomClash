package org.daan.kingdomclash.index;

import com.simibubi.create.content.contraptions.base.HalfShaftInstance;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import org.daan.kingdomclash.common.KingdomClash;
import org.daan.kingdomclash.common.block.mechanicalbeacon.MechanicalBeaconTileEntity;
import org.daan.kingdomclash.common.block.mechanicalreinforcer.MechanicalReinforcerTileEntity;

public class KCTileEntities {

    public static final BlockEntityEntry<MechanicalBeaconTileEntity> MECHANICAL_BEACON = KingdomClash.registrate()
            .tileEntity("mechanical_beacon", MechanicalBeaconTileEntity::new)
            .instance(() -> HalfShaftInstance::new)
            .validBlocks(KCBlocks.MECHANICAL_BEACON)
            .register();

    public static final BlockEntityEntry<MechanicalReinforcerTileEntity> MECHANICAL_REINFORCER = KingdomClash.registrate()
            .tileEntity("mechanical_beacon", MechanicalReinforcerTileEntity::new)
            .instance(() -> HalfShaftInstance::new)
            .validBlocks(KCBlocks.MECHANICAL_REINFORCER)
            .register();

    public static void register() {}
}
