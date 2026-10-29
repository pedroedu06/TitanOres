package com.titanmods.titanores.init;

import com.titanmods.titanores.TitanOres;
import com.titanmods.titanores.recipe.UpgradeShapedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipes {
    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TitanOres.MOD_ID);

    public static final RegistryObject<UpgradeShapedRecipe.Serializer> UPGRADE_SHAPED =
            SERIALIZERS.register("upgrade_shaped", UpgradeShapedRecipe.Serializer::new);

}
