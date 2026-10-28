package com.titanmodpack.titanores.event;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.block.EssenceCropBlock;
import net.minecraft.item.Items;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Essence crops refuse vanilla bone meal (by hand or dispenser); special growth items from other mods still work.
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class CropEvents {
    @SubscribeEvent
    public static void onBonemeal(BonemealEvent event) {
        if (event.getBlock().getBlock() instanceof EssenceCropBlock && event.getStack().getItem() == Items.BONE_MEAL) {
            event.setCanceled(true);
        }
    }
}
