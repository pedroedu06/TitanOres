package com.titanmodpack.titanores.event;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.init.ModTags;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.util.FakePlayer;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Mining machines (Mekanism Digital Miner, RFTools Builder, etc.) fire BreakEvent with a
// FakePlayer so protection mods can stop them. Cancel it for blocks in the manual_mining_only tag.
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class AutomationBlocker {
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        PlayerEntity player = event.getPlayer();
        if ((player == null || player instanceof FakePlayer) && event.getState().is(ModTags.MANUAL_MINING_ONLY)) {
            event.setCanceled(true);
        }
    }
}
