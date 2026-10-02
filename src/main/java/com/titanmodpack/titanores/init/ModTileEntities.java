package com.titanmodpack.titanores.init;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.block.SolariteLanternTileEntity;
import com.titanmodpack.titanores.block.TitanFactoryTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntities {
    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITIES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, TitanOres.MOD_ID);

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<TileEntityType<SolariteLanternTileEntity>> SOLARITE_LANTERN = TILE_ENTITIES.register("solarite_lantern",
            () -> TileEntityType.Builder.of(SolariteLanternTileEntity::new, ModBlocks.SOLARITE_LANTERN.get()).build(null));

    @SuppressWarnings("ConstantConditions")
    public static final RegistryObject<TileEntityType<TitanFactoryTileEntity>> TITAN_FACTORY = TILE_ENTITIES.register("titan_factory",
            () -> TileEntityType.Builder.of(TitanFactoryTileEntity::new, ModBlocks.TITAN_FACTORY.get()).build(null));
}
