package com.titanmodpack.titanores.loot;

import com.google.gson.JsonObject;
import com.titanmodpack.titanores.item.AreaToolHelper;
import com.titanmodpack.titanores.item.TitaniumPickaxeItem;
import com.titanmodpack.titanores.item.TitaniumShovelItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Titanium pickaxe/shovel in Area Mode without Silk Touch: dropped ores (forge:ores) come out smelted.
public class AutoSmeltModifier extends LootModifier {
    public AutoSmeltModifier(ILootCondition[] conditions) {
        super(conditions);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        ItemStack tool = context.getParamOrNull(LootParameters.TOOL);
        if (tool == null
                || !(tool.getItem() instanceof TitaniumPickaxeItem || tool.getItem() instanceof TitaniumShovelItem)
                || !AreaToolHelper.isActive(tool)
                || EnchantmentHelper.getItemEnchantmentLevel(Enchantments.SILK_TOUCH, tool) > 0) {
            return generatedLoot;
        }
        ServerWorld world = context.getLevel();
        List<ItemStack> result = new ArrayList<>();
        float experience = 0.0F;
        for (ItemStack stack : generatedLoot) {
            if (stack.getItem().is(Tags.Items.ORES)) {
                Optional<FurnaceRecipe> recipe = world.getRecipeManager().getRecipeFor(IRecipeType.SMELTING, new Inventory(stack), world);
                if (recipe.isPresent()) {
                    ItemStack smelted = recipe.get().getResultItem().copy();
                    smelted.setCount(smelted.getCount() * stack.getCount());
                    result.add(smelted);
                    experience += recipe.get().getExperience() * stack.getCount();
                    continue;
                }
            }
            result.add(stack);
        }
        dropExperience(world, context.getParamOrNull(LootParameters.ORIGIN), experience);
        return result;
    }

    private static void dropExperience(ServerWorld world, Vector3d origin, float experience) {
        if (origin == null || experience <= 0.0F) {
            return;
        }
        int amount = MathHelper.floor(experience);
        if (world.random.nextFloat() < experience - amount) {
            amount++;
        }
        while (amount > 0) {
            int orb = ExperienceOrbEntity.getExperienceValue(amount);
            amount -= orb;
            world.addFreshEntity(new ExperienceOrbEntity(world, origin.x, origin.y, origin.z, orb));
        }
    }

    public static class Serializer extends GlobalLootModifierSerializer<AutoSmeltModifier> {
        @Override
        public AutoSmeltModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
            return new AutoSmeltModifier(conditions);
        }

        @Override
        public JsonObject write(AutoSmeltModifier instance) {
            return makeConditions(instance.conditions);
        }
    }
}
