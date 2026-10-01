package com.titanmodpack.titanores.item;

import com.titanmodpack.titanores.init.ModItems;
import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.LazyValue;

import java.util.function.Supplier;

// Tool tiers. Harvest levels: 4 netherite, 5 solarite (mines emberite), 6 emberite (mines titanium).
public enum ModItemTier implements IItemTier {
    SOLARITE(5, 12000, 10.0F, 5.0F, 85, () -> Ingredient.of(ModItems.SOLARITE_INGOT.get())),
    EMBERITE(6, 24000, 15.0F, 6.0F, 85, () -> Ingredient.of(ModItems.EMBERITE_INGOT.get())),
    // 0 uses = not damageable, so titanium tools are unbreakable.
    TITANIUM(7, 0, 18.0F, 7.0F, 85, () -> Ingredient.of(ModItems.TITANIUM_INGOT.get()));

    private final int level;
    private final int uses;
    private final float speed;
    private final float damage;
    private final int enchantmentValue;
    private final LazyValue<Ingredient> repairIngredient;

    ModItemTier(int level, int uses, float speed, float damage, int enchantmentValue, Supplier<Ingredient> repairIngredient) {
        this.level = level;
        this.uses = uses;
        this.speed = speed;
        this.damage = damage;
        this.enchantmentValue = enchantmentValue;
        this.repairIngredient = new LazyValue<>(repairIngredient);
    }

    @Override
    public int getUses() {
        return uses;
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    @Override
    public float getAttackDamageBonus() {
        return damage;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getEnchantmentValue() {
        return enchantmentValue;
    }

    @Override
    public Ingredient getRepairIngredient() {
        return repairIngredient.get();
    }
}
