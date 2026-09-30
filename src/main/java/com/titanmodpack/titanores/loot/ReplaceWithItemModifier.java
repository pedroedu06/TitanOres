package com.titanmodpack.titanores.loot;

import com.google.gson.JsonObject;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.List;

// Global loot modifier: when its conditions pass, the generated loot is replaced by a single item.
public class ReplaceWithItemModifier extends LootModifier {
    private final Item item;

    public ReplaceWithItemModifier(ILootCondition[] conditions, Item item) {
        super(conditions);
        this.item = item;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        generatedLoot.clear();
        generatedLoot.add(new ItemStack(item));
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<ReplaceWithItemModifier> {
        @Override
        public ReplaceWithItemModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(JSONUtils.getAsString(object, "item")));
            return new ReplaceWithItemModifier(conditions, item);
        }

        @Override
        public JsonObject write(ReplaceWithItemModifier instance) {
            JsonObject json = makeConditions(instance.conditions);
            json.addProperty("item", ForgeRegistries.ITEMS.getKey(instance.item).toString());
            return json;
        }
    }
}
