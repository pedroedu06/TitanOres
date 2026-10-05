package com.titanmodpack.titanores.block;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

// Side-specific item access for the Titan Factory (hoppers, pipes, AE2/RS interfaces).
public final class TitanFactoryItemHandlers {
    private static final int[][] COLUMNS = {{0, 1, 2}, {3, 4, 5}};

    private TitanFactoryItemHandlers() {
    }

    // Top: insert only. Recipes are column based, so an item goes to the column that already holds it
    // (or the first empty column) and is spread one by one over that column's slots.
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
            int[] column = pickColumn(stack);
            if (column == null) {
                return stack;
            }
            int limit = Math.min(64, stack.getMaxStackSize());
            if (simulate) {
                int space = 0;
                for (int s : column) {
                    ItemStack current = items.getStackInSlot(s);
                    if (current.isEmpty() || ItemHandlerHelper.canItemStacksStack(current, stack)) {
                        space += limit - current.getCount();
                    }
                }
                return ItemHandlerHelper.copyStackWithSize(stack, Math.max(0, stack.getCount() - space));
            }
            ItemStack remaining = stack.copy();
            while (!remaining.isEmpty()) {
                int best = -1;
                for (int s : column) {
                    ItemStack current = items.getStackInSlot(s);
                    boolean fits = current.isEmpty() || ItemHandlerHelper.canItemStacksStack(current, remaining);
                    if (fits && current.getCount() < limit
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

        private int[] pickColumn(ItemStack stack) {
            for (int[] column : COLUMNS) {
                boolean hasSame = false;
                boolean hasOther = false;
                for (int s : column) {
                    ItemStack current = items.getStackInSlot(s);
                    if (!current.isEmpty()) {
                        if (ItemHandlerHelper.canItemStacksStack(current, stack)) {
                            hasSame = true;
                        } else {
                            hasOther = true;
                        }
                    }
                }
                if (hasSame && !hasOther) {
                    return column;
                }
            }
            for (int[] column : COLUMNS) {
                boolean empty = true;
                for (int s : column) {
                    empty &= items.getStackInSlot(s).isEmpty();
                }
                if (empty) {
                    return column;
                }
            }
            return null;
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
            return items.getStackInSlot(TitanFactoryTileEntity.OUTPUT_SLOT);
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            return stack;
        }

        @Nonnull
        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            return items.extractItem(TitanFactoryTileEntity.OUTPUT_SLOT, amount, simulate);
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
