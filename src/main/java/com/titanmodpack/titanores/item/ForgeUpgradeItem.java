package com.titanmodpack.titanores.item;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUseContext;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

// Forbidden & Arcanus 16.2.x has Hephaestus Forge tiers but no way to upgrade them in survival (the level is only read
// from NBT). These items come from upgrade rituals (in the modpack) and raise the forge one tier on right-click.
// Works through the block's NBT ("Level": "1".."4", "X"), so no compile dependency on Forbidden & Arcanus.
public class ForgeUpgradeItem extends Item {
    private static final ResourceLocation FORGE = new ResourceLocation("forbidden_arcanus", "hephaestus_forge");
    private static final String[] LEVELS = {"1", "2", "3", "4", "X"};

    // Tier this item upgrades the forge to (2-5; 5 = "X").
    private final int targetTier;

    public ForgeUpgradeItem(Properties properties, int targetTier) {
        super(properties);
        this.targetTier = targetTier;
    }

    @Override
    public ActionResultType useOn(ItemUseContext context) {
        World world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        BlockState state = world.getBlockState(pos);
        if (!FORGE.equals(state.getBlock().getRegistryName())) {
            return ActionResultType.PASS;
        }
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        TileEntity tile = world.getBlockEntity(pos);
        PlayerEntity player = context.getPlayer();
        if (tile == null) {
            return ActionResultType.FAIL;
        }
        CompoundNBT nbt = tile.save(new CompoundNBT());
        int currentTier = tierOf(nbt.getString("Level"));
        if (currentTier != targetTier - 1) {
            if (player != null) {
                player.displayClientMessage(new TranslationTextComponent("message.titanores.forge_upgrade.wrong_tier",
                        LEVELS[targetTier - 2], LEVELS[targetTier - 1]), true);
            }
            return ActionResultType.FAIL;
        }
        nbt.putString("Level", LEVELS[targetTier - 1]);
        tile.load(state, nbt);
        tile.setChanged();
        world.sendBlockUpdated(pos, state, state, 3);
        world.playSound(null, pos, SoundEvents.BEACON_POWER_SELECT, SoundCategory.BLOCKS, 1.0F, 1.0F);
        if (player != null) {
            player.displayClientMessage(new TranslationTextComponent("message.titanores.forge_upgrade.done", LEVELS[targetTier - 1]), true);
            if (!player.abilities.instabuild) {
                context.getItemInHand().shrink(1);
            }
        }
        return ActionResultType.CONSUME;
    }

    private static int tierOf(String level) {
        for (int i = 0; i < LEVELS.length; i++) {
            if (LEVELS[i].equals(level)) {
                return i + 1;
            }
        }
        return 1;
    }
}
