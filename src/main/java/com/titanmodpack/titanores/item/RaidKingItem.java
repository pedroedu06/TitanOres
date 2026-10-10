package com.titanmodpack.titanores.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

import javax.annotation.Nullable;

// Crown dropped by max Bad Omen raids. Worn on the head (armor slot or Curios "head") it gives endless Hero of the
// Village (see RaidKingEvents). No armor value. "HeroLevel" = the raid's max Bad Omen level when it dropped.
public class RaidKingItem extends Item {
    private static final String HERO_LEVEL = "HeroLevel";
    private static final int DEFAULT_HERO_LEVEL = 5;

    public RaidKingItem(Properties properties) {
        super(properties);
    }

    public static ItemStack withHeroLevel(ItemStack stack, int level) {
        stack.getOrCreateTag().putInt(HERO_LEVEL, level);
        return stack;
    }

    public static int getHeroLevel(ItemStack stack) {
        return stack.hasTag() && stack.getTag().contains(HERO_LEVEL) ? Math.max(1, stack.getTag().getInt(HERO_LEVEL)) : DEFAULT_HERO_LEVEL;
    }

    @Nullable
    @Override
    public EquipmentSlotType getEquipmentSlot(ItemStack stack) {
        return EquipmentSlotType.HEAD;
    }

    @Override
    public boolean canEquip(ItemStack stack, EquipmentSlotType armorType, Entity entity) {
        return armorType == EquipmentSlotType.HEAD;
    }

    // Right-click puts it on the head when the helmet slot is empty, like armor.
    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.getItemBySlot(EquipmentSlotType.HEAD).isEmpty()) {
            player.setItemSlot(EquipmentSlotType.HEAD, stack.copy());
            stack.setCount(0);
            return ActionResult.sidedSuccess(stack, world.isClientSide);
        }
        return ActionResult.fail(stack);
    }
}
