package com.leo.voidminers.energy;

import net.minecraftforge.energy.EnergyStorage;

public class ModEnergyStorage extends EnergyStorage {
    private long longCapacity;
    private long longEnergy;

    public ModEnergyStorage(long capacity) {
        super(capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)capacity);
        this.longCapacity = capacity;
        this.longEnergy = 0;
    }

    public ModEnergyStorage(long capacity, long maxTransfer) {
        super(capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)capacity,
              maxTransfer > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxTransfer);
        this.longCapacity = capacity;
        this.longEnergy = 0;
    }

    public ModEnergyStorage(long capacity, long maxReceive, long maxExtract) {
        super(capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)capacity,
              maxReceive > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxReceive,
              maxExtract > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxExtract);
        this.longCapacity = capacity;
        this.longEnergy = 0;
    }

    public ModEnergyStorage(long capacity, long maxReceive, long maxExtract, long energy) {
        super(capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)capacity,
              maxReceive > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxReceive,
              maxExtract > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)maxExtract,
              energy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)energy);
        this.longCapacity = capacity;
        this.longEnergy = energy;
    }

    // Legacy int constructors for backward compatibility
    public ModEnergyStorage(int capacity) {
        this((long)capacity);
    }

    public ModEnergyStorage(int capacity, int maxTransfer) {
        this((long)capacity, (long)maxTransfer);
    }

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract) {
        this((long)capacity, (long)maxReceive, (long)maxExtract);
    }

    public ModEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) {
        this((long)capacity, (long)maxReceive, (long)maxExtract, (long)energy);
    }

    @Override
    public int getEnergyStored() {
        return longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)longEnergy;
    }

    @Override
    public int getMaxEnergyStored() {
        return longCapacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)longCapacity;
    }

    // New long-based methods
    public long getLongEnergyStored() {
        return longEnergy;
    }

    public long getLongMaxEnergyStored() {
        return longCapacity;
    }

    public void setEnergy(long energy) {
        this.longEnergy = Math.max(0, Math.min(longCapacity, energy));
        this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)longEnergy;
    }

    // Legacy int method for backward compatibility
    public void setEnergy(int energy) {
        setEnergy((long)energy);
    }

    public void removeEnergy(long remove) {
        this.longEnergy = Math.max(0, this.longEnergy - remove);
        this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)longEnergy;
    }

    // Legacy int method for backward compatibility
    public void removeEnergy(int remove) {
        removeEnergy((long)remove);
    }

    public void addEnergy(long add) {
        this.longEnergy = Math.min(this.longCapacity, this.longEnergy + add);
        this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int)longEnergy;
    }

    // Legacy int method for backward compatibility
    public void addEnergy(int add) {
        addEnergy((long)add);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) return 0;

        int receivable = Math.min(maxReceive, this.maxReceive);
        long longReceivable = Math.min((long) receivable, longCapacity - longEnergy);

        if (!simulate && longReceivable > 0) {
            longEnergy += longReceivable;
            this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) longEnergy;
        }
        return (int) longReceivable;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) return 0;

        int extractable = Math.min(maxExtract, this.maxExtract);
        long longExtractable = Math.min((long) extractable, longEnergy);

        if (!simulate && longExtractable > 0) {
            longEnergy -= longExtractable;
            this.energy = longEnergy > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) longEnergy;
        }
        return (int) longExtractable;
    }
}
