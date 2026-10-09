package com.titanmodpack.titanores.item;

import net.minecraft.block.Block;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

// Ore block item with a tooltip telling where to find it: dimension/biome and Y range (the pickaxe is left to Waila/Jade).
// Lines come from lang keys tooltip.titanores.<ore>.where / .height.
public class OreBlockItem extends BlockItem {
    private final String ore;

    public OreBlockItem(Block block, Properties properties, String ore) {
        super(block, properties);
        this.ore = ore;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslationTextComponent("tooltip.titanores." + ore + ".where").withStyle(TextFormatting.GOLD));
        tooltip.add(new TranslationTextComponent("tooltip.titanores." + ore + ".height").withStyle(TextFormatting.GOLD));
    }
}
