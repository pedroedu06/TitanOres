package com.titanmodpack.titanores.compat.jei;

import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.block.TitanFactoryTileEntity;
import com.titanmodpack.titanores.client.TitanFactoryScreen;
import com.titanmodpack.titanores.container.TitanFactoryContainer;
import com.titanmodpack.titanores.init.ModItems;
import com.titanmodpack.titanores.init.ModRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Rectangle2d;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

// JEI integration. Only loaded by JEI itself (@JeiPlugin), so the mod still works without JEI installed.
@JeiPlugin
public class TitanOresJeiPlugin implements IModPlugin {
    private static final ResourceLocation UID = new ResourceLocation(TitanOres.MOD_ID, "jei_plugin");

    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new TitanFactoryCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(Objects.requireNonNull(Minecraft.getInstance().level).getRecipeManager()
                .getAllRecipesFor(ModRecipes.TITAN_FACTORY_TYPE), TitanFactoryCategory.UID);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(ModItems.TITAN_FACTORY.get()), TitanFactoryCategory.UID);
    }

    @Override
    public void registerGuiHandlers(IGuiHandlerRegistration registration) {
        // Clicking either arrow of the machine GUI opens its recipes.
        registration.addRecipeClickArea(TitanFactoryScreen.class, 34, 35, 22, 14, TitanFactoryCategory.UID);
        registration.addRecipeClickArea(TitanFactoryScreen.class, 97, 35, 22, 14, TitanFactoryCategory.UID);
        registration.addGuiContainerHandler(TitanFactoryScreen.class, new IGuiContainerHandler<TitanFactoryScreen>() {
            @Override
            public List<Rectangle2d> getGuiExtraAreas(TitanFactoryScreen screen) {
                return Collections.singletonList(screen.getUpgradeTabArea());
            }
        });
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        // "+" button: recipe inputs go to machine slots 0-5; the player inventory follows the output and upgrade slots.
        int playerStart = TitanFactoryTileEntity.SLOT_COUNT + TitanFactoryTileEntity.UPGRADE_SLOTS;
        registration.addRecipeTransferHandler(TitanFactoryContainer.class, TitanFactoryCategory.UID,
                0, TitanFactoryTileEntity.INPUT_SLOTS, playerStart, 36);
    }
}
