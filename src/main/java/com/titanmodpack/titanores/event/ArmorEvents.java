package com.titanmodpack.titanores.event;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.item.ModArmorItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Passive abilities of each armor piece (any tier of the mod's armor).
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class ArmorEvents {

    // Helmet: aqua affinity without the enchantment (vanilla mines 5x slower with the eyes underwater).
    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        PlayerEntity player = event.getPlayer();
        if (player.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(player)
                && ModArmorItem.isWorn(player, EquipmentSlotType.HEAD)) {
            event.setNewSpeed(event.getNewSpeed() * 5.0F);
        }
    }

    // Chestplate: wither can't be applied.
    @SubscribeEvent
    public static void onPotionApplicable(PotionEvent.PotionApplicableEvent event) {
        if (event.getPotionEffect().getEffect() == Effects.WITHER && ModArmorItem.isWorn(event.getEntityLiving(), EquipmentSlotType.CHEST)) {
            event.setResult(Event.Result.DENY);
        }
    }

    // Leggings: no fire or lava damage. Boots: no fall damage.
    @SubscribeEvent
    public static void onAttack(LivingAttackEvent event) {
        LivingEntity entity = event.getEntityLiving();
        if (event.getSource().isFire() && ModArmorItem.isWorn(entity, EquipmentSlotType.LEGS)) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onFall(LivingFallEvent event) {
        if (ModArmorItem.isWorn(event.getEntityLiving(), EquipmentSlotType.FEET)) {
            event.setCanceled(true);
        }
    }

    // Clears wither already active when the chestplate is put on, and the burning overlay with the leggings.
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level.isClientSide) {
            return;
        }
        if (player.hasEffect(Effects.WITHER) && ModArmorItem.isWorn(player, EquipmentSlotType.CHEST)) {
            player.removeEffect(Effects.WITHER);
        }
        if (player.isOnFire() && ModArmorItem.isWorn(player, EquipmentSlotType.LEGS)) {
            player.clearFire();
        }
    }
}
