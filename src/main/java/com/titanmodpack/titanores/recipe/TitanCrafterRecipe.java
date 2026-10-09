package com.titanmodpack.titanores.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
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

import java.util.HashMap;
import java.util.Map;

// Titan Crafter recipe: a fixed 9x6 shaped pattern. Pattern row r, column c is grid slot r * 9 + c; there is no
// offset or mirroring, and a space means that slot must be empty. Each used slot consumes 1 item.
// The energy is spent gradually: energy / time per tick.
public class TitanCrafterRecipe implements IRecipe<IInventory> {
    public static final int WIDTH = 9;
    public static final int HEIGHT = 6;
    public static final int SIZE = WIDTH * HEIGHT;

    private final ResourceLocation id;
    private final NonNullList<Ingredient> grid;
    private final int energy;
    private final int time;
    private final ItemStack result;

    public TitanCrafterRecipe(ResourceLocation id, NonNullList<Ingredient> grid, int energy, int time, ItemStack result) {
        this.id = id;
        this.grid = grid;
        this.energy = energy;
        this.time = time;
        this.result = result;
    }

    @Override
    public boolean matches(IInventory inventory, World world) {
        for (int i = 0; i < SIZE; i++) {
            if (!grid.get(i).test(inventory.getItem(i))) {
                return false;
            }
        }
        return true;
    }

    // Removes 1 item from every slot used by the pattern.
    public void consumeInputs(IItemHandlerModifiable items) {
        for (int i = 0; i < SIZE; i++) {
            if (!grid.get(i).isEmpty()) {
                items.extractItem(i, 1, false);
            }
        }
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
        return width >= WIDTH && height >= HEIGHT;
    }

    @Override
    public ItemStack getResultItem() {
        return result;
    }

    // All 54 positions in slot order (Ingredient.EMPTY for empty slots).
    @Override
    public NonNullList<Ingredient> getIngredients() {
        return grid;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModRecipes.TITAN_CRAFTING_SERIALIZER.get();
    }

    @Override
    public IRecipeType<?> getType() {
        return ModRecipes.TITAN_CRAFTING_TYPE;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<TitanCrafterRecipe> {
        @Override
        public TitanCrafterRecipe fromJson(ResourceLocation id, JsonObject json) {
            Map<Character, Ingredient> key = readKey(JSONUtils.getAsJsonObject(json, "key"));
            JsonArray pattern = JSONUtils.getAsJsonArray(json, "pattern");
            if (pattern.size() != HEIGHT) {
                throw new JsonSyntaxException("Titan Crafter pattern must have exactly " + HEIGHT + " rows");
            }
            NonNullList<Ingredient> grid = NonNullList.withSize(SIZE, Ingredient.EMPTY);
            for (int row = 0; row < HEIGHT; row++) {
                String line = JSONUtils.convertToString(pattern.get(row), "pattern[" + row + "]");
                if (line.length() != WIDTH) {
                    throw new JsonSyntaxException("Titan Crafter pattern rows must have exactly " + WIDTH + " characters");
                }
                for (int col = 0; col < WIDTH; col++) {
                    char symbol = line.charAt(col);
                    if (symbol == ' ') {
                        continue;
                    }
                    Ingredient ingredient = key.get(symbol);
                    if (ingredient == null) {
                        throw new JsonSyntaxException("Titan Crafter pattern uses undefined symbol '" + symbol + "'");
                    }
                    grid.set(row * WIDTH + col, ingredient);
                }
            }
            int energy = JSONUtils.getAsInt(json, "energy");
            int time = Math.max(1, JSONUtils.getAsInt(json, "time", 200));
            ItemStack result = ShapedRecipe.itemFromJson(JSONUtils.getAsJsonObject(json, "result"));
            return new TitanCrafterRecipe(id, grid, energy, time, result);
        }

        private static Map<Character, Ingredient> readKey(JsonObject json) {
            Map<Character, Ingredient> key = new HashMap<>();
            for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                if (entry.getKey().length() != 1 || entry.getKey().equals(" ")) {
                    throw new JsonSyntaxException("Invalid Titan Crafter key symbol '" + entry.getKey() + "'");
                }
                key.put(entry.getKey().charAt(0), Ingredient.fromJson(entry.getValue()));
            }
            return key;
        }

        @Override
        public TitanCrafterRecipe fromNetwork(ResourceLocation id, PacketBuffer buffer) {
            NonNullList<Ingredient> grid = NonNullList.withSize(SIZE, Ingredient.EMPTY);
            for (int i = 0; i < SIZE; i++) {
                grid.set(i, Ingredient.fromNetwork(buffer));
            }
            int energy = buffer.readVarInt();
            int time = buffer.readVarInt();
            ItemStack result = buffer.readItem();
            return new TitanCrafterRecipe(id, grid, energy, time, result);
        }

        @Override
        public void toNetwork(PacketBuffer buffer, TitanCrafterRecipe recipe) {
            for (Ingredient ingredient : recipe.grid) {
                ingredient.toNetwork(buffer);
            }
            buffer.writeVarInt(recipe.energy);
            buffer.writeVarInt(recipe.time);
            buffer.writeItem(recipe.result);
        }
    }
}
