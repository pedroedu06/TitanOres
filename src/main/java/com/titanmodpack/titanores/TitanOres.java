package com.titanmodpack.titanores;

import com.titanmodpack.titanores.client.ModKeyBindings;
import com.titanmodpack.titanores.compat.CuriosCompat;
import com.titanmodpack.titanores.network.ModNetwork;
import com.titanmodpack.titanores.init.ModBlocks;
import com.titanmodpack.titanores.init.ModItems;
import com.titanmodpack.titanores.init.ModLootModifiers;
import com.titanmodpack.titanores.init.ModRecipes;
import com.titanmodpack.titanores.init.ModTileEntities;
import com.titanmodpack.titanores.world.ModOreGeneration;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Main mod class. The @Mod value must match mod_id in gradle.properties.
@Mod(TitanOres.MOD_ID)
public class TitanOres {
    public static final String MOD_ID = "titanores";

    public static final Logger LOGGER = LogManager.getLogger();

    public TitanOres() {
        // Mod event bus: mod loading events (registries, setup, etc.).
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModTileEntities.TILE_ENTITIES.register(modEventBus);
        ModLootModifiers.LOOT_MODIFIERS.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);
        ModNetwork.register();

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::enqueueImc);

        // Forge event bus: in-game events (block break, world join, etc.).
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Runs on both client and server, after all registries are populated.
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Titan Ores loading...");
        // Worldgen registries are not thread-safe, so they are registered on the main thread.
        event.enqueueWork(ModOreGeneration::registerFeatures);
    }

    // Messages to other mods. Curios is optional, so only talk to it when it is installed.
    private void enqueueImc(final InterModEnqueueEvent event) {
        if (ModList.get().isLoaded(CuriosCompat.MOD_ID)) {
            CuriosCompat.sendImc();
        }
    }

    // Client only (rendering, key bindings, screens).
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Titan Ores client setup");
        ModKeyBindings.register();
    }
}
