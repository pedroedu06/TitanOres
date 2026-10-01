package com.titanmodpack.titanores.init;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.recipe.UpgradeShapedRecipe;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModRecipes {
    public static final DeferredRegister<IRecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, TitanOres.MOD_ID);

    public static final RegistryObject<UpgradeShapedRecipe.Serializer> UPGRADE_SHAPED =
            SERIALIZERS.register("upgrade_shaped", UpgradeShapedRecipe.Serializer::new);
}
