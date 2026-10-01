package com.titanmodpack.titanores.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeMod;

import java.util.ArrayList;
import java.util.List;

// Shared logic for the titanium tools: the toggleable "Area Mode" (Shift + right-click),
// 3x3 mining, and breaking extra blocks as the player (fires events, normal drops).
public final class AreaToolHelper {
    private static final String MODE_KEY = "AreaMode";
    private static final ThreadLocal<Boolean> BREAKING = ThreadLocal.withInitial(() -> false);

    private AreaToolHelper() {
    }

    // On by default, so a freshly upgraded tool already works.
    public static boolean isActive(ItemStack stack) {
        return !stack.hasTag() || !stack.getTag().contains(MODE_KEY) || stack.getTag().getBoolean(MODE_KEY);
    }

    public static ITextComponent stateText(boolean active) {
        TranslationTextComponent state = new TranslationTextComponent(active ? "message.titanores.on" : "message.titanores.off");
        state.withStyle(active ? TextFormatting.GREEN : TextFormatting.RED);
        return new TranslationTextComponent("message.titanores.area_mode", state);
    }

    private static void toggle(ItemStack stack, World world, PlayerEntity player) {
        if (world.isClientSide) {
            return;
        }
        boolean active = !isActive(stack);
        stack.getOrCreateTag().putBoolean(MODE_KEY, active);
        player.displayClientMessage(stateText(active), true);
        world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundCategory.PLAYERS, 0.5F, active ? 1.2F : 0.6F);
    }

    // Item#use: returns null when the player is not sneaking (normal behavior).
    public static ActionResult<ItemStack> toggleOnUse(World world, PlayerEntity player, Hand hand) {
        if (!player.isShiftKeyDown()) {
            return null;
        }
        ItemStack stack = player.getItemInHand(hand);
        toggle(stack, world, player);
        return ActionResult.sidedSuccess(stack, world.isClientSide);
    }

    // Item#useOn: returns null when the player is not sneaking (normal behavior).
    public static ActionResultType toggleOnUseOn(ItemUseContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null || !player.isShiftKeyDown()) {
            return null;
        }
        toggle(context.getItemInHand(), context.getLevel(), player);
        return ActionResultType.sidedSuccess(context.getLevel().isClientSide);
    }

    public static boolean isBreaking() {
        return BREAKING.get();
    }

    // Breaks blocks as the player. The guard stops onBlockStartBreak from recursing.
    public static void breakBlocks(ServerPlayerEntity player, List<BlockPos> positions) {
        BREAKING.set(true);
        try {
            for (BlockPos pos : positions) {
                player.gameMode.destroyBlock(pos);
            }
        } finally {
            BREAKING.set(false);
        }
    }

    // Breaks the 8 blocks around center, on the plane of the face the player is looking at.
    public static void mineArea(ItemStack stack, BlockPos center, PlayerEntity player) {
        if (isBreaking() || !(player instanceof ServerPlayerEntity) || player.isShiftKeyDown()) {
            return;
        }
        World world = player.level;
        BlockState centerState = world.getBlockState(center);
        if (!isEffective(stack, centerState)) {
            return;
        }
        float maxHardness = centerState.getDestroySpeed(world, center) + 5.0F;
        Direction.Axis axis = lookedFace(world, player).getAxis();

        List<BlockPos> targets = new ArrayList<>();
        for (int a = -1; a <= 1; a++) {
            for (int b = -1; b <= 1; b++) {
                if (a == 0 && b == 0) {
                    continue;
                }
                BlockPos pos = axis == Direction.Axis.Y ? center.offset(a, 0, b)
                        : axis == Direction.Axis.X ? center.offset(0, a, b)
                        : center.offset(a, b, 0);
                BlockState state = world.getBlockState(pos);
                float hardness = state.getDestroySpeed(world, pos);
                if (state.isAir(world, pos) || hardness < 0 || hardness > maxHardness || !isEffective(stack, state)) {
                    continue;
                }
                targets.add(pos);
            }
        }
        breakBlocks((ServerPlayerEntity) player, targets);
    }

    private static boolean isEffective(ItemStack stack, BlockState state) {
        return stack.getDestroySpeed(state) > 1.0F;
    }

    private static Direction lookedFace(World world, PlayerEntity player) {
        Vector3d eye = player.getEyePosition(1.0F);
        Vector3d look = player.getViewVector(1.0F);
        double reach = player.getAttribute(ForgeMod.REACH_DISTANCE.get()).getValue();
        BlockRayTraceResult hit = world.clip(new RayTraceContext(eye, eye.add(look.scale(reach)),
                RayTraceContext.BlockMode.OUTLINE, RayTraceContext.FluidMode.NONE, player));
        if (hit.getType() == RayTraceResult.Type.BLOCK) {
            return hit.getDirection();
        }
        return Direction.getNearest(look.x, look.y, look.z).getOpposite();
    }
}
