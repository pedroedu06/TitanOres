package com.titanmodpack.titanores.compat;

import com.titanmodpack.titanores.item.SolariteMagnetItem;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.fml.InterModComms;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotTypeMessage;
import top.theillusivec4.curios.api.SlotTypePreset;

// Only loaded when Curios is installed (always guard calls with ModList.isLoaded("curios")).
public class CuriosCompat {
    public static final String MOD_ID = "curios";

    // Asks Curios to create the "charm" slot, where the magnet can be equipped.
    public static void sendImc() {
        InterModComms.sendTo(CuriosApi.MODID, SlotTypeMessage.REGISTER_TYPE, () -> SlotTypePreset.CHARM.getMessageBuilder().build());
    }

    public static void tickMagnet(PlayerEntity player) {
        CuriosApi.getCuriosHelper()
                .findEquippedCurio(stack -> stack.getItem() instanceof SolariteMagnetItem && SolariteMagnetItem.isActive(stack), player)
                .ifPresent(found -> SolariteMagnetItem.pullNearby(player));
    }
}
