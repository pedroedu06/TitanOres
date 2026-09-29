package com.titanmodpack.titanores.init;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;

public class ModItemGroup {
    public static final ItemGroup TITAN_ORES = new ItemGroup("titanores") {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.SOLARITE_INGOT.get());
        }
    };
}
