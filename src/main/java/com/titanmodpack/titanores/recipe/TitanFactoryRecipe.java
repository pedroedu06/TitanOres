package com.titanmodpack.titanores.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.titanmodpack.titanores.init.ModRecipes;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.registries.ForgeRegistryEntry;

// Titan Factory recipe: two columns of 3 positions (slots 0-2 left, 3-5 right, top to bottom) -> one result.
// Each non-empty position consumes 1 item. "mirrored" also accepts the columns swapped.
// The energy is spent gradually: energy / time per tick.
public class TitanFactoryRecipe implements IRecipe<IInventory> {
    public static final int COLUMN_SIZE = 3;

    private final ResourceLocation id;
    private final Ingredient[] left;
    private final Ingredient[] right;
    private final boolean mirrored;
    private final int energy;
    private final int time;
    private final ItemStack result;

    public TitanFactoryRecipe(ResourceLocation id, Ingredient[] left, Ingredient[] right, boolean mirrored, int energy, int time, ItemStack result) {
        this.id = id;
        this.left = left;
        this.right = right;
        this.mirrored = mirrored;
        this.energy = energy;
        this.time = time;
        this.result = result;
    }

    @Override
    public boolean matches(IInventory inventory, World world) {
        return matchesLayout(inventory, false) || (mirrored && matchesLayout(inventory, true));
    }

    private boolean matchesLayout(IInventory inventory, boolean swapped) {
        for (int i = 0; i < COLUMN_SIZE; i++) {
            if (!left(swapped)[i].test(inventory.getItem(i)) || !right(swapped)[i].test(inventory.getItem(COLUMN_SIZE + i))) {
                return false;
            }
        }
        return true;
    }

    private Ingredient[] left(boolean swapped) {
        return swapped ? right : left;
    }

    private Ingredient[] right(boolean swapped) {
        return swapped ? left : right;
    }

    // Removes 1 item from every position used by the recipe (in whichever orientation matched).
    public void consumeInputs(IInventory inventory, IItemHandlerModifiable items) {
        boolean swapped = !matchesLayout(inventory, false);
        for (int i = 0; i < COLUMN_SIZE; i++) {
            if (!left(swapped)[i].isEmpty()) {
                items.extractItem(i, 1, false);
            }
            if (!right(swapped)[i].isEmpty()) {
                items.extractItem(COLUMN_SIZE + i, 1, false);
            }
        }
    }

    public boolean isMirrored() {
        return mirrored;
    }

    public int getEnergy() {
        return energy;
    }

    public int getTime() {
        return time;
    }

    public int getEnergyPerTick() {
        return (int) Math.ceil((double) energy / time);
    }

    @Override
    public ItemStack assemble(IInventory inventory) {
        return result.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        NonNullList<Ingredient> list = NonNullList.withSize(COLUMN_SIZE * 2, Ingredient.EMPTY);
        for (int i = 0; i < COLUMN_SIZE; i++) {
            list.set(i, left[i]);
            list.set(COLUMN_SIZE + i, right[i]);
        }
        return list;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.TITAN_FACTORY_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.TITAN_FACTORY_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<TitanFactoryRecipe> {
        @Override
        public TitanFactoryRecipe fromJson(ResourceLocation id, JsonObject json) {
            Ingredient[] left = readColumn(json, "left");
            Ingredient[] right = readColumn(json, "right");
            boolean mirrored = JSONUtils.getAsBoolean(json, "mirrored", false);
            int energy = JSONUtils.getAsInt(json, "energy");
            int time = Math.max(1, JSONUtils.getAsInt(json, "time", 200));
            ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            return new TitanFactoryRecipe(id, left, right, mirrored, energy, time, result);
        }

        private static Ingredient[] readColumn(JsonObject json, String key) {
            Ingredient[] column = new Ingredient[COLUMN_SIZE];
            JsonArray array = json.has(key) ? JSONUtils.getAsJsonArray(json, key) : new JsonArray();
            if (array.size() > COLUMN_SIZE) {
                throw new IllegalArgumentException("Titan Factory column '" + key + "' has more than " + COLUMN_SIZE + " entries");
            }
            for (int i = 0; i < COLUMN_SIZE; i++) {
                JsonElement element = i < array.size() ? array.get(i) : null;
                column[i] = element == null || element.isJsonNull() ? Ingredient.EMPTY : Ingredient.fromJson(element);
            }
            return column;
        }

        @Override
        public TitanFactoryRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            Ingredient[] left = new Ingredient[COLUMN_SIZE];
            Ingredient[] right = new Ingredient[COLUMN_SIZE];
            for (int i = 0; i < COLUMN_SIZE; i++) {
                left[i] = Ingredient.fromNetwork(buffer);
            }
            for (int i = 0; i < COLUMN_SIZE; i++) {
                right[i] = Ingredient.fromNetwork(buffer);
            }
            boolean mirrored = buffer.readBoolean();
            int energy = buffer.readVarInt();
            int time = buffer.readVarInt();
            ItemStack result = buffer.readItem();
            return new TitanFactoryRecipe(id, left, right, mirrored, energy, time, result);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, TitanFactoryRecipe recipe) {
            for (Ingredient ingredient : recipe.left) {
                ingredient.toNetwork(buffer);
            }
            for (Ingredient ingredient : recipe.right) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeBoolean(recipe.mirrored);
            buffer.writeVarInt(recipe.energy);
            buffer.writeVarInt(recipe.time);
            buffer.writeItem(recipe.result);
        }
    }
}
