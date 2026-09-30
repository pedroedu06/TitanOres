package com.titanmodpack.titanores.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

// Item with the enchantment glint, like the Nether Star.
public class GlowingItem extends Item {
    public GlowingItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }
}
