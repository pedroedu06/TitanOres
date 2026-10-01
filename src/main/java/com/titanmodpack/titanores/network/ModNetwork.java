package com.titanmodpack.titanores.network;

import com.titanmodpack.titanores.TitanOres;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

// Client <-> server messages of the mod.
public class ModNetwork {
    private static final String VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(TitanOres.MOD_ID, "main"), () -> VERSION, VERSION::equals, VERSION::equals);

    public static void register() {
        CHANNEL.registerMessage(0, ToggleNightVisionPacket.class,
                (message, buffer) -> {
                },
                buffer -> new ToggleNightVisionPacket(),
                ToggleNightVisionPacket::handle);
    }
}
