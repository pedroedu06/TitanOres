package com.titanmodpack.titanores.block;

import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// Loaded Solarite Lantern positions per dimension (server side only).
public class LanternRegistry {
    private static final Map<RegistryKey<World>, Set<BlockPos>> LANTERNS = new ConcurrentHashMap<>();

    public static void add(World world, BlockPos pos) {
        LANTERNS.computeIfAbsent(world.dimension(), key -> ConcurrentHashMap.newKeySet()).add(pos.immutable());
    }

    public static void remove(World world, BlockPos pos) {
        Set<BlockPos> positions = LANTERNS.get(world.dimension());
        if (positions != null) {
            positions.remove(pos);
        }
    }

    // True if a lantern is in the same chunk as pos or in one of the 8 chunks around it.
    public static boolean isProtected(RegistryKey<World> dimension, BlockPos pos) {
        Set<BlockPos> positions = LANTERNS.get(dimension);
        if (positions == null || positions.isEmpty()) {
            return false;
        }
        int chunkX = pos.getX() >> 4;
        int chunkZ = pos.getZ() >> 4;
        for (BlockPos lantern : positions) {
            if (Math.abs((lantern.getX() >> 4) - chunkX) <= 1 && Math.abs((lantern.getZ() >> 4) - chunkZ) <= 1) {
                return true;
            }
        }
        return false;
    }
}
