package com.titanmodpack.titanores.event;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.item.TitaniumSwordItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class ToolEvents {

    // Like vanilla Fire Aspect: ignite before the damage, so a one-hit kill still drops cooked food.
    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        PlayerEntity player = event.getPlayer();
        Entity target = event.getTarget();
        if (player.level.isClientSide || !(player.getMainHandItem().getItem() instanceof TitaniumSwordItem)) {
            return;
        }
        if (target instanceof LivingEntity && !target.isOnFire() && !target.fireImmune()) {
            target.setSecondsOnFire(1);
        }
    }
}
