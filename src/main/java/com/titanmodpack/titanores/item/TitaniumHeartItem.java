package com.titanmodpack.titanores.item;

import com.titanmodpack.titanores.event.PlayerHealthEvents;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

// Eating it permanently adds 2 hearts of max health (no limit besides the vanilla 1024 cap).
public class TitaniumHeartItem extends Item {
    public TitaniumHeartItem(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, World world, LivingEntity entity) {
        if (!world.isClientSide && entity instanceof PlayerEntity) {
            PlayerHealthEvents.addHeartItem((PlayerEntity) entity);
        }
        return super.finishUsingItem(stack, world, entity);
    }
}
