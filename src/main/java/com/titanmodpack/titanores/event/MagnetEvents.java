package com.titanmodpack.titanores.event;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.compat.CuriosCompat;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

// Magnet in a Curios slot. In the normal inventory it works through SolariteMagnetItem#inventoryTick.
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class MagnetEvents {
    private static final boolean CURIOS_LOADED = ModList.get().isLoaded(CuriosCompat.MOD_ID);

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (CURIOS_LOADED && event.phase == TickEvent.Phase.END && !event.player.level.isClientSide) {
            CuriosCompat.tickMagnet(event.player);
        }
    }
}
