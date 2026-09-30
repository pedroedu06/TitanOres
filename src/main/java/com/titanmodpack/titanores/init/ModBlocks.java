package com.titanmodpack.titanores.init;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.block.ManualMiningOreBlock;
import com.titanmodpack.titanores.block.SolariteLanternBlock;
import com.titanmodpack.titanores.block.SolariteLightBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, TitanOres.MOD_ID);

    // Harvest levels: 0 wood, 1 stone, 2 iron, 3 diamond, 4 netherite.
    // The ore requires level 4: netherite or any modded pickaxe with an equal or higher level.
    public static final RegistryObject<Block> SOLARITE_ORE = BLOCKS.register("solarite_ore",
            () -> new ManualMiningOreBlock(AbstractBlock.Properties.of(Material.STONE)
                    // Blast resistance 1200 (same as ancient debris/obsidian): TNT and creepers can't destroy it.
                    .strength(3.0F, 1200.0F)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(4)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> SOLARITE_BLOCK = BLOCKS.register("solarite_block",
            () -> new Block(AbstractBlock.Properties.of(Material.METAL)
                    // Blast resistance 1200, same as the ore: explosions can't destroy it.
                    .strength(5.0F, 1200.0F)
                    .sound(SoundType.METAL)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(4)
                    .requiresCorrectToolForDrops()));

    // Emberite requires level 5 (solarite pickaxe), one above netherite.
    public static final RegistryObject<Block> EMBERITE_ORE = BLOCKS.register("emberite_ore",
            () -> new ManualMiningOreBlock(AbstractBlock.Properties.of(Material.STONE)
                    .strength(3.0F, 1200.0F)
                    .sound(SoundType.NETHER_ORE)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(5)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> EMBERITE_BLOCK = BLOCKS.register("emberite_block",
            () -> new Block(AbstractBlock.Properties.of(Material.METAL)
                    .strength(5.0F, 1200.0F)
                    .sound(SoundType.METAL)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(5)
                    .requiresCorrectToolForDrops()));

    // Titanium (endgame) requires level 6 (emberite pickaxe).
    public static final RegistryObject<Block> TITANIUM_ORE = BLOCKS.register("titanium_ore",
            () -> new ManualMiningOreBlock(AbstractBlock.Properties.of(Material.STONE)
                    .strength(3.0F, 1200.0F)
                    .sound(SoundType.STONE)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(6)
                    .requiresCorrectToolForDrops()));

    public static final RegistryObject<Block> TITANIUM_BLOCK = BLOCKS.register("titanium_block",
            () -> new Block(AbstractBlock.Properties.of(Material.METAL)
                    .strength(5.0F, 1200.0F)
                    .sound(SoundType.METAL)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(6)
                    .requiresCorrectToolForDrops()));

    // Light level 15; blocks natural hostile spawns in its 3x3 chunk area. Needs an iron pickaxe or better.
    public static final RegistryObject<Block> SOLARITE_LANTERN = BLOCKS.register("solarite_lantern",
            () -> new SolariteLanternBlock(AbstractBlock.Properties.of(Material.GLASS)
                    .strength(1.5F)
                    .sound(SoundType.LANTERN)
                    .lightLevel(state -> 15)
                    .harvestTool(ToolType.PICKAXE)
                    .harvestLevel(2)
                    .requiresCorrectToolForDrops()));

    // Invisible light placed by the Solarite Lantern (no item).
    public static final RegistryObject<Block> SOLARITE_LIGHT = BLOCKS.register("solarite_light", SolariteLightBlock::new);
}
