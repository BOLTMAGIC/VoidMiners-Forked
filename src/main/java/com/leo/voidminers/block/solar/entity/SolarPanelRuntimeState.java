package com.leo.voidminers.block.solar.entity;

import net.minecraft.nbt.CompoundTag;

final class SolarPanelRuntimeState {

    private boolean blockedByDimension;
    private boolean active;
    private boolean working;
    private boolean hasSkyView;
    private float lastEfficiency;
    private long lastGeneration;
    private long lastEnergyStored;
    private long lastEnergyCapacity;

    boolean updateBlockedByDimension(boolean blocked) {
        boolean changed = this.blockedByDimension != blocked;
        this.blockedByDimension = blocked;
        return changed;
    }

    boolean isBlockedByDimension() {
        return blockedByDimension;
    }

    void setActive(boolean active) {
        this.active = active;
    }

    boolean isActive() {
        return active;
    }

    void setWorking(boolean working) {
        this.working = working;
    }

    boolean isWorking() {
        return working;
    }

    void setHasSkyView(boolean hasSkyView) {
        this.hasSkyView = hasSkyView;
    }

    boolean hasSkyView() {
        return hasSkyView;
    }

    void setLastEfficiency(float efficiency) {
        lastEfficiency = efficiency;
    }

    float lastEfficiency() {
        return lastEfficiency;
    }

    void setLastGeneration(long generation) {
        lastGeneration = generation;
    }

    long lastGeneration() {
        return lastGeneration;
    }

    void updateEnergySnapshot(long stored, long capacity) {
        lastEnergyStored = stored;
        lastEnergyCapacity = capacity;
    }

    long lastEnergyStored() {
        return lastEnergyStored;
    }

    long lastEnergyCapacity() {
        return lastEnergyCapacity;
    }

    void saveTo(CompoundTag tag) {
        tag.putBoolean("blockedByDimension", blockedByDimension);
        tag.putBoolean("active", active);
        tag.putBoolean("working", working);
        tag.putBoolean("hasSkyView", hasSkyView);
        tag.putFloat("lastEfficiency", lastEfficiency);
        tag.putLong("lastGeneration", lastGeneration);
        tag.putLong("lastEnergyStored", lastEnergyStored);
        tag.putLong("lastEnergyCapacity", lastEnergyCapacity);
    }

    void loadFrom(CompoundTag tag) {
        if (tag.contains("blockedByDimension")) {
            blockedByDimension = tag.getBoolean("blockedByDimension");
        }
        if (tag.contains("active")) {
            active = tag.getBoolean("active");
        }
        if (tag.contains("working")) {
            working = tag.getBoolean("working");
        }
        if (tag.contains("hasSkyView")) {
            hasSkyView = tag.getBoolean("hasSkyView");
        }
        if (tag.contains("lastEfficiency")) {
            lastEfficiency = tag.getFloat("lastEfficiency");
        }
        if (tag.contains("lastGeneration")) {
            lastGeneration = tag.getLong("lastGeneration");
        }
        if (tag.contains("lastEnergyStored")) {
            lastEnergyStored = tag.getLong("lastEnergyStored");
        }
        if (tag.contains("lastEnergyCapacity")) {
            lastEnergyCapacity = tag.getLong("lastEnergyCapacity");
        }
    }
}
