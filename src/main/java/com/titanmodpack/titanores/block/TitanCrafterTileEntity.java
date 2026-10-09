package com.titanmodpack.titanores.block;

import com.titanmodpack.titanores.container.TitanCrafterContainer;
import com.titanmodpack.titanores.energy.ModEnergyStorage;
import com.titanmodpack.titanores.init.ModRecipes;
import com.titanmodpack.titanores.init.ModTileEntities;
import com.titanmodpack.titanores.recipe.TitanCrafterRecipe;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Titan Crafter: 9x6 crafting grid (slots 0-53, row by row), 1 output slot (54) and a 200M FE buffer.
// Energy enters from every side; items enter from the top (template style) and leave from the bottom.
// Each tick it spends recipe.energy / recipe.time FE; without enough energy it pauses (progress is kept).
public class TitanCrafterTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
    public static final int GRID_SLOTS = TitanCrafterRecipe.SIZE;
    public static final int OUTPUT_SLOT = GRID_SLOTS;
    public static final int SLOT_COUNT = GRID_SLOTS + 1;
    public static final int ENERGY_CAPACITY = 200_000_000;

    private final ItemStackHandler items = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            recipeDirty = true;
            setChanged();
        }
    };
    private final ModEnergyStorage energy = new ModEnergyStorage(ENERGY_CAPACITY, this::setChanged);
    private final RecipeWrapper recipeInventory = new RecipeWrapper(items);

    private final LazyOptional<IItemHandler> allItems = LazyOptional.of(() -> items);
    private final LazyOptional<IItemHandler> topItems = LazyOptional.of(() -> new TitanCrafterItemHandlers.TopInput(items));
    private final LazyOptional<IItemHandler> bottomItems = LazyOptional.of(() -> new TitanCrafterItemHandlers.BottomOutput(items));
    private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> energy);

    private int progress;
    private int maxProgress;
    private boolean recipeDirty = true;
    @Nullable
    private TitanCrafterRecipe cachedRecipe;
    @Nullable
    private ResourceLocation lastRecipeId;

    // Synced to the open GUI. Container data is sent as 16-bit values, so energy is split in two halves.
    private final IIntArray data = new IIntArray() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0:
                    return energy.getEnergyStored() & 0xFFFF;
                case 1:
                    return (energy.getEnergyStored() >>> 16) & 0xFFFF;
                case 2:
                    return progress;
                case 3:
                    return maxProgress;
                default:
                    return 0;
            }
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int getCount() {
            return 4;
        }
    };

    public TitanCrafterTileEntity() {
        super(ModTileEntities.TITAN_CRAFTER.get());
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }
        TitanCrafterRecipe recipe = currentRecipe();
        ResourceLocation recipeId = recipe == null ? null : recipe.getId();
        if (recipeId == null || !recipeId.equals(lastRecipeId)) {
            lastRecipeId = recipeId;
            if (progress != 0) {
                progress = 0;
                setChanged();
            }
        }
        if (recipe == null) {
            maxProgress = 0;
            return;
        }
        maxProgress = recipe.getTime();
        if (!canOutput(recipe.getResultItem())) {
            return;
        }
        int cost = recipe.getEnergyPerTick();
        if (energy.getEnergyStored() < cost) {
            return;
        }
        energy.consume(cost);
        progress++;
        if (progress >= maxProgress) {
            ItemStack result = recipe.assemble(recipeInventory);
            recipe.consumeInputs(items);
            items.insertItem(OUTPUT_SLOT, result, false);
            progress = 0;
        }
        setChanged();
    }

    @Nullable
    private TitanCrafterRecipe currentRecipe() {
        if (recipeDirty) {
            recipeDirty = false;
            cachedRecipe = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.TITAN_CRAFTING_TYPE, recipeInventory, level)
                    .orElse(null);
        }
        return cachedRecipe;
    }

    private boolean canOutput(ItemStack result) {
        ItemStack output = items.getStackInSlot(OUTPUT_SLOT);
        if (output.isEmpty()) {
            return true;
        }
        return ItemHandlerHelper.canItemStacksStack(output, result)
                && output.getCount() + result.getCount() <= Math.min(output.getMaxStackSize(), items.getSlotLimit(OUTPUT_SLOT));
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public IIntArray getData() {
        return data;
    }

    public void dropContents(World world, BlockPos pos) {
        for (int i = 0; i < items.getSlots(); i++) {
            InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), items.getStackInSlot(i));
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.titanores.titan_crafter");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new TitanCrafterContainer(id, playerInventory, this, data);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("Inventory", items.serializeNBT());
        nbt.putInt("Energy", energy.getEnergyStored());
        nbt.putInt("Progress", progress);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        items.deserializeNBT(nbt.getCompound("Inventory"));
        energy.setEnergy(nbt.getInt("Energy"));
        progress = nbt.getInt("Progress");
        recipeDirty = true;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == CapabilityEnergy.ENERGY) {
            return energyCapability.cast();
        }
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) {
                return allItems.cast();
            }
            if (side == Direction.UP) {
                return topItems.cast();
            }
            if (side == Direction.DOWN) {
                return bottomItems.cast();
            }
            return LazyOptional.empty();
        }
        return super.getCapability(capability, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        allItems.invalidate();
        topItems.invalidate();
        bottomItems.invalidate();
        energyCapability.invalidate();
    }
}
