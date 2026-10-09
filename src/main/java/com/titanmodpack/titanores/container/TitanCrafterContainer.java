package com.titanmodpack.titanores.container;

import com.titanmodpack.titanores.block.TitanCrafterTileEntity;
import com.titanmodpack.titanores.init.ModBlocks;
import com.titanmodpack.titanores.init.ModContainers;
import com.titanmodpack.titanores.recipe.TitanCrafterRecipe;
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
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

// Slot positions match textures/gui/titan_crafter_gui.png (240x222).
public class TitanCrafterContainer extends Container {
    public static final int GRID_X = 8;
    public static final int GRID_Y = 18;
    public static final int OUTPUT_X = 197;
    public static final int OUTPUT_Y = 54;
    public static final int PLAYER_X = 8;
    public static final int PLAYER_Y = 140;
    public static final int HOTBAR_Y = 198;
    private static final int MACHINE_SLOTS = TitanCrafterTileEntity.SLOT_COUNT;

    private final IWorldPosCallable access;
    private final IIntArray data;

    // Client side: the tile entity is looked up from the position sent by the server; data is synced by the container.
    public TitanCrafterContainer(int id, PlayerInventory playerInventory, PacketBuffer buffer) {
        this(id, playerInventory, getTile(playerInventory, buffer), new IntArray(4));
    }

    public TitanCrafterContainer(int id, PlayerInventory playerInventory, TitanCrafterTileEntity tile, IIntArray data) {
        super(ModContainers.TITAN_CRAFTER.get(), id);
        this.access = IWorldPosCallable.create(tile.getLevel(), tile.getBlockPos());
        this.data = data;
        addDataSlots(data);

        ItemStackHandler items = tile.getItems();
        for (int row = 0; row < TitanCrafterRecipe.HEIGHT; row++) {
            for (int col = 0; col < TitanCrafterRecipe.WIDTH; col++) {
                addSlot(new SlotItemHandler(items, row * TitanCrafterRecipe.WIDTH + col, GRID_X + col * 18, GRID_Y + row * 18));
            }
        }
        addSlot(new SlotItemHandler(items, TitanCrafterTileEntity.OUTPUT_SLOT, OUTPUT_X, OUTPUT_Y) {
            @Override
            public boolean mayPlace(@Nonnull ItemStack stack) {
                return false;
            }
        });

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(playerInventory, col + row * 9 + 9, PLAYER_X + col * 18, PLAYER_Y + row * 18));
            }
        }
        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(playerInventory, col, PLAYER_X + col * 18, HOTBAR_Y));
        }
    }

    private static TitanCrafterTileEntity getTile(PlayerInventory playerInventory, PacketBuffer buffer) {
        TileEntity tile = playerInventory.player.level.getBlockEntity(buffer.readBlockPos());
        if (tile instanceof TitanCrafterTileEntity) {
            return (TitanCrafterTileEntity) tile;
        }
        throw new IllegalStateException("Titan Crafter tile entity not found");
    }

    public int getEnergy() {
        return (data.get(0) & 0xFFFF) | ((data.get(1) & 0xFFFF) << 16);
    }

    public int getEnergyCapacity() {
        return TitanCrafterTileEntity.ENERGY_CAPACITY;
    }

    public int getProgress() {
        return data.get(2);
    }

    public int getMaxProgress() {
        return data.get(3);
    }

    @Override
    public boolean stillValid(PlayerEntity player) {
        return stillValid(access, player, ModBlocks.TITAN_CRAFTER.get());
    }

    // Shift-click: machine -> player inventory; player inventory -> grid slots that already hold the item first,
    // then the first empty grid slot.
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
        } else if (!moveToGrid(stack)) {
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

    private boolean moveToGrid(ItemStack stack) {
        boolean moved = false;
        for (int i = 0; i < TitanCrafterTileEntity.GRID_SLOTS && !stack.isEmpty(); i++) {
            Slot target = slots.get(i);
            if (target.hasItem() && ItemHandlerHelper.canItemStacksStack(target.getItem(), stack)) {
                moved |= moveItemStackTo(stack, i, i + 1, false);
            }
        }
        if (!stack.isEmpty()) {
            for (int i = 0; i < TitanCrafterTileEntity.GRID_SLOTS; i++) {
                if (!slots.get(i).hasItem()) {
                    moved |= moveItemStackTo(stack, i, i + 1, false);
                    break;
                }
            }
        }
        return moved;
    }
}
