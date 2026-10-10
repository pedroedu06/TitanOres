package com.titanmodpack.titanores.event;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.compat.CuriosCompat;
import com.titanmodpack.titanores.init.ModItems;
import com.titanmodpack.titanores.item.RaidKingItem;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.AbstractRaiderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.raid.Raid;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

// Raid King crown: dropped where the last raider of a max Bad Omen raid dies, and gives endless Hero of the Village
// while worn on the head (armor slot or Curios).
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class RaidKingEvents {
    private static final boolean CURIOS_LOADED = ModList.get().isLoaded(CuriosCompat.MOD_ID);
    private static final int EFFECT_DURATION = 400;

    // Per world: raid id -> where its latest raider died. The crown drops there once the game declares the victory
    // (which only happens after the last raider of the last wave dies).
    private static final Map<ServerWorld, Map<Integer, Vector3d>> PENDING = new WeakHashMap<>();

    @SubscribeEvent
    public static void onRaiderDeath(LivingDeathEvent event) {
        if (!(event.getEntityLiving() instanceof AbstractRaiderEntity) || event.getEntityLiving().level.isClientSide) {
            return;
        }
        AbstractRaiderEntity raider = (AbstractRaiderEntity) event.getEntityLiving();
        // The raider already left its raid when this event fires (AbstractRaiderEntity#die calls removeFromRaid first),
        // so the raid is looked up by position, like vanilla does. Wave 0 = patrols, not raid members.
        if (raider.getWave() <= 0) {
            return;
        }
        ServerWorld world = (ServerWorld) raider.level;
        Raid raid = world.getRaidAt(raider.blockPosition());
        if (raid != null && raid.getBadOmenLevel() >= raid.getMaxBadOmenLevel()) {
            PENDING.computeIfAbsent(world, w -> new HashMap<>()).put(raid.getId(), raider.position());
        }
    }

    @SubscribeEvent
    public static void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !(event.world instanceof ServerWorld)) {
            return;
        }
        ServerWorld world = (ServerWorld) event.world;
        Map<Integer, Vector3d> pending = PENDING.get(world);
        if (pending == null || pending.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<Integer, Vector3d>> it = pending.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, Vector3d> entry = it.next();
            Raid raid = world.getRaids().get(entry.getKey());
            if (raid == null || raid.isOver()) {
                if (raid != null && raid.isVictory()) {
                    dropCrown(world, entry.getValue(), raid.getMaxBadOmenLevel());
                }
                it.remove();
            }
        }
    }

    private static void dropCrown(ServerWorld world, Vector3d pos, int heroLevel) {
        ItemStack crown = RaidKingItem.withHeroLevel(new ItemStack(ModItems.RAID_KING.get()), heroLevel);
        ItemEntity item = new ItemEntity(world, pos.x, pos.y + 0.5, pos.z, crown);
        item.setDefaultPickUpDelay();
        world.addFreshEntity(item);
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        PlayerEntity player = event.player;
        if (event.phase != TickEvent.Phase.END || player.level.isClientSide || player.tickCount % 20 != 0) {
            return;
        }
        ItemStack crown = player.getItemBySlot(EquipmentSlotType.HEAD);
        if (!(crown.getItem() instanceof RaidKingItem) && CURIOS_LOADED) {
            crown = CuriosCompat.findRaidKing(player);
        }
        if (crown.getItem() instanceof RaidKingItem) {
            int amplifier = RaidKingItem.getHeroLevel(crown) - 1;
            player.addEffect(new EffectInstance(Effects.HERO_OF_THE_VILLAGE, EFFECT_DURATION, amplifier, true, false, true));
        }
    }
}
