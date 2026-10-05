package com.titanmodpack.titanores.container;

import com.titanmodpack.titanores.block.TitanFactoryTileEntity;
import com.titanmodpack.titanores.init.ModBlocks;
import com.titanmodpack.titanores.init.ModContainers;
import com.titanmodpack.titanores.init.ModItems;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

// Slot positions match textures/gui/titan_factory.png.
public class TitanFactoryContainer extends Container {
    private static final int[][] INPUT_POSITIONS = {{11, 11}, {11, 34}, {11, 56}, {128, 11}, {128, 34}, {128, 56}};
    private static final int OUTPUT_X = 69;
    private static final int OUTPUT_Y = 34;
    // The upgrade slot lives in a tab outside the right edge of the GUI, next to the energy bar (drawn by TitanFactoryScreen).
    public static final int UPGRADE_X = 180;
    public static final int UPGRADE_Y = 28;
    private static final int UPGRADE_START = TitanFactoryTileEntity.SLOT_COUNT;
    private static final int MACHINE_SLOTS = UPGRADE_START + TitanFactoryTileEntity.UPGRADE_SLOTS;

    private final TitanFactoryTileEntity tile;
    private final IWorldPosCallable access;
    private final IIntArray data;

    // Client side: the tile entity is looked up from the position sent by the server; data is synced by the container.
    public TitanFactoryContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        this(id, playerInventory, getTile(playerInventory, buffer), new IntArray(4));
    }

    public TitanFactoryContainer(int id, PlayerInventory playerInventory, TitanFactoryTileEntity tile, IIntArray data) {
        super(ModContainers.TITAN_FACTORY.get(), id);
        this.tile = tile;
        this.access = IWorldPosCallable.create(tile.getLevel(), tile.getBlockPos());
        this.data = data;
        addDataSlots(data);

        ItemStackHandler items = tile.getItems();
        for (int i = 0; i < INPUT_POSITIONS.length; i++) {
            addSlot(new SlotItemHandler(items, i, INPUT_POSITIONS[i][0], INPUT_POSITIONS[i][1]));
        }
        addSlot(new SlotItemHandler(items, TitanFactoryTileEntity.OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return false;
            }
        });

        ItemStackHandler upgrades = tile.getUpgrades();
        for (int i = 0; i < upgrades.getSlots(); i++) {
            addSlot(new SlotItemHandler(upgrades, i, UPGRADE_X, UPGRADE_Y + i * 18));
        }

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, 8 + col * 18, 84 + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, 8 + col * 18, 142));
        }
    }

    private static TitanFactoryTileEntity getTile(PlayerInventory playerInventory, PacketBuffer buffer) {
        TileEntity tile = playerInventory.player.level.getBlockEntity(buffer.readBlockPos());
        if (tile instanceof TitanFactoryTileEntity) {
            return (TitanFactoryTileEntity) tile;
        }
        throw new IllegalStateException("Titan Factory tile entity not found");
    }

    public int getEnergy() {
        return (data.get(0) & 0xFFFF) | ((data.get(1) & 0xFFFF) << 16);
    }

    public int getEnergyCapacity() {
        return TitanFactoryTileEntity.ENERGY_CAPACITY;
    }

    public int getProgress() {
        return data.get(2);
    }

    public int getInstalledUpgrades() {
        return Math.min(TitanFactoryTileEntity.MAX_UPGRADES, slots.get(UPGRADE_START).getItem().getCount());
    }

    public int getMaxProgress() {
        return data.get(3);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(access, player, ModBlocks.TITAN_FACTORY.get());
    }

    // Shift-click: machine -> player inventory, player inventory -> input slots.
    @Override
    public ItemStack quickMoveStack(PlayerEntity player, int index) {
        Slot slot = slots.get(index);
        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }
        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();
        if (index < MACHINE_SLOTS) {
            if (!moveItemStackTo(stack, MACHINE_SLOTS, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (stack.getItem() == ModItems.SPEED_UPGRADE.get()) {
            if (!moveItemStackTo(stack, UPGRADE_START, MACHINE_SLOTS, false)) {
                return ItemStack.EMPTY;
            }
        } else if (!moveItemStackTo(stack, 0, TitanFactoryTileEntity.INPUT_SLOTS, false)) {
            return ItemStack.EMPTY;
        }
        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }
        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }
        slot.onTake(player, stack);
        return original;
    }
}
