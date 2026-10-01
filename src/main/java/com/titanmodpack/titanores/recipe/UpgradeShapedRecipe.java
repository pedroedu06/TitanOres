package com.titanmodpack.titanores.recipe;

import com.google.gson.JsonObject;
import com.titanmodpack.titanores.init.ModRecipes;
import com.titanmodpack.titanores.item.ModArmorItem;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;

// Shaped crafting recipe that keeps the NBT (enchantments, custom name) of the armor piece being upgraded.
// JSON format is the same as minecraft:crafting_shaped, with "type": "titanores:upgrade_shaped".
public class UpgradeShapedRecipe extends ShapedRecipe {
    public UpgradeShapedRecipe(ShapedRecipe base) {
        super(base.getId(), base.getGroup(), base.getWidth(), base.getHeight(), base.getIngredients(), base.getResultItem());
    }

    @Override
    public ItemStack assemble(CraftingInventory inventory) {
        ItemStack result = super.assemble(inventory);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.getItem() instanceof ModArmorItem && stack.hasTag()) {
                result.setTag(stack.getTag().copy());
                break;
            }
        }
        return result;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.UPGRADE_SHAPED.get();
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<UpgradeShapedRecipe> {
        @Override
        public UpgradeShapedRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new UpgradeShapedRecipe(IRecipeSerializer.SHAPED_RECIPE.fromJson(id, json));
        }

        @Nullable
        @Override
        public UpgradeShapedRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            ShapedRecipe base = IRecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buffer);
            return base == null ? null : new UpgradeShapedRecipe(base);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, UpgradeShapedRecipe recipe) {
            IRecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
