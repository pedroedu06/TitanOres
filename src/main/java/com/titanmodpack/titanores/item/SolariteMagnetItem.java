package com.titanmodpack.titanores.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

// Pulls dropped items and XP orbs within 11 blocks while active. Right-click in the air toggles it.
public class SolariteMagnetItem extends Item {
    private static final String ACTIVE_KEY = "Active";
    private static final double RANGE = 11.0D;

    public SolariteMagnetItem(Properties properties) {
        super(properties);
    }

    public static boolean isActive(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(ACTIVE_KEY);
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            boolean active = !isActive(stack);
            stack.getOrCreateTag().putBoolean(ACTIVE_KEY, active);

            TranslationTextComponent state = new TranslationTextComponent(active ? "message.titanores.magnet.on" : "message.titanores.magnet.off");
            state.withStyle(active ? TextFormatting.GREEN : TextFormatting.RED);
            player.displayClientMessage(new TranslationTextComponent("message.titanores.magnet", state), true);
            world.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                    SoundCategory.PLAYERS, 0.5F, active ? 1.2F : 0.6F);
        }
        return ActionResult.sidedSuccess(stack, world.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return isActive(stack);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClientSide && entity instanceof PlayerEntity && isActive(stack)) {
            pullNearby((PlayerEntity) entity);
        }
    }

    // Moves items/orbs onto the player; normal pickup rules still apply (e.g. full inventory).
    public static void pullNearby(PlayerEntity player) {
        if (player.isSpectator()) {
            return;
        }
        AxisAlignedBB area = player.getBoundingBox().inflate(RANGE);
        double rangeSqr = RANGE * RANGE;
        for (ItemEntity item : player.level.getEntitiesOfClass(ItemEntity.class, area,
                e -> e.isAlive() && !e.hasPickUpDelay() && !e.getPersistentData().getBoolean("PreventRemoteMovement"))) {
            if (item.distanceToSqr(player) <= rangeSqr) {
                item.setPos(player.getX(), player.getY(), player.getZ());
            }
        }
        for (ExperienceOrbEntity orb : player.level.getEntitiesOfClass(ExperienceOrbEntity.class, area, Entity::isAlive)) {
            if (orb.distanceToSqr(player) <= rangeSqr) {
                orb.setPos(player.getX(), player.getY(), player.getZ());
            }
        }
    }
}
