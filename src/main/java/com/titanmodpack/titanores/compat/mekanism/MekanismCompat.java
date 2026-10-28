package com.titanmodpack.titanores.compat.mekanism;

import com.titanmodpack.titanores.TitanOres;
import mekanism.api.chemical.slurry.Slurry;
import mekanism.api.chemical.slurry.SlurryBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;

// Mekanism 5x ore processing: dirty/clean slurries for each ore (recipes in data/titanores/recipes/processing).
// Only loaded when Mekanism is installed (always guard calls with ModList.isLoaded("mekanism")).
public class MekanismCompat {
    public static final String MOD_ID = "mekanism";
    private static final String[] ORES = {"solarite", "emberite", "titanium"};

    private static final DeferredRegister<Slurry> SLURRIES = DeferredRegister.create(Slurry.class, TitanOres.MOD_ID);

    static {
        for (String ore : ORES) {
            ResourceLocation oreTag = new ResourceLocation("forge", "ores/" + ore);
            // Clean slurry uses the ore's "cold" liquid texture; the dirty one is a darker copy of it.
            SLURRIES.register("dirty_" + ore, () -> new Slurry(SlurryBuilder.builder(texture("dirty_" + ore + "_still")).ore(oreTag)));
            SLURRIES.register("clean_" + ore, () -> new Slurry(SlurryBuilder.builder(texture("cold_" + ore + "_still")).ore(oreTag)));
        }
    }

    public static void register(IEventBus modBus) {
        SLURRIES.register(modBus);
    }

    private static ResourceLocation texture(String name) {
        return new ResourceLocation(TitanOres.MOD_ID, "item/mekanism/fluid/" + name);
    }
}
