package com.titanmodpack.titanores.init;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.recipe.TitanCrafterRecipe;
import com.titanmodpack.titanores.recipe.TitanFactoryRecipe;
import com.titanmodpack.titanores.recipe.UpgradeShapedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipes {
    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TitanOres.MOD_ID);

    public static final RegistryObject<UpgradeShapedRecipe.Serializer> UPGRADE_SHAPED =
            SERIALIZERS.register("upgrade_shaped", UpgradeShapedRecipe.Serializer::new);

    // Titan Factory machine recipes (data/<namespace>/recipes, "type": "titanores:titan_factory").
    public static final IRecipeType<TitanFactoryRecipe> TITAN_FACTORY_TYPE = IRecipeType.register(TitanOres.MOD_ID + ":titan_factory");

    public static final RegistryObject<TitanFactoryRecipe.Serializer> TITAN_FACTORY_SERIALIZER =
            SERIALIZERS.register("titan_factory", TitanFactoryRecipe.Serializer::new);

    // Titan Crafter machine recipes ("type": "titanores:titan_crafting", fixed 9x6 pattern).
    public static final IRecipeType<TitanCrafterRecipe> TITAN_CRAFTING_TYPE = IRecipeType.register(TitanOres.MOD_ID + ":titan_crafting");

    public static final RegistryObject<TitanCrafterRecipe.Serializer> TITAN_CRAFTING_SERIALIZER =
            SERIALIZERS.register("titan_crafting", TitanCrafterRecipe.Serializer::new);
}
