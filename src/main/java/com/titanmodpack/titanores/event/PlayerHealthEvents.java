package com.titanmodpack.titanores.event;

import com.titanmodpack.titanores.TitanOres;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

// Permanent max health from Titanium Hearts. The count is stored in the player's persisted NBT,
// which Forge keeps across deaths, and the attribute modifier is re-applied whenever needed.
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class PlayerHealthEvents {
    private static final UUID HEART_MODIFIER_ID = UUID.fromString("5e0b6a4c-2f3d-4c8e-9b1a-7d6f0c2e8a41");
    private static final String HEARTS_KEY = TitanOres.MOD_ID + ":titanium_hearts";
    private static final double HEALTH_PER_HEART_ITEM = 4.0D;

    public static void addHeartItem(PlayerEntity player) {
        CompoundNBT data = persistedData(player);
        data.putInt(HEARTS_KEY, data.getInt(HEARTS_KEY) + 1);
        applyModifier(player);
        player.heal((float) HEALTH_PER_HEART_ITEM);
    }

    public static int getHeartItems(PlayerEntity player) {
        return persistedData(player).getInt(HEARTS_KEY);
    }

    // Used by /titanores hearts. Clamps current health if max health went down.
    public static void setHeartItems(PlayerEntity player, int count) {
        persistedData(player).putInt(HEARTS_KEY, Math.max(0, count));
        applyModifier(player);
        if (player.getHealth() > player.getMaxHealth()) {
            player.setHealth(player.getMaxHealth());
        }
    }

    private static CompoundNBT persistedData(PlayerEntity player) {
        CompoundNBT root = player.getPersistentData();
        if (!root.contains(PlayerEntity.PERSISTED_NBT_TAG)) {
            root.put(PlayerEntity.PERSISTED_NBT_TAG, new CompoundNBT());
        }
        return root.getCompound(PlayerEntity.PERSISTED_NBT_TAG);
    }

    private static void applyModifier(PlayerEntity player) {
        ModifiableAttributeInstance maxHealth = player.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealth == null) {
            return;
        }
        double amount = persistedData(player).getInt(HEARTS_KEY) * HEALTH_PER_HEART_ITEM;
        AttributeModifier existing = maxHealth.getModifier(HEART_MODIFIER_ID);
        if (existing != null && existing.getAmount() == amount) {
            return;
        }
        maxHealth.removeModifier(HEART_MODIFIER_ID);
        if (amount > 0) {
            maxHealth.addPermanentModifier(new AttributeModifier(HEART_MODIFIER_ID, "Titanium hearts", amount, AttributeModifier.Operation.ADDITION));
        }
    }

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent event) {
        applyModifier(event.getPlayer());
    }

    @SubscribeEvent
    public static void onClone(PlayerEvent.Clone event) {
        CompoundNBT oldData = persistedData(event.getOriginal());
        persistedData(event.getPlayer()).putInt(HEARTS_KEY, oldData.getInt(HEARTS_KEY));
    }

    @SubscribeEvent
    public static void onRespawn(PlayerEvent.PlayerRespawnEvent event) {
        PlayerEntity player = event.getPlayer();
        applyModifier(player);
        player.setHealth(player.getMaxHealth());
    }
}
