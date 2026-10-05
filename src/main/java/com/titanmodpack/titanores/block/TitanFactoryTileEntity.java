package com.titanmodpack.titanores.block;

import com.titanmodpack.titanores.container.TitanFactoryContainer;
import com.titanmodpack.titanores.energy.ModEnergyStorage;
import com.titanmodpack.titanores.init.ModItems;
import com.titanmodpack.titanores.init.ModRecipes;
import com.titanmodpack.titanores.init.ModTileEntities;
import com.titanmodpack.titanores.recipe.TitanFactoryRecipe;
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

// Titan Factory: 6 input slots (0-2 left column, 3-5 right column), 1 output slot (6) and a 100M FE buffer.
// Energy enters from every side; items enter from the top and leave from the bottom.
// Each tick it spends recipe.energy / recipe.time FE; without enough energy it pauses (progress is kept).
public class TitanFactoryTileEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {
    public static final int INPUT_SLOTS = 6;
    public static final int OUTPUT_SLOT = 6;
    public static final int SLOT_COUNT = 7;
    public static final int ENERGY_CAPACITY = 100_000_000;
    public static final int UPGRADE_SLOTS = 1;
    public static final int MAX_UPGRADES = 4;

    private final ItemStackHandler items = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            recipeDirty = true;
            setChanged();
        }
    };
    // Single upgrade slot holding up to MAX_UPGRADES speed upgrades. Separate handler (own NBT key) so old saves keep
    // their 7-slot inventory. Not exposed to automation.
    private final ItemStackHandler upgrades = new ItemStackHandler(UPGRADE_SLOTS) {
        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return stack.getItem() == ModItems.SPEED_UPGRADE.get();
        }

        @Override
        public int getSlotLimit(int slot) {
            return MAX_UPGRADES;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final ModEnergyStorage energy = new ModEnergyStorage(ENERGY_CAPACITY, this::setChanged);
    private final RecipeWrapper recipeInventory = new RecipeWrapper(items);

    private final LazyOptional<IItemHandler> allItems = LazyOptional.of(() -> items);
    private final LazyOptional<IItemHandler> topItems = LazyOptional.of(() -> new TitanFactoryItemHandlers.TopInput(items));
    private final LazyOptional<IItemHandler> bottomItems = LazyOptional.of(() -> new TitanFactoryItemHandlers.BottomOutput(items));
    private final LazyOptional<IEnergyStorage> energyCapability = LazyOptional.of(() -> energy);

    private int progress;
    private int maxProgress;
    private boolean recipeDirty = true;
    @Nullable
    private TitanFactoryRecipe cachedRecipe;
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

    public TitanFactoryTileEntity() {
        super(ModTileEntities.TITAN_FACTORY.get());
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }
        TitanFactoryRecipe recipe = currentRecipe();
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
        // Each speed upgrade halves the time; the total energy of the craft stays the same, so FE/t goes up.
        maxProgress = Math.max(1, recipe.getTime() >> installedUpgrades());
        if (progress > maxProgress) {
            progress = maxProgress;
        }
        if (!canOutput(recipe.getResultItem())) {
            return;
        }
        int cost = (int) Math.ceil((double) recipe.getEnergy() / maxProgress);
        if (energy.getEnergyStored() < cost) {
            return;
        }
        energy.consume(cost);
        progress++;
        if (progress >= maxProgress) {
            ItemStack result = recipe.assemble(recipeInventory);
            recipe.consumeInputs(recipeInventory, items);
            items.insertItem(OUTPUT_SLOT, result, false);
            progress = 0;
        }
        setChanged();
    }

    @Nullable
    private TitanFactoryRecipe currentRecipe() {
        if (recipeDirty) {
            recipeDirty = false;
            cachedRecipe = level.getRecipeManager()
                    .getRecipeFor(ModRecipes.TITAN_FACTORY_TYPE, recipeInventory, level)
                    .orElse(null);
        }
        return cachedRecipe;
    }

    private int installedUpgrades() {
        return Math.min(MAX_UPGRADES, upgrades.getStackInSlot(0).getCount());
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

    public ItemStackHandler getUpgrades() {
        return upgrades;
    }

    public IIntArray getData() {
        return data;
    }

    public void dropContents(World world, BlockPos pos) {
        for (int i = 0; i < items.getSlots(); i++) {
            InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), items.getStackInSlot(i));
        }
        for (int i = 0; i < upgrades.getSlots(); i++) {
            InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), upgrades.getStackInSlot(i));
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.titanores.titan_factory");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new TitanFactoryContainer(id, playerInventory, this, data);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("Inventory", items.serializeNBT());
        nbt.put("Upgrades", upgrades.serializeNBT());
        nbt.putInt("Energy", energy.getEnergyStored());
        nbt.putInt("Progress", progress);
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        items.deserializeNBT(nbt.getCompound("Inventory"));
        if (nbt.contains("Upgrades")) {
            loadUpgrades(nbt.getCompound("Upgrades"));
        }
        energy.setEnergy(nbt.getInt("Energy"));
        progress = nbt.getInt("Progress");
        recipeDirty = true;
    }

    // Reads into a temporary handler first: saves from older versions had one upgrade per slot in 4 slots.
    private void loadUpgrades(CompoundNBT tag) {
        ItemStackHandler saved = new ItemStackHandler();
        saved.deserializeNBT(tag);
        int total = 0;
        for (int i = 0; i < saved.getSlots(); i++) {
            ItemStack stack = saved.getStackInSlot(i);
            if (stack.getItem() == ModItems.SPEED_UPGRADE.get()) {
                total += stack.getCount();
            }
        }
        upgrades.setStackInSlot(0, total > 0 ? new ItemStack(ModItems.SPEED_UPGRADE.get(), Math.min(total, MAX_UPGRADES)) : ItemStack.EMPTY);
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
