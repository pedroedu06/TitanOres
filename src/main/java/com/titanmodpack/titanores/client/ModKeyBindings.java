package com.titanmodpack.titanores.client;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.network.ModNetwork;
import com.titanmodpack.titanores.network.ToggleNightVisionPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.util.InputMappings;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

// Key bindings of the mod (changeable in Options > Controls, category "Titan Ores").
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID, value = Dist.CLIENT)
public class ModKeyBindings {
    public static final KeyBinding NIGHT_VISION = new KeyBinding("key.titanores.night_vision",
            KeyConflictContext.IN_GAME, InputMappings.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.categories.titanores");

    public static void register() {
        ClientRegistry.registerKeyBinding(NIGHT_VISION);
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        while (NIGHT_VISION.consumeClick()) {
            if (Minecraft.getInstance().player != null) {
                ModNetwork.CHANNEL.sendToServer(new ToggleNightVisionPacket());
            }
        }
    }
}
