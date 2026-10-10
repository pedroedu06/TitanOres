package com.titanmodpack.titanores.init;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.item.GlowingItem;
import com.titanmodpack.titanores.item.ModItemTier;
import com.titanmodpack.titanores.item.OreBlockItem;
import com.titanmodpack.titanores.item.RaidKingItem;
import com.titanmodpack.titanores.item.SolariteMagnetItem;
import com.titanmodpack.titanores.item.TitaniumAxeItem;
import com.titanmodpack.titanores.item.TitaniumHeartItem;
import com.titanmodpack.titanores.item.TitaniumHoeItem;
import com.titanmodpack.titanores.item.TitaniumPickaxeItem;
import com.titanmodpack.titanores.item.TitaniumShovelItem;
import com.titanmodpack.titanores.item.TitaniumSwordItem;
import net.minecraft.block.Block;
import com.titanmodpack.titanores.item.ModArmorMaterial;
import net.minecraft.inventory.EquipmentSlotType;
import com.titanmodpack.titanores.item.ModArmorItem;
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

    public static final RegistryObject<Item> SOLARITE_ORE = ITEMS.register("solarite_ore",
            () -> new OreBlockItem(ModBlocks.SOLARITE_ORE.get(), props(), "solarite"));
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

    // Solarite armor: 4/7/9/4 (24), toughness 3.5 and knockback resistance 0.15 per piece.
    public static final RegistryObject<Item> SOLARITE_HELMET = ITEMS.register("solarite_helmet",
            () -> new ModArmorItem(ModArmorMaterial.SOLARITE, EquipmentSlotType.HEAD, props()));
    public static final RegistryObject<Item> SOLARITE_CHESTPLATE = ITEMS.register("solarite_chestplate",
            () -> new ModArmorItem(ModArmorMaterial.SOLARITE, EquipmentSlotType.CHEST, props()));
    public static final RegistryObject<Item> SOLARITE_LEGGINGS = ITEMS.register("solarite_leggings",
            () -> new ModArmorItem(ModArmorMaterial.SOLARITE, EquipmentSlotType.LEGS, props()));
    public static final RegistryObject<Item> SOLARITE_BOOTS = ITEMS.register("solarite_boots",
            () -> new ModArmorItem(ModArmorMaterial.SOLARITE, EquipmentSlotType.FEET, props()));

    // Emberite
    public static final RegistryObject<Item> EMBERITE_INGOT = ITEMS.register("emberite_ingot", ModItems::simpleItem);
    public static final RegistryObject<Item> EMBERITE_NUGGET = ITEMS.register("emberite_nugget", ModItems::simpleItem);
    public static final RegistryObject<Item> EMBERITE_DUST = ITEMS.register("emberite_dust", ModItems::simpleItem);

    public static final RegistryObject<Item> EMBERITE_ORE = ITEMS.register("emberite_ore",
            () -> new OreBlockItem(ModBlocks.EMBERITE_ORE.get(), props(), "emberite"));
    public static final RegistryObject<Item> EMBERITE_BLOCK = ITEMS.register("emberite_block", () -> blockItem(ModBlocks.EMBERITE_BLOCK.get()));

    // Emberite tools: smithing table upgrade of the solarite tools (keeps enchantments).
    // Final damage = 1 (base) + 6 (tier) + modifier: sword 35, axe 37, others 12.
    public static final RegistryObject<Item> EMBERITE_SWORD = ITEMS.register("emberite_sword",
            () -> new SwordItem(ModItemTier.EMBERITE, 28, -2.8F, props()));
    public static final RegistryObject<Item> EMBERITE_PICKAXE = ITEMS.register("emberite_pickaxe",
            () -> new PickaxeItem(ModItemTier.EMBERITE, 5, -2.8F, props()));
    public static final RegistryObject<Item> EMBERITE_AXE = ITEMS.register("emberite_axe",
            () -> new AxeItem(ModItemTier.EMBERITE, 30.0F, -3.0F, props()));
    public static final RegistryObject<Item> EMBERITE_SHOVEL = ITEMS.register("emberite_shovel",
            () -> new ShovelItem(ModItemTier.EMBERITE, 5.0F, -3.0F, props()));
    public static final RegistryObject<Item> EMBERITE_HOE = ITEMS.register("emberite_hoe",
            () -> new HoeItem(ModItemTier.EMBERITE, 5, 0.0F, props()));

    // Emberite armor (solarite piece + emberite ingots, keeps enchantments): 5/8/10/5, toughness 4.5, knockback 0.2.
    public static final RegistryObject<Item> EMBERITE_HELMET = ITEMS.register("emberite_helmet",
            () -> new ModArmorItem(ModArmorMaterial.EMBERITE, EquipmentSlotType.HEAD, props()));
    public static final RegistryObject<Item> EMBERITE_CHESTPLATE = ITEMS.register("emberite_chestplate",
            () -> new ModArmorItem(ModArmorMaterial.EMBERITE, EquipmentSlotType.CHEST, props()));
    public static final RegistryObject<Item> EMBERITE_LEGGINGS = ITEMS.register("emberite_leggings",
            () -> new ModArmorItem(ModArmorMaterial.EMBERITE, EquipmentSlotType.LEGS, props()));
    public static final RegistryObject<Item> EMBERITE_BOOTS = ITEMS.register("emberite_boots",
            () -> new ModArmorItem(ModArmorMaterial.EMBERITE, EquipmentSlotType.FEET, props()));

    // Titanium
    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot", ModItems::simpleItem);
    public static final RegistryObject<Item> TITANIUM_NUGGET = ITEMS.register("titanium_nugget", ModItems::simpleItem);
    public static final RegistryObject<Item> TITANIUM_DUST = ITEMS.register("titanium_dust", ModItems::simpleItem);

    public static final RegistryObject<Item> TITANIUM_ORE = ITEMS.register("titanium_ore",
            () -> new OreBlockItem(ModBlocks.TITANIUM_ORE.get(), props(), "titanium"));
    public static final RegistryObject<Item> TITANIUM_BLOCK = ITEMS.register("titanium_block", () -> blockItem(ModBlocks.TITANIUM_BLOCK.get()));

    // Titanium tools: smithing table upgrade of the emberite tools. Unbreakable, with special abilities.
    // Final damage = 1 (base) + 7 (tier) + modifier: sword 45, axe 47, others 15.
    public static final RegistryObject<Item> TITANIUM_SWORD = ITEMS.register("titanium_sword",
            () -> new TitaniumSwordItem(ModItemTier.TITANIUM, 37, -3.0F, props()));
    public static final RegistryObject<Item> TITANIUM_PICKAXE = ITEMS.register("titanium_pickaxe",
            () -> new TitaniumPickaxeItem(ModItemTier.TITANIUM, 7, -2.8F, props()));
    public static final RegistryObject<Item> TITANIUM_AXE = ITEMS.register("titanium_axe",
            () -> new TitaniumAxeItem(ModItemTier.TITANIUM, 39.0F, -3.0F, props()));
    public static final RegistryObject<Item> TITANIUM_SHOVEL = ITEMS.register("titanium_shovel",
            () -> new TitaniumShovelItem(ModItemTier.TITANIUM, 7.0F, -3.0F, props()));
    public static final RegistryObject<Item> TITANIUM_HOE = ITEMS.register("titanium_hoe",
            () -> new TitaniumHoeItem(ModItemTier.TITANIUM, 7, 0.0F, props()));

    // Titanium armor (emberite piece + titanium ingots, keeps enchantments): 6/9/9/6, toughness 5, knockback 0.25. Unbreakable.
    public static final RegistryObject<Item> TITANIUM_HELMET = ITEMS.register("titanium_helmet",
            () -> new ModArmorItem(ModArmorMaterial.TITANIUM, EquipmentSlotType.HEAD, props()));
    public static final RegistryObject<Item> TITANIUM_CHESTPLATE = ITEMS.register("titanium_chestplate",
            () -> new ModArmorItem(ModArmorMaterial.TITANIUM, EquipmentSlotType.CHEST, props()));
    public static final RegistryObject<Item> TITANIUM_LEGGINGS = ITEMS.register("titanium_leggings",
            () -> new ModArmorItem(ModArmorMaterial.TITANIUM, EquipmentSlotType.LEGS, props()));
    public static final RegistryObject<Item> TITANIUM_BOOTS = ITEMS.register("titanium_boots",
            () -> new ModArmorItem(ModArmorMaterial.TITANIUM, EquipmentSlotType.FEET, props()));

    // Titanium Star tools: titanium tool + Titanium Star in the smithing table. Same abilities,
    // mining speed 26, 220 damage sword (1 + 7 + 212), 47 axe, 45 pickaxe/shovel/hoe (1 + 7 + 37). Animated textures.
    public static final RegistryObject<Item> TITANIUM_STAR_SWORD = ITEMS.register("titanium_star_sword",
            () -> new TitaniumSwordItem(ModItemTier.TITANIUM_STAR, 212, -3.0F, props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> TITANIUM_STAR_PICKAXE = ITEMS.register("titanium_star_pickaxe",
            () -> new TitaniumPickaxeItem(ModItemTier.TITANIUM_STAR, 37, -2.8F, props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> TITANIUM_STAR_AXE = ITEMS.register("titanium_star_axe",
            () -> new TitaniumAxeItem(ModItemTier.TITANIUM_STAR, 39.0F, -3.0F, props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> TITANIUM_STAR_SHOVEL = ITEMS.register("titanium_star_shovel",
            () -> new TitaniumShovelItem(ModItemTier.TITANIUM_STAR, 37.0F, -3.0F, props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> TITANIUM_STAR_HOE = ITEMS.register("titanium_star_hoe",
            () -> new TitaniumHoeItem(ModItemTier.TITANIUM_STAR, 37, 0.0F, props().rarity(Rarity.EPIC)));

    // Titanium Star armor: titanium piece + Titanium Star in the smithing table. Unbreakable, extra abilities
    // and 90% less damage with the full set (see StarArmorEvents).
    public static final RegistryObject<Item> TITANIUM_STAR_HELMET = ITEMS.register("titanium_star_helmet",
            () -> new ModArmorItem(ModArmorMaterial.TITANIUM_STAR, EquipmentSlotType.HEAD, props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> TITANIUM_STAR_CHESTPLATE = ITEMS.register("titanium_star_chestplate",
            () -> new ModArmorItem(ModArmorMaterial.TITANIUM_STAR, EquipmentSlotType.CHEST, props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> TITANIUM_STAR_LEGGINGS = ITEMS.register("titanium_star_leggings",
            () -> new ModArmorItem(ModArmorMaterial.TITANIUM_STAR, EquipmentSlotType.LEGS, props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> TITANIUM_STAR_BOOTS = ITEMS.register("titanium_star_boots",
            () -> new ModArmorItem(ModArmorMaterial.TITANIUM_STAR, EquipmentSlotType.FEET, props().rarity(Rarity.EPIC)));

    // Emberium: emberite + titanium alloy. No recipes yet (will come from the Titan Factory).
    public static final RegistryObject<Item> EMBERIUM_INGOT = ITEMS.register("emberium_ingot", ModItems::simpleItem);
    public static final RegistryObject<Item> EMBERIUM_DUST = ITEMS.register("emberium_dust", ModItems::simpleItem);
    public static final RegistryObject<Item> EMBERIUM_BLOCK = ITEMS.register("emberium_block", () -> blockItem(ModBlocks.EMBERIUM_BLOCK.get()));

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

    public static final RegistryObject<Item> TITAN_FACTORY = ITEMS.register("titan_factory", () -> blockItem(ModBlocks.TITAN_FACTORY.get()));
    public static final RegistryObject<Item> TITAN_CRAFTER = ITEMS.register("titan_crafter", () -> blockItem(ModBlocks.TITAN_CRAFTER.get()));

    // Final items (modpack endgame components; their recipes come from KubeJS in the modpack).
    // Textures live in textures/item/finalitens/.
    public static final RegistryObject<Item> SOURCE_CRYSTAL = ITEMS.register("source_crystal",
            () -> new Item(props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> SPONGE_BOB = ITEMS.register("sponge_bob",
            () -> new Item(props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> ANCESTRAL_SEED = ITEMS.register("ancestral_seed",
            () -> new Item(props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> WORLD_CORE = ITEMS.register("world_core",
            () -> new Item(props().rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> RAID_KING = ITEMS.register("raid_king",
            () -> new RaidKingItem(props().rarity(Rarity.EPIC).stacksTo(1)));

    // Titan Factory upgrade: each one installed doubles the machine speed (see TitanFactoryTileEntity).
    public static final RegistryObject<Item> SPEED_UPGRADE = ITEMS.register("speed_upgrade", ModItems::simpleItem);

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
