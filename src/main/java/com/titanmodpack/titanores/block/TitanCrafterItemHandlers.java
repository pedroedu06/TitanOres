package com.titanmodpack.titanores.block;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

// Side-specific item access for the Titan Crafter (hoppers, pipes, AE2/RS interfaces).
public final class TitanCrafterItemHandlers {
    private TitanCrafterItemHandlers() {
    }

    // Top: insert only, "template" style. Recipes use fixed positions, so the player places one set of the recipe and
    // automation only tops up grid slots that already hold the same item (the one with the fewest items first).
    // Items that are not in the grid are refused, so the pattern is never broken.
    public static class TopInput implements IItemHandler {
        private final ItemStackHandler items;

        public TopInput(ItemStackHandler items) {
            this.items = items;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return ItemStack.EMPTY;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (stack.isEmpty()) {
                return stack;
            }
            int limit = Math.min(64, stack.getMaxStackSize());
            if (simulate) {
                int space = 0;
                for (int s = 0; s < TitanCrafterTileEntity.GRID_SLOTS; s++) {
                    ItemStack current = items.getStackInSlot(s);
                    if (!current.isEmpty() && ItemHandlerHelper.canItemStacksStack(current, stack)) {
                        space += limit - current.getCount();
                    }
                }
                return ItemHandlerHelper.copyStackWithSize(stack, Math.max(0, stack.getCount() - space));
            }
            ItemStack remaining = stack.copy();
            while (!remaining.isEmpty()) {
                int best = -1;
                for (int s = 0; s < TitanCrafterTileEntity.GRID_SLOTS; s++) {
                    ItemStack current = items.getStackInSlot(s);
                    if (!current.isEmpty() && ItemHandlerHelper.canItemStacksStack(current, remaining)
                            && current.getCount() < limit
                            && (best == -1 || current.getCount() < items.getStackInSlot(best).getCount())) {
                        best = s;
                    }
                }
                if (best == -1) {
                    break;
                }
                ItemStack leftover = items.insertItem(best, remaining.split(1), false);
                if (!leftover.isEmpty()) {
                    remaining.grow(leftover.getCount());
                    break;
                }
            }
            return remaining;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return ItemStack.EMPTY;
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return true;
        }
    }

    // Bottom: extract the output only.
    public static class BottomOutput implements IItemHandler {
        private final ItemStackHandler items;

        public BottomOutput(ItemStackHandler items) {
            this.items = items;
        }

        @Override
        public int getSlots() {
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack getStackInSlot(int slot) {
            return items.getStackInSlot(TitanCrafterTileEntity.OUTPUT_SLOT);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return items.extractItem(TitanCrafterTileEntity.OUTPUT_SLOT, amount, simulate);
        }

        @Override
        public int getSlotLimit(int slot) {
            return 64;
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return false;
        }
    }
}
