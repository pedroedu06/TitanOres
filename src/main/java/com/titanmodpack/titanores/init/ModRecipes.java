package com.titanmodpack.titanores.init;

import com.titanmodpack.titanores.TitanOres;
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
}
