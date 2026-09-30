package com.titanmodpack.titanores.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

// Lights up dark air within 16 blocks with invisible lights and blocks natural hostile
// mob spawns in its 3x3 chunk area (see SolariteLanternTileEntity and MobSpawnEvents).
public class SolariteLanternBlock extends Block {
    public SolariteLanternBlock(Properties properties) {
        super(properties);
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new SolariteLanternTileEntity();
    }

    // When the lantern is broken or replaced, remove the invisible lights it placed.
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof SolariteLanternTileEntity) {
                ((SolariteLanternTileEntity) tile).removeLights(world);
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }
}
