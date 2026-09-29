package com.titanmodpack.titanores;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// Classe principal do mod. O valor de @Mod precisa ser igual ao mod_id do gradle.properties.
@Mod(TitanOres.MOD_ID)
public class TitanOres {
    public static final String MOD_ID = "titanores";

    // Logger para escrever mensagens no console/log do jogo.
    public static final Logger LOGGER = LogManager.getLogger();

    public TitanOres() {
        // "Mod event bus": eventos de carregamento do mod (registros, setup, etc.).
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::clientSetup);

        // "Forge event bus": eventos do jogo em andamento (quebrar bloco, entrar no mundo, etc.).
        MinecraftForge.EVENT_BUS.register(this);
    }

    // Roda no cliente e no servidor, depois que tudo foi registrado.
    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Titan Ores carregando...");
    }

    // Roda apenas no cliente (coisas visuais: renderização, teclas, telas).
    private void clientSetup(final FMLClientSetupEvent event) {
        LOGGER.info("Titan Ores: setup do cliente");
    }
}
