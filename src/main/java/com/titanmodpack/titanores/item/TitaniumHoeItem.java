package com.titanmodpack.titanores.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

// Area Mode: tills a 3x3 area around the clicked block.
public class TitaniumHoeItem extends HoeItem {
    public TitaniumHoeItem(IItemTier tier, int attackDamage, float attackSpeed, Properties properties) {
        super(tier, attackDamage, attackSpeed, properties);
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        ActionResultType toggled = AreaToolHelper.toggleOnUseOn(context);
        if (toggled != null) {
            return toggled;
        }
        if (!AreaToolHelper.isActive(context.getItemInHand())) {
            return super.useOn(context);
        }
        ActionResultType result = ActionResultType.PASS;
        BlockPos center = context.getClickedPos();
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockRayTraceResult hit = new BlockRayTraceResult(context.getClickLocation().add(dx, 0, dz),
                        context.getClickedFace(), center.offset(dx, 0, dz), context.isInside());
                ActionResultType tilled = super.useOn(new ItemUseContext(context.getPlayer(), context.getHand(), hit));
                if (tilled.consumesAction()) {
                    result = tilled;
                }
            }
        }
        return result;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ActionResult<ItemStack> toggled = AreaToolHelper.toggleOnUse(world, player, hand);
        return toggled != null ? toggled : super.use(world, player, hand);
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
