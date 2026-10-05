package com.titanmodpack.titanores.energy;

import net.minecraftforge.energy.EnergyStorage;

// Forge Energy (FE/RF) buffer for machines. Accepts as much as the cable delivers (no input limit)
// and cannot be extracted by other blocks: the machine only consumes it.
public class ModEnergyStorage extends EnergyStorage {
    private final Runnable onChanged;

    public ModEnergyStorage(int capacity, Runnable onChanged) {
        super(capacity, Integer.MAX_VALUE, 0);
        this.onChanged = onChanged;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        int received = super.receiveEnergy(maxReceive, simulate);
        if (received > 0 && !simulate) {
            onChanged.run();
        }
        return received;
    }

    public void consume(int amount) {
        energy = Math.max(0, energy - amount);
        onChanged.run();
    }

    public void setEnergy(int amount) {
        energy = Math.max(0, Math.min(capacity, amount));
    }
}
