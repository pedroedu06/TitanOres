package com.titanmodpack.titanores.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraftforge.common.util.FakePlayer;

import java.util.Collections;
import java.util.List;

// Ore that only drops when mined by a real player. Machines (quarries, digital miners,
// builders) break blocks through fake players or without any entity, so they get nothing.
public class ManualMiningOreBlock extends Block {
    public ManualMiningOreBlock(Properties properties) {
        super(properties);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        Entity breaker = builder.getOptionalParameter(LootParameters.THIS_ENTITY);
        if (!(breaker instanceof PlayerEntity) || breaker instanceof FakePlayer) {
            return Collections.emptyList();
        }
        return super.getDrops(state, builder);
    }
}
