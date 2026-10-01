package com.titanmodpack.titanores.item;

import com.titanmodpack.titanores.init.ModItems;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IArmorMaterial;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;

import java.util.function.Supplier;

// Armor tiers. The name "titanores:<material>" makes Minecraft load the worn textures from
// assets/titanores/textures/models/armor/<material>_layer_1.png (helmet, chestplate, boots) and _layer_2.png (leggings).
// Vanilla caps: total armor 30, total toughness 20, knockback resistance 1.0.
public enum ModArmorMaterial implements IArmorMaterial {
    // Defense order: boots, leggings, chestplate, helmet. Durability is the same for every piece.
    SOLARITE("titanores:solarite", 12000, new int[]{4, 9, 7, 4}, 85, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.5F, 0.15F,
            () -> Ingredient.of(ModItems.SOLARITE_INGOT.get())),
    EMBERITE("titanores:emberite", 24000, new int[]{5, 10, 8, 5}, 105, SoundEvents.ARMOR_EQUIP_NETHERITE, 4.5F, 0.2F,
            () -> Ingredient.of(ModItems.EMBERITE_INGOT.get())),
    // 0 durability = not damageable, so titanium armor is unbreakable.
    TITANIUM("titanores:titanium", 0, new int[]{6, 9, 9, 6}, 125, SoundEvents.ARMOR_EQUIP_NETHERITE, 5.0F, 0.25F,
            () -> Ingredient.of(ModItems.TITANIUM_INGOT.get())),
    // 3000 per piece is shown in the tooltip but the game caps total armor at 30; the real
    // protection of this set is the extra damage reduction in ArmorEvents.
    TITANIUM_STAR("titanores:titanium_star", 0, new int[]{3000, 3000, 3000, 3000}, 145, SoundEvents.ARMOR_EQUIP_NETHERITE, 5.0F, 0.25F,
            () -> Ingredient.of(ModItems.TITANIUM_INGOT.get()));

    private final String name;
    private final int durability;
    private final int[] defense;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyValue<Ingredient> repairIngredient;

    ModArmorMaterial(String name, int durability, int[] defense, int enchantmentValue, SoundEvent sound,
                     float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durability = durability;
        this.defense = defense;
        this.enchantmentValue = enchantmentValue;
        this.sound = sound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = new LazyValue<>(repairIngredient);
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlotType slot) {
        return durability;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlotType slot) {
        return defense[slot.getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public SoundEvent getEquipSound() {
        return sound;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public float getToughness() {
        return toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return knockbackResistance;
    }
}
