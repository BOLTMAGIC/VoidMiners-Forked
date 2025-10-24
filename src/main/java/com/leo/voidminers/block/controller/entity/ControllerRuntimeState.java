package com.leo.voidminers.block.controller.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

final class ControllerRuntimeState {

    private boolean canProduceCache = true;
    private boolean inventoryChanged = true;
    private boolean blockedByDimension = false;
    private boolean lastInventoryFull = false;
    private boolean lastEnergyInsufficient = false;
    private boolean lastEnergyDemandTooHigh = false;
    private boolean lastOutputBlocked = false;
    private int lastEnergyDemand = 0;
    private long lastEnergyStored = 0;
    private long lastEnergyCapacity = 0;
    private int lastRecipeCount = 0;
    private ItemStack lastBlockedStack = ItemStack.EMPTY;
    private OutputBlockReason lastOutputBlockReason = OutputBlockReason.NONE;

    boolean updateBlockedByDimension(boolean blocked) {
        boolean changed = this.blockedByDimension != blocked;
        this.blockedByDimension = blocked;
        return changed;
    }

    boolean isBlockedByDimension() {
        return blockedByDimension;
    }

    boolean isInventoryChanged() {
        return inventoryChanged;
    }

    void markInventoryChanged() {
        inventoryChanged = true;
    }

    void setInventoryChanged(boolean value) {
        inventoryChanged = value;
    }

    void clearInventoryChangedFlag() {
        inventoryChanged = false;
    }

    boolean canProduceCache() {
        return canProduceCache;
    }

    void setCanProduceCache(boolean value) {
        canProduceCache = value;
    }

    boolean isLastInventoryFull() {
        return lastInventoryFull;
    }

    void setLastInventoryFull(boolean value) {
        lastInventoryFull = value;
    }

    boolean isLastEnergyInsufficient() {
        return lastEnergyInsufficient;
    }

    void setLastEnergyInsufficient(boolean value) {
        lastEnergyInsufficient = value;
    }

    boolean isLastEnergyDemandTooHigh() {
        return lastEnergyDemandTooHigh;
    }

    void setLastEnergyDemandTooHigh(boolean value) {
        lastEnergyDemandTooHigh = value;
    }

    boolean isLastOutputBlocked() {
        return lastOutputBlocked;
    }

    void setLastOutputBlocked(boolean value) {
        lastOutputBlocked = value;
    }

    int getLastEnergyDemand() {
        return lastEnergyDemand;
    }

    void setLastEnergyDemand(int value) {
        lastEnergyDemand = value;
    }

    long getLastEnergyStored() {
        return lastEnergyStored;
    }

    void setLastEnergyStored(long value) {
        lastEnergyStored = value;
    }

    long getLastEnergyCapacity() {
        return lastEnergyCapacity;
    }

    void setLastEnergyCapacity(long value) {
        lastEnergyCapacity = value;
    }

    int getLastRecipeCount() {
        return lastRecipeCount;
    }

    void setLastRecipeCount(int value) {
        lastRecipeCount = value;
    }

    ItemStack getLastBlockedStack() {
        return lastBlockedStack;
    }

    void setLastBlockedStack(ItemStack value) {
        lastBlockedStack = value.isEmpty() ? ItemStack.EMPTY : value.copy();
    }

    void resetBlockedStack() {
        lastBlockedStack = ItemStack.EMPTY;
    }

    OutputBlockReason getLastOutputBlockReason() {
        return lastOutputBlockReason;
    }

    void setLastOutputBlockReason(OutputBlockReason value) {
        lastOutputBlockReason = value;
    }

    void resetCycleSnapshot(long stored, long capacity) {
        lastInventoryFull = false;
        lastOutputBlocked = false;
        lastEnergyDemandTooHigh = false;
        lastEnergyInsufficient = false;
        lastEnergyDemand = 0;
        lastEnergyStored = stored;
        lastEnergyCapacity = capacity;
    }

    void updateEnergySnapshot(int demand, long stored, long capacity) {
        lastEnergyDemand = demand;
        lastEnergyStored = stored;
        lastEnergyCapacity = capacity;
    }

    void saveTo(CompoundTag tag) {
        tag.putBoolean("canProduceCache", canProduceCache);
        tag.putBoolean("blockedByDimension", blockedByDimension);
        tag.putBoolean("inventoryChanged", inventoryChanged);
        tag.putBoolean("lastInventoryFull", lastInventoryFull);
        tag.putBoolean("lastEnergyInsufficient", lastEnergyInsufficient);
        tag.putBoolean("lastEnergyDemandTooHigh", lastEnergyDemandTooHigh);
        tag.putBoolean("lastOutputBlocked", lastOutputBlocked);
        tag.putInt("lastEnergyDemand", lastEnergyDemand);
        tag.putLong("lastEnergyStored", lastEnergyStored);
        tag.putLong("lastEnergyCapacity", lastEnergyCapacity);
        tag.putInt("lastRecipeCount", lastRecipeCount);
        tag.putInt("lastOutputBlockReason", lastOutputBlockReason.ordinal());
        if (!lastBlockedStack.isEmpty()) {
            tag.put("lastBlockedStack", lastBlockedStack.save(new CompoundTag()));
        }
    }

    void loadFrom(CompoundTag tag) {
        if (tag.contains("canProduceCache")) {
            canProduceCache = tag.getBoolean("canProduceCache");
        }
        if (tag.contains("blockedByDimension")) {
            blockedByDimension = tag.getBoolean("blockedByDimension");
        }
        if (tag.contains("inventoryChanged")) {
            inventoryChanged = tag.getBoolean("inventoryChanged");
        }
        if (tag.contains("lastInventoryFull")) {
            lastInventoryFull = tag.getBoolean("lastInventoryFull");
        }
        if (tag.contains("lastEnergyInsufficient")) {
            lastEnergyInsufficient = tag.getBoolean("lastEnergyInsufficient");
        }
        if (tag.contains("lastEnergyDemandTooHigh")) {
            lastEnergyDemandTooHigh = tag.getBoolean("lastEnergyDemandTooHigh");
        }
        if (tag.contains("lastOutputBlocked")) {
            lastOutputBlocked = tag.getBoolean("lastOutputBlocked");
        }
        if (tag.contains("lastEnergyDemand")) {
            lastEnergyDemand = tag.getInt("lastEnergyDemand");
        }
        if (tag.contains("lastEnergyStored")) {
            lastEnergyStored = tag.getLong("lastEnergyStored");
        }
        if (tag.contains("lastEnergyCapacity")) {
            lastEnergyCapacity = tag.getLong("lastEnergyCapacity");
        }
        if (tag.contains("lastRecipeCount")) {
            lastRecipeCount = tag.getInt("lastRecipeCount");
        }
        if (tag.contains("lastOutputBlockReason")) {
            int ordinal = tag.getInt("lastOutputBlockReason");
            if (ordinal >= 0 && ordinal < OutputBlockReason.values().length) {
                lastOutputBlockReason = OutputBlockReason.values()[ordinal];
            } else {
                lastOutputBlockReason = OutputBlockReason.NONE;
            }
        } else {
            lastOutputBlockReason = OutputBlockReason.NONE;
        }
        if (tag.contains("lastBlockedStack")) {
            lastBlockedStack = ItemStack.of(tag.getCompound("lastBlockedStack"));
        } else {
            lastBlockedStack = ItemStack.EMPTY;
        }
    }
}
