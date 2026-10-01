package com.titanmodpack.titanores.event;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.item.ModArmorItem;
import com.titanmodpack.titanores.item.ModArmorMaterial;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// Extra abilities of the Titanium Star armor:
// - full set: 90% less damage after armor/enchantments (/kill and the void still work);
// - helmet: infinite breath and toggleable night vision (key, see ModKeyBindings / ToggleNightVisionPacket);
// - chestplate: creative flight in survival.
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class StarArmorEvents {
    private static final float FULL_SET_DAMAGE_MULTIPLIER = 0.1F;
    private static final String NIGHT_VISION_KEY = "NightVision";
    private static final String FLIGHT_KEY = TitanOres.MOD_ID + ":star_flight";
    private static final int NIGHT_VISION_DURATION = 400;

    @SubscribeEvent
    public static void onDamage(LivingDamageEvent event) {
        if (!event.getSource().isBypassInvul() && ModArmorItem.fullSet(event.getEntityLiving()) == ModArmorMaterial.TITANIUM_STAR) {
            event.setAmount(event.getAmount() * FULL_SET_DAMAGE_MULTIPLIER);
        }
    }

    // Night vision starts on; the state is stored on the helmet.
    public static boolean isNightVisionOn(ItemStack helmet) {
        return !helmet.hasTag() || !helmet.getTag().contains(NIGHT_VISION_KEY) || helmet.getTag().getBoolean(NIGHT_VISION_KEY);
    }

    // Called on the server when the player presses the night vision key.
    public static void toggleNightVision(ServerPlayerEntity player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlotType.HEAD);
        if (!ModArmorItem.isMaterial(helmet, ModArmorMaterial.TITANIUM_STAR)) {
            return;
        }
        boolean on = !isNightVisionOn(helmet);
        helmet.getOrCreateTag().putBoolean(NIGHT_VISION_KEY, on);
        TranslationTextComponent state = new TranslationTextComponent(on ? "message.titanores.on" : "message.titanores.off");
        state.withStyle(on ? TextFormatting.GREEN : TextFormatting.RED);
        player.displayClientMessage(new TranslationTextComponent("message.titanores.night_vision", state), true);
        player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.EXPERIENCE_ORB_PICKUP,
                SoundCategory.PLAYERS, 0.5F, on ? 1.2F : 0.6F);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level.isClientSide) {
            return;
        }
        tickHelmet(player);
        tickFlight(player);
    }

    private static void tickHelmet(PlayerEntity player) {
        ItemStack helmet = player.getItemBySlot(EquipmentSlotType.HEAD);
        boolean starHelmet = ModArmorItem.isMaterial(helmet, ModArmorMaterial.TITANIUM_STAR);
        if (starHelmet) {
            player.setAirSupply(player.getMaxAirSupply());
        }
        EffectInstance nightVision = player.getEffect(Effects.NIGHT_VISION);
        if (starHelmet && isNightVisionOn(helmet)) {
            // Refresh before it reaches 200 ticks, when vanilla starts flickering the screen.
            if (nightVision == null || nightVision.getDuration() < 220) {
                player.addEffect(new EffectInstance(Effects.NIGHT_VISION, NIGHT_VISION_DURATION, 0, true, false, false));
            }
        } else if (nightVision != null && nightVision.isAmbient() && nightVision.getDuration() <= NIGHT_VISION_DURATION) {
            // Only remove the effect we gave (ambient, short); potions are left alone.
            player.removeEffect(Effects.NIGHT_VISION);
        }
    }

    private static void tickFlight(PlayerEntity player) {
        CompoundNBT data = player.getPersistentData();
        if (ModArmorItem.isMaterial(player.getItemBySlot(EquipmentSlotType.CHEST), ModArmorMaterial.TITANIUM_STAR)) {
            data.putBoolean(FLIGHT_KEY, true);
            if (!player.abilities.mayfly) {
                player.abilities.mayfly = true;
                player.onUpdateAbilities();
            }
        } else if (data.getBoolean(FLIGHT_KEY)) {
            // Only take away flight we granted, and never from creative/spectator players.
            data.remove(FLIGHT_KEY);
            if (!player.isCreative() && !player.isSpectator()) {
                player.abilities.mayfly = false;
                player.abilities.flying = false;
                player.onUpdateAbilities();
            }
        }
    }
}
