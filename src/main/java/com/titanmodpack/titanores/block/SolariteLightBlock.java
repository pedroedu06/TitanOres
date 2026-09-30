package com.titanmodpack.titanores.block;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.PushReaction;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

// Invisible light placed by the Solarite Lantern. No collision, no drops, can be replaced like air.
public class SolariteLightBlock extends Block {
    public SolariteLightBlock() {
        super(AbstractBlock.Properties.of(Material.AIR)
                .noCollission()
                .noDrops()
                .noOcclusion()
                .instabreak()
                .lightLevel(state -> 14));
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockItemUseContext context) {
        return true;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }
}
