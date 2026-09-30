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

    // Nether material: fire resistant items, like netherite.
    public static final RegistryObject<Item> EMBERITE_INGOT = ITEMS.register("emberite_ingot", ModItems::fireproofItem);
    public static final RegistryObject<Item> EMBERITE_NUGGET = ITEMS.register("emberite_nugget", ModItems::fireproofItem);
    public static final RegistryObject<Item> EMBERITE_DUST = ITEMS.register("emberite_dust", ModItems::fireproofItem);

    public static final RegistryObject<Item> EMBERITE_ORE = ITEMS.register("emberite_ore", () -> fireproofBlockItem(ModBlocks.EMBERITE_ORE.get()));
    public static final RegistryObject<Item> EMBERITE_BLOCK = ITEMS.register("emberite_block", () -> fireproofBlockItem(ModBlocks.EMBERITE_BLOCK.get()));

    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot", ModItems::fireproofItem);
    public static final RegistryObject<Item> TITANIUM_NUGGET = ITEMS.register("titanium_nugget", ModItems::fireproofItem);
    public static final RegistryObject<Item> TITANIUM_DUST = ITEMS.register("titanium_dust", ModItems::fireproofItem);

    public static final RegistryObject<Item> TITANIUM_ORE = ITEMS.register("titanium_ore", () -> fireproofBlockItem(ModBlocks.TITANIUM_ORE.get()));
    public static final RegistryObject<Item> TITANIUM_BLOCK = ITEMS.register("titanium_block", () -> fireproofBlockItem(ModBlocks.TITANIUM_BLOCK.get()));

    private static Item simpleItem() {
        return new Item(new Item.Properties().tab(ModItemGroup.TITAN_ORES));
    }

    private static Item blockItem(Block block) {
        return new BlockItem(block, new Item.Properties().tab(ModItemGroup.TITAN_ORES));
    }

    private static Item fireproofItem() {
        return new Item(new Item.Properties().tab(ModItemGroup.TITAN_ORES).fireResistant());
    }

    private static Item fireproofBlockItem(Block block) {
        return new BlockItem(block, new Item.Properties().tab(ModItemGroup.TITAN_ORES).fireResistant());
    }
}
