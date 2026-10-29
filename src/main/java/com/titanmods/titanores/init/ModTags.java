package com.titanmods.titanores.init;

import com.titanmods.titanores.TitanOres;
import net.minecraft.block.Block;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;

public class ModTags {
    // Blocks that machines/fake players are not allowed to break (data/titanores/tags/blocks/manual_mining_only.json).
    public static final Tags.IOptionalNamedTag<Block> MANUAL_MINING_ONLY =
            BlockTags.createOptional(new ResourceLocation(TitanOres.MOD_ID, "manual_mining_only"));

    // Soils the essence crops grow on (data/titanores/tags/blocks/essence_crop_soil.json). Titan Utilities adds its Blue Soil.
    public static final Tags.IOptionalNamedTag<Block> ESSENCE_CROP_SOIL =
            BlockTags.createOptional(new ResourceLocation(TitanOres.MOD_ID, "essence_crop_soil"));
}
