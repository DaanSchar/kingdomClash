package org.daan.kingdomclash.common.data;

import net.minecraft.nbt.CompoundTag;

public class PlayerData {

    private int data;

    public int getData() {
        return data;
    }

    public void setData(int data) {
        this.data = data;
    }

    public void addData(int data) {
        this.data += data;
    }

    public void copyFrom(PlayerData source) {
        data = source.data;
    }

    public void saveNBTData(CompoundTag compoundTag) {
        compoundTag.putInt("data", data);
    }

    public void loadNBTData(CompoundTag compoundTag) {
        data = compoundTag.getInt("data");
    }
}
