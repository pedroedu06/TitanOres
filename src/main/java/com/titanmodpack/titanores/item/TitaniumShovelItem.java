package com.titanmodpack.titanores.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.item.ShovelItem;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

// Same Area Mode as the titanium pickaxe: digs 3x3 and auto-smelts ores.
public class TitaniumShovelItem extends ShovelItem {
    public TitaniumShovelItem(IItemTier tier, float attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, PlayerEntity player) {
        if (AreaToolHelper.isActive(stack)) {
            AreaToolHelper.mineArea(stack, pos, player);
        }
        return false;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ActionResult<ItemStack> toggled = AreaToolHelper.toggleOnUse(world, player, hand);
        return toggled != null ? toggled : super.use(world, player, hand);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ActionResultType toggled = AreaToolHelper.toggleOnUseOn(context);
        return toggled != null ? toggled : super.useOn(context);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        tooltip.add(AreaToolHelper.stateText(AreaToolHelper.isActive(stack)));
        tooltip.add(new TranslationTextComponent("tooltip.titanores.toggle_hint").withStyle(TextFormatting.GRAY));
    }
}
