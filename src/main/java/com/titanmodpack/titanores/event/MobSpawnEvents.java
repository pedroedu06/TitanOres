package com.titanmodpack.titanores.event;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.block.LanternRegistry;
import com.titanmodpack.titanores.init.ModItems;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.EndPodiumFeature;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID)
public class MobSpawnEvents {

    // Solarite Lantern: no natural hostile spawns in its 3x3 chunk area. Spawners and spawn eggs still work.
    @SubscribeEvent
    public static void onCheckSpawn(LivingSpawnEvent.CheckSpawn event) {
        SpawnReason reason = event.getSpawnReason();
        if (reason != SpawnReason.NATURAL && reason != SpawnReason.CHUNK_GENERATION) {
            return;
        }
        if (event.getEntity().getType().getCategory() != EntityClassification.MONSTER) {
            return;
        }
        if (!(event.getWorld() instanceof IServerWorld)) {
            return;
        }
        World world = ((IServerWorld) event.getWorld()).getLevel();
        BlockPos pos = new BlockPos(event.getX(), event.getY(), event.getZ());
        if (LanternRegistry.isProtected(world.dimension(), pos)) {
            event.setResult(Event.Result.DENY);
        }
    }

    // The Ender Dragon drops a Titanium Heart. In the End it is placed on top of the exit portal pillar,
    // so it can't fall into the void; elsewhere it drops where the dragon died.
    @SubscribeEvent
    public static void onLivingDrops(LivingDropsEvent event) {
        if (!(event.getEntityLiving() instanceof EnderDragonEntity)) {
            return;
        }
        World world = event.getEntityLiving().level;
        double x = event.getEntityLiving().getX();
        double y = event.getEntityLiving().getY();
        double z = event.getEntityLiving().getZ();
        if (world.dimension() == World.END) {
            BlockPos top = world.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING, EndPodiumFeature.END_PODIUM_LOCATION);
            x = top.getX() + 0.5D;
            y = top.getY() + 0.5D;
            z = top.getZ() + 0.5D;
        }
        ItemEntity heart = new ItemEntity(world, x, y, z, new ItemStack(ModItems.TITANIUM_HEART.get()));
        heart.setExtendedLifetime();
        event.getDrops().add(heart);
    }
}
