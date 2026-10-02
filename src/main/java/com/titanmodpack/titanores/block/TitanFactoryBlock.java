package com.titanmodpack.titanores.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

// Titan Factory machine: faces the player when placed and opens its GUI on right-click.
// Energy and recipes come later (see TitanFactoryTileEntity).
public class TitanFactoryBlock extends Block {
    public static final DirectionProperty FACING = HorizontalBlock.FACING;

    public TitanFactoryBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TitanFactoryTileEntity();
    }

    @Override
    @SuppressWarnings("deprecation")
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit) {
        if (!world.isClientSide) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TitanFactoryTileEntity && player instanceof ServerPlayerEntity) {
                NetworkHooks.openGui((ServerPlayerEntity) player, (TitanFactoryTileEntity) tile, pos);
            }
        }
        return ActionResultType.sidedSuccess(world.isClientSide);
    }

    // Drop the stored items when the machine is broken.
    @Override
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            TileEntity tile = world.getBlockEntity(pos);
            if (tile instanceof TitanFactoryTileEntity) {
                ((TitanFactoryTileEntity) tile).dropContents(world, pos);
            }
        }
        super.onRemove(state, world, pos, newState, isMoving);
    }
}
