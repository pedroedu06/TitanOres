package com.titanmodpack.titanores.world;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.init.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.template.BlockMatchRuleTest;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class ModOreGeneration {
    private static final ResourceLocation WARPED_FOREST = new ResourceLocation("minecraft", "warped_forest");
    private static final ResourceLocation END_HIGHLANDS = new ResourceLocation("minecraft", "end_highlands");

    private static ConfiguredFeature<?, ?> solariteOre;
    private static ConfiguredFeature<?, ?> emberiteOre;
    private static ConfiguredFeature<?, ?> titaniumOre;

    // Rarer than diamond: small veins (size 4, usually 1-3 ores), Y=0 to Y=16,
    // and only 1 in 2 chunks gets a generation attempt.
    public static void registerFeatures() {
        solariteOre = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(TitanOres.MOD_ID, "solarite_ore"),
                Feature.ORE.configured(new OreFeatureConfig(
                                OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                                ModBlocks.SOLARITE_ORE.get().defaultBlockState(),
                                4))
                        .range(16)
                        .squared()
                        .chance(2));

        // Same settings as vanilla ancient debris: veins of up to 3 blocks, 2 attempts per chunk,
        // never exposed to air (NO_SURFACE_ORE). Y=95 to Y=125 (95 + random 0..30).
        emberiteOre = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(TitanOres.MOD_ID, "emberite_ore"),
                Feature.NO_SURFACE_ORE.configured(new OreFeatureConfig(
                                OreFeatureConfig.FillerBlockType.NETHER_ORE_REPLACEABLES,
                                ModBlocks.EMBERITE_ORE.get().defaultBlockState(),
                                3))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(95, 0, 31)))
                        .squared()
                        .count(2));

        // Ancient debris rarity inside End Highlands islands, Y=16 to Y=56 (16 + random 0..40).
        // NO_SURFACE_ORE keeps it away from air, so players have to dig for it.
        titaniumOre = Registry.register(WorldGenRegistries.CONFIGURED_FEATURE,
                new ResourceLocation(TitanOres.MOD_ID, "titanium_ore"),
                Feature.NO_SURFACE_ORE.configured(new OreFeatureConfig(
                                new BlockMatchRuleTest(Blocks.END_STONE),
                                ModBlocks.TITANIUM_ORE.get().defaultBlockState(),
                                3))
                        .decorated(Placement.RANGE.configured(new TopSolidRangeConfig(16, 0, 41)))
                        .squared()
                        .count(2));
    }

    // Solarite only generates in deep ocean biomes (deep_ocean, deep_cold_ocean,
    // deep_lukewarm_ocean, deep_frozen_ocean), which only exist in the Overworld.
    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void onBiomeLoading(BiomeLoadingEvent event) {
        if (solariteOre == null || event.getName() == null) {
            return;
        }
        if (event.getCategory() == Biome.Category.OCEAN && event.getName().getPath().startsWith("deep_")) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, solariteOre);
        }
        // Emberite: Warped Forest only (a Nether-only biome).
        if (WARPED_FOREST.equals(event.getName())) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, emberiteOre);
        }
        // Titanium: End Highlands only (an End-only biome).
        if (END_HIGHLANDS.equals(event.getName())) {
            event.getGeneration().addFeature(GenerationStage.Decoration.UNDERGROUND_DECORATION, titaniumOre);
        }
    }
}
