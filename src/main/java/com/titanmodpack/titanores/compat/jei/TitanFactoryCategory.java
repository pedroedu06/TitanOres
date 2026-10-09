package com.titanmodpack.titanores.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.block.TitanFactoryTileEntity;
import com.titanmodpack.titanores.init.ModItems;
import com.titanmodpack.titanores.recipe.TitanFactoryRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// JEI page drawn with the machine's own GUI. It loops a "machine working" animation: the progress bar fills, while
// the energy bar statically shows only the recipe's energy (relative to the 100M buffer); then the output glows.
public class TitanFactoryCategory implements IRecipeCategory<TitanFactoryRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(TitanOres.MOD_ID, "titan_factory");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TitanOres.MOD_ID, "textures/gui/titan_factory.png");

    // The machine part of the GUI texture (no player inventory); everything below is relative to this crop.
    private static final int CROP_X = 4;
    private static final int CROP_Y = 4;
    private static final int WIDTH = 168;
    private static final int HEIGHT = 74;
    private static final int TEXT_HEIGHT = 12;

    private static final int[][] INPUTS = {{11, 11}, {11, 34}, {11, 56}, {128, 11}, {128, 34}, {128, 56}};
    private static final int OUTPUT_X = 69;
    private static final int OUTPUT_Y = 34;
    private static final int ENERGY_X = 153 - CROP_X;
    private static final int ENERGY_Y = 12 - CROP_Y;
    private static final int ENERGY_W = 15;
    private static final int ENERGY_H = 63;
    private static final int PROGRESS_X = 65 - CROP_X;
    private static final int PROGRESS_Y = 57 - CROP_Y;
    private static final int PROGRESS_W = 24;
    private static final int PROGRESS_H = 3;
    private static final int ARROWS_X = 34 - CROP_X;
    private static final int ARROWS_Y = 35 - CROP_Y;
    private static final int ARROWS_W = 85;
    private static final int ARROWS_H = 14;

    private final IDrawable background;
    private final IDrawable icon;

    public TitanFactoryCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.drawableBuilder(TEXTURE, CROP_X, CROP_Y, WIDTH, HEIGHT)
                .addPadding(0, TEXT_HEIGHT, 0, 0)
                .build();
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.TITAN_FACTORY.get()));
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends TitanFactoryRecipe> getRecipeClass() {
        return TitanFactoryRecipe.class;
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getTitle() {
        return getTitleAsTextComponent().getString();
    }

    @Override
    public ITextComponent getTitleAsTextComponent() {
        return new TranslationTextComponent("jei.titanores.titan_factory");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(TitanFactoryRecipe recipe, IIngredients ingredients) {
        // Order matches container slots 0-5 (left column, then right column), used by the "+" transfer button.
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, TitanFactoryRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup stacks = layout.getItemStacks();
        // JEI slots are placed 1 px before the item, like vanilla slot borders.
        for (int i = 0; i < INPUTS.length; i++) {
            stacks.init(i, true, INPUTS[i][0] - CROP_X - 1, INPUTS[i][1] - CROP_Y - 1);
        }
        stacks.init(TitanFactoryTileEntity.OUTPUT_SLOT, false, OUTPUT_X - CROP_X - 1, OUTPUT_Y - CROP_Y - 1);
        stacks.set(ingredients);
    }

    @Override
    public void draw(TitanFactoryRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        double progress = JeiDrawUtil.cycleProgress(recipe.getTime());
        JeiDrawUtil.energyBar(matrixStack, ENERGY_X, ENERGY_Y, ENERGY_W, ENERGY_H, recipe.getEnergy(), TitanFactoryTileEntity.ENERGY_CAPACITY);
        JeiDrawUtil.progressBar(matrixStack, PROGRESS_X, PROGRESS_Y, PROGRESS_W, PROGRESS_H, progress);
        JeiDrawUtil.outputGlow(matrixStack, OUTPUT_X - CROP_X, OUTPUT_Y - CROP_Y, progress);
        JeiDrawUtil.centeredText(matrixStack, new TranslationTextComponent("jei.titanores.titan_factory.info",
                String.format("%,d", recipe.getEnergy()), JeiDrawUtil.formatSeconds(recipe.getTime())).getString(), WIDTH, HEIGHT + 2);
    }

    @Override
    public List<ITextComponent> getTooltipStrings(TitanFactoryRecipe recipe, double mouseX, double mouseY) {
        if (JeiDrawUtil.inside(mouseX, mouseY, ENERGY_X, ENERGY_Y, ENERGY_W, ENERGY_H)) {
            return Collections.singletonList(new TranslationTextComponent("jei.titanores.titan_factory.energy",
                    String.format("%,d", recipe.getEnergy())));
        }
        if (JeiDrawUtil.inside(mouseX, mouseY, PROGRESS_X, PROGRESS_Y - 2, PROGRESS_W, PROGRESS_H + 4)) {
            return Collections.singletonList(new TranslationTextComponent("jei.titanores.titan_factory.time",
                    JeiDrawUtil.formatSeconds(recipe.getTime()), String.format("%,d", recipe.getEnergyPerTick())));
        }
        if (JeiDrawUtil.inside(mouseX, mouseY, ARROWS_X, ARROWS_Y, ARROWS_W, ARROWS_H) && recipe.isMirrored()) {
            List<ITextComponent> lines = new ArrayList<>();
            lines.add(new TranslationTextComponent("jei.titanores.titan_factory.mirrored"));
            return lines;
        }
        return Collections.emptyList();
    }
}
