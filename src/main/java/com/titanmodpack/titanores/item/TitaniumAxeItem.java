package com.titanmodpack.titanores.item;

import net.minecraft.block.BlockState;
import net.minecraft.block.LeavesBlock;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.AxeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// Breaking any log fells the whole tree (connected logs, up to MAX_LOGS).
// Only natural trees: the logs must touch non-persistent leaves (or nether wart blocks),
// so log buildings are safe. Sneaking breaks a single log.
public class TitaniumAxeItem extends AxeItem {
    private static final int MAX_LOGS = 256;

    public TitaniumAxeItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        if (!(player instanceof ServerPlayerEntity) || player.isShiftKeyDown() || AreaToolHelper.isBreaking()) {
            return false;
        }
        World world = player.level;
        if (!world.getBlockState(pos).is(BlockTags.LOGS)) {
            return false;
        }

        Set<BlockPos> visited = new HashSet<>();
        Deque<BlockPos> queue = new ArrayDeque<>();
        List<BlockPos> logs = new ArrayList<>();
        boolean naturalTree = false;
        visited.add(pos);
        queue.add(pos);
        while (!queue.isEmpty() && logs.size() < MAX_LOGS) {
            BlockPos current = queue.poll();
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    for (int dz = -1; dz <= 1; dz++) {
                        BlockPos next = current.offset(dx, dy, dz);
                        if (!visited.add(next) || !world.isLoaded(next)) {
                            continue;
                        }
                        BlockState state = world.getBlockState(next);
                        if (state.is(BlockTags.LOGS)) {
                            queue.add(next);
                            logs.add(next);
                        } else if (isNaturalCanopy(state)) {
                            naturalTree = true;
                        }
                    }
                }
            }
        }
        if (naturalTree) {
            AreaToolHelper.breakBlocks((ServerPlayerEntity) player, logs);
        }
        return false;
    }

    private static boolean isNaturalCanopy(BlockState state) {
        if (state.getBlock() instanceof LeavesBlock) {
            return !state.getValue(LeavesBlock.PERSISTENT);
        }
        return state.is(BlockTags.WART_BLOCKS);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(new TranslationTextComponent("tooltip.titanores.tree_felling").withStyle(TextFormatting.GRAY));
    }
}
