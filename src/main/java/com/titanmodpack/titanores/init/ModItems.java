package com.titanmodpack.titanores.init;

import com.titanmodpack.titanores.TitanOres;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TitanOres.MOD_ID);

    public static final RegistryObject<Item> SOLARITE_INGOT = ITEMS.register("solarite_ingot", ModItems::simpleItem);
    public static final RegistryObject<Item> SOLARITE_NUGGET = ITEMS.register("solarite_nugget", ModItems::simpleItem);
    public static final RegistryObject<Item> SOLARITE_DUST = ITEMS.register("solarite_dust", ModItems::simpleItem);

    public static final RegistryObject<Item> SOLARITE_ORE = ITEMS.register("solarite_ore", () -> blockItem(ModBlocks.SOLARITE_ORE.get()));
    public static final RegistryObject<Item> SOLARITE_BLOCK = ITEMS.register("solarite_block", () -> blockItem(ModBlocks.SOLARITE_BLOCK.get()));

    private static Item simpleItem() {
        return new Item(new Item.Properties().tab(ModItemGroup.TITAN_ORES));
    }

    private static Item blockItem(Block block) {
        return new BlockItem(block, new Item.Properties().tab(ModItemGroup.TITAN_ORES));
    }
}
