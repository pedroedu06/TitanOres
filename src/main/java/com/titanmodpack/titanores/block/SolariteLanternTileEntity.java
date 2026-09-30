package com.titanmodpack.titanores.block;

import com.titanmodpack.titanores.init.ModBlocks;
import com.titanmodpack.titanores.init.ModTileEntities;
import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Solarite Lantern logic:
// - registers its position in LanternRegistry so hostile spawns are blocked around it;
// - slowly places invisible light blocks in dark air within 16 blocks (like Torchmaster's Feral Flare);
// - removes those lights when the lantern is broken (see SolariteLanternBlock#onRemove).
public class SolariteLanternTileEntity extends TileEntity implements ITickableTileEntity {
    private static final int RADIUS = 16;
    private static final int MAX_LIGHTS = 128;
    private static final int MIN_LIGHT_LEVEL = 10;
    private static final int MIN_SPACING_SQR = 4 * 4;
    private static final int ATTEMPTS_PER_TICK = 4;
    private static final int CLEANUP_INTERVAL = 200;

    private final List<BlockPos> lights = new ArrayList<>();
    private final Random random = new Random();
    private int ticks;

    public SolariteLanternTileEntity() {
        super(ModTileEntities.SOLARITE_LANTERN.get());
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) {
            return;
        }
        if (++ticks % CLEANUP_INTERVAL == 0) {
            lights.removeIf(pos -> level.isLoaded(pos) && !level.getBlockState(pos).is(ModBlocks.SOLARITE_LIGHT.get()));
        }
        if (lights.size() >= MAX_LIGHTS) {
            return;
        }
        for (int i = 0; i < ATTEMPTS_PER_TICK; i++) {
            BlockPos target = worldPosition.offset(
                    random.nextInt(RADIUS * 2 + 1) - RADIUS,
                    random.nextInt(RADIUS * 2 + 1) - RADIUS,
                    random.nextInt(RADIUS * 2 + 1) - RADIUS);
            if (canPlaceLight(target)) {
                level.setBlock(target, ModBlocks.SOLARITE_LIGHT.get().defaultBlockState(), 3);
                lights.add(target);
                setChanged();
                return;
            }
        }
    }

    private boolean canPlaceLight(BlockPos target) {
        if (target.distSqr(worldPosition) > RADIUS * RADIUS || !level.isLoaded(target) || !level.isEmptyBlock(target)) {
            return false;
        }
        if (level.getBrightness(LightType.BLOCK, target) >= MIN_LIGHT_LEVEL) {
            return false;
        }
        // Light updates are not instant, so also keep a minimum distance from our other lights.
        for (BlockPos light : lights) {
            if (light.distSqr(target) < MIN_SPACING_SQR) {
                return false;
            }
        }
        return true;
    }

    public void removeLights(World world) {
        BlockState lightState = ModBlocks.SOLARITE_LIGHT.get().defaultBlockState();
        for (BlockPos pos : lights) {
            if (world.getBlockState(pos) == lightState) {
                world.removeBlock(pos, false);
            }
        }
        lights.clear();
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            LanternRegistry.add(level, worldPosition);
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        unregister();
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        unregister();
    }

    private void unregister() {
        if (level != null && !level.isClientSide) {
            LanternRegistry.remove(level, worldPosition);
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT nbt) {
        super.save(nbt);
        nbt.putLongArray("Lights", lights.stream().mapToLong(BlockPos::asLong).toArray());
        return nbt;
    }

    @Override
    public void load(BlockState state, CompoundNBT nbt) {
        super.load(state, nbt);
        lights.clear();
        for (long packed : nbt.getLongArray("Lights")) {
            lights.add(BlockPos.of(packed));
        }
    }
}
