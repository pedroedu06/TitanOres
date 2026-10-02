package com.titanmodpack.titanores.block;

import com.titanmodpack.titanores.container.TitanFactoryContainer;
import com.titanmodpack.titanores.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Titan Factory storage: 6 input slots (0-5) and 1 output slot (6).
// Exposed as an item handler so hoppers/pipes can insert. Energy and recipe processing come later.
public class TitanFactoryTileEntity extends TileEntity implements INamedContainerProvider {
    public static final int INPUT_SLOTS = 6;
    public static final int OUTPUT_SLOT = 6;
    public static final int SLOT_COUNT = 7;

    private final ItemStackHandler items = new ItemStackHandler(SLOT_COUNT) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> itemCapability = LazyOptional.of(() -> items);

    public TitanFactoryTileEntity() {
        super(ModTileEntities.TITAN_FACTORY.get());
    }

    public ItemStackHandler getItems() {
        return items;
    }

    public void dropContents(World world, BlockPos pos) {
        for (int i = 0; i < items.getSlots(); i++) {
            InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), items.getStackInSlot(i));
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.titanores.titan_factory");
    }

    @Nullable
    @Override
    public Container createMenu(int id, PlayerInventory playerInventory, PlayerEntity player) {
        return new TitanFactoryContainer(id, playerInventory, this);
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.put("Inventory", items.serializeNBT());
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        items.deserializeNBT(nbt.getCompound("Inventory"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction side) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    protected void invalidateCaps() {
        super.invalidateCaps();
        itemCapability.invalidate();
    }
}
