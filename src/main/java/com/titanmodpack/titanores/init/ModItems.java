package com.titanmodpack.titanores.init;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.item.GlowingItem;
import com.titanmodpack.titanores.item.SolariteMagnetItem;
import com.titanmodpack.titanores.item.TitaniumHeartItem;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
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

    // Upgrade material for titanium tools (smithing table).
    public static final RegistryObject<Item> TITANIUM_STAR = ITEMS.register("titanium_star",
            () -> new GlowingItem(new Item.Properties().tab(ModItemGroup.TITAN_ORES).fireResistant().rarity(Rarity.EPIC)));

    // Dropped by the Ender Dragon. Can always be eaten; each one adds 2 hearts permanently.
    public static final RegistryObject<Item> TITANIUM_HEART = ITEMS.register("titanium_heart",
            () -> new TitaniumHeartItem(new Item.Properties().tab(ModItemGroup.TITAN_ORES).fireResistant().rarity(Rarity.EPIC)
                    .food(new Food.Builder().nutrition(2).saturationMod(0.5F).alwaysEat().build())));

    // Piglin bartering only (see data/titanores/loot_modifiers). All effects last 3 minutes.
    public static final RegistryObject<Item> SOLARITE_APPLE = ITEMS.register("solarite_apple",
            () -> new GlowingItem(new Item.Properties().tab(ModItemGroup.TITAN_ORES).rarity(Rarity.EPIC)
                    .food(new Food.Builder().nutrition(4).saturationMod(1.2F).alwaysEat()
                            .effect(() -> new EffectInstance(Effects.HEALTH_BOOST, 3600, 4), 1.0F)
                            .effect(() -> new EffectInstance(Effects.ABSORPTION, 3600, 4), 1.0F)
                            .effect(() -> new EffectInstance(Effects.FIRE_RESISTANCE, 3600, 0), 1.0F)
                            .effect(() -> new EffectInstance(Effects.DAMAGE_RESISTANCE, 3600, 0), 1.0F)
                            .effect(() -> new EffectInstance(Effects.NIGHT_VISION, 3600, 0), 1.0F)
                            .effect(() -> new EffectInstance(Effects.REGENERATION, 3600, 1), 1.0F)
                            .build())));

    // Saturation keeps the hunger bar full for 8 minutes; regeneration lasts 10 minutes.
    public static final RegistryObject<Item> SOLARITE_CARROT = ITEMS.register("solarite_carrot",
            () -> new Item(new Item.Properties().tab(ModItemGroup.TITAN_ORES).rarity(Rarity.RARE)
                    .food(new Food.Builder().nutrition(10).saturationMod(1.2F)
                            .effect(() -> new EffectInstance(Effects.SATURATION, 9600, 0), 1.0F)
                            .effect(() -> new EffectInstance(Effects.REGENERATION, 12000, 0), 1.0F)
                            .build())));

    public static final RegistryObject<Item> SOLARITE_MAGNET = ITEMS.register("solarite_magnet",
            () -> new SolariteMagnetItem(new Item.Properties().tab(ModItemGroup.TITAN_ORES).stacksTo(1)));

    public static final RegistryObject<Item> SOLARITE_LANTERN = ITEMS.register("solarite_lantern", () -> blockItem(ModBlocks.SOLARITE_LANTERN.get()));

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
