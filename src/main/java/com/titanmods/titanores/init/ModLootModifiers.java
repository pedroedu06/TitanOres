package com.titanmods.titanores.init;

import com.titanmods.titanores.TitanOres;
import com.titanmods.titanores.loot.AutoSmeltModifier;
import com.titanmods.titanores.loot.ReplaceWithItemModifier;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModLootModifiers {
    public static final DeferredRegister<GlobalLootModifierSerializer<?>> LOOT_MODIFIERS =
            DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, TitanOres.MOD_ID);

    public static final RegistryObject<GlobalLootModifierSerializer<?>> REPLACE_WITH_ITEM =
            LOOT_MODIFIERS.register("replace_with_item", ReplaceWithItemModifier.Serializer::new);

    public static final RegistryObject<GlobalLootModifierSerializer<?>> AUTO_SMELT =
            LOOT_MODIFIERS.register("auto_smelt", AutoSmeltModifier.Serializer::new);
}
