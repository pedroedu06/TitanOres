package com.titanmodpack.titanores.init;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.item.GlowingItem;
import com.titanmodpack.titanores.item.ModItemTier;
import com.titanmodpack.titanores.item.SolariteMagnetItem;
import com.titanmodpack.titanores.item.TitaniumHeartItem;
import net.minecraft.block.Block;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Food;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.PickaxeItem;
import net.minecraft.item.Rarity;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.SwordItem;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

// Every item in the mod is fire resistant (like netherite): see props().
public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, TitanOres.MOD_ID);

    // Solarite
    public static final RegistryObject<Item> SOLARITE_INGOT = ITEMS.register("solarite_ingot", ModItems::simpleItem);
    public static final RegistryObject<Item> SOLARITE_NUGGET = ITEMS.register("solarite_nugget", ModItems::simpleItem);
    public static final RegistryObject<Item> SOLARITE_DUST = ITEMS.register("solarite_dust", ModItems::simpleItem);
    public static final RegistryObject<Item> SOLARITE_STICK = ITEMS.register("solarite_stick", ModItems::simpleItem);

    public static final RegistryObject<Item> SOLARITE_ORE = ITEMS.register("solarite_ore", () -> blockItem(ModBlocks.SOLARITE_ORE.get()));
    public static final RegistryObject<Item> SOLARITE_BLOCK = ITEMS.register("solarite_block", () -> blockItem(ModBlocks.SOLARITE_BLOCK.get()));

    // Solarite tools. Final damage = 1 (base) + 5 (tier) + modifier: sword 25, axe 27.
    public static final RegistryObject<Item> SOLARITE_SWORD = ITEMS.register("solarite_sword",
            () -> new SwordItem(ModItemTier.SOLARITE, 19, -2.4F, props()));
    public static final RegistryObject<Item> SOLARITE_PICKAXE = ITEMS.register("solarite_pickaxe",
            () -> new PickaxeItem(ModItemTier.SOLARITE, 1, -2.8F, props()));
    public static final RegistryObject<Item> SOLARITE_AXE = ITEMS.register("solarite_axe",
            () -> new AxeItem(ModItemTier.SOLARITE, 21.0F, -3.0F, props()));
    public static final RegistryObject<Item> SOLARITE_SHOVEL = ITEMS.register("solarite_shovel",
            () -> new ShovelItem(ModItemTier.SOLARITE, 1.5F, -3.0F, props()));
    public static final RegistryObject<Item> SOLARITE_HOE = ITEMS.register("solarite_hoe",
            () -> new HoeItem(ModItemTier.SOLARITE, -5, 0.0F, props()));

    // Emberite
    public static final RegistryObject<Item> EMBERITE_INGOT = ITEMS.register("emberite_ingot", ModItems::simpleItem);
    public static final RegistryObject<Item> EMBERITE_NUGGET = ITEMS.register("emberite_nugget", ModItems::simpleItem);
    public static final RegistryObject<Item> EMBERITE_DUST = ITEMS.register("emberite_dust", ModItems::simpleItem);

    public static final RegistryObject<Item> EMBERITE_ORE = ITEMS.register("emberite_ore", () -> blockItem(ModBlocks.EMBERITE_ORE.get()));
    public static final RegistryObject<Item> EMBERITE_BLOCK = ITEMS.register("emberite_block", () -> blockItem(ModBlocks.EMBERITE_BLOCK.get()));

    // Titanium
    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot", ModItems::simpleItem);
    public static final RegistryObject<Item> TITANIUM_NUGGET = ITEMS.register("titanium_nugget", ModItems::simpleItem);
    public static final RegistryObject<Item> TITANIUM_DUST = ITEMS.register("titanium_dust", ModItems::simpleItem);

    public static final RegistryObject<Item> TITANIUM_ORE = ITEMS.register("titanium_ore", () -> blockItem(ModBlocks.TITANIUM_ORE.get()));
    public static final RegistryObject<Item> TITANIUM_BLOCK = ITEMS.register("titanium_block", () -> blockItem(ModBlocks.TITANIUM_BLOCK.get()));

    // Upgrade material for titanium tools (smithing table).
    public static final RegistryObject<Item> TITANIUM_STAR = ITEMS.register("titanium_star",
            () -> new GlowingItem(props().rarity(Rarity.EPIC)));

    // Dropped by the Ender Dragon. Can always be eaten; each one adds 2 hearts permanently.
    public static final RegistryObject<Item> TITANIUM_HEART = ITEMS.register("titanium_heart",
            () -> new TitaniumHeartItem(props().rarity(Rarity.EPIC)
                    .food(new Food.Builder().nutrition(2).saturationMod(0.5F).alwaysEat().build())));

    // Piglin bartering only (see data/titanores/loot_modifiers). All effects last 3 minutes.
    public static final RegistryObject<Item> SOLARITE_APPLE = ITEMS.register("solarite_apple",
            () -> new GlowingItem(props().rarity(Rarity.EPIC)
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
            () -> new Item(props().rarity(Rarity.RARE)
                    .food(new Food.Builder().nutrition(10).saturationMod(1.2F)
                            .effect(() -> new EffectInstance(Effects.SATURATION, 9600, 0), 1.0F)
                            .effect(() -> new EffectInstance(Effects.REGENERATION, 12000, 0), 1.0F)
                            .build())));

    public static final RegistryObject<Item> SOLARITE_MAGNET = ITEMS.register("solarite_magnet",
            () -> new SolariteMagnetItem(props().stacksTo(1)));

    public static final RegistryObject<Item> SOLARITE_LANTERN = ITEMS.register("solarite_lantern", () -> blockItem(ModBlocks.SOLARITE_LANTERN.get()));

    private static Item.Properties props() {
        return new Item.Properties().tab(ModItemGroup.TITAN_ORES).fireResistant();
    }

    private static Item simpleItem() {
        return new Item(props());
    }

    private static Item blockItem(Block block) {
        return new BlockItem(block, props());
    }
}
