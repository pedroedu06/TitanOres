package com.titanmods.titanores.block;

import com.titanmods.titanores.init.ModTags;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.item.Item;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.ForgeHooks;

import java.util.Random;
import java.util.function.Supplier;

// Mystical Agriculture style crop: only grows on the titanores:essence_crop_soil tag (Titan Utilities Blue Soil), slower than wheat (1 in GROWTH_CHANCE random ticks with
// enough light). Bone meal is blocked in event/CropEvents; other growth items/accelerators still work.
public class EssenceCropBlock extends CropsBlock {
    private static final int GROWTH_CHANCE = 5;

    private final Supplier<Item> seed;

    public EssenceCropBlock(Properties properties, Supplier<Item> seed) {
        super(properties);
        this.seed = seed;
    }

    @Override
    protected IItemProvider getBaseSeedId() {
        return seed.get();
    }

    @Override
    protected boolean mayPlaceOn(BlockState state, IBlockReader world, BlockPos pos) {
        return state.is(ModTags.ESSENCE_CROP_SOIL);
    }

    // Vanilla/Forge crops ask the block below if it sustains CROP plants (farmland only), so check the soil tag directly.
    @Override
    public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
        boolean light = world.getRawBrightness(pos, 0) >= 8 || world.canSeeSky(pos);
        return light && mayPlaceOn(world.getBlockState(pos.below()), world, pos.below());
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isAreaLoaded(pos, 1) || world.getRawBrightness(pos, 0) < 9) {
            return;
        }
        int age = getAge(state);
        if (age < getMaxAge() && ForgeHooks.onCropsGrowPre(world, pos, state, random.nextInt(GROWTH_CHANCE) == 0)) {
            world.setBlock(pos, getStateForAge(age + 1), 2);
            ForgeHooks.onCropsGrowPost(world, pos, state);
        }
    }
}
