package com.titanmodpack.titanores.network;

import com.titanmodpack.titanores.event.StarArmorEvents;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

// Sent by the client when the night vision key is pressed. No data: the server checks the worn helmet.
public class ToggleNightVisionPacket {
    public static void handle(ToggleNightVisionPacket message, Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            ServerPlayerEntity player = context.get().getSender();
            if (player != null) {
                StarArmorEvents.toggleNightVision(player);
            }
        });
        context.get().setPacketHandled(true);
    }
}
