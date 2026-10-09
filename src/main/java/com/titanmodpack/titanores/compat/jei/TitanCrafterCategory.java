package com.titanmodpack.titanores.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.block.TitanCrafterTileEntity;
import com.titanmodpack.titanores.client.TitanCrafterScreen;
import com.titanmodpack.titanores.init.ModItems;
import com.titanmodpack.titanores.recipe.TitanCrafterRecipe;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import java.util.Collections;
import java.util.List;

// JEI page for the Titan Crafter. JEI 7 recipe windows are fixed at 198 px wide, so the 240 px machine GUI does not
// fit: the page uses a compact layout instead (9x6 grid, vertical energy bar beside it, arrow -> output below).
// The energy bar statically shows only the recipe's energy (relative to the 200M buffer); progress loops.
public class TitanCrafterCategory implements IRecipeCategory<TitanCrafterRecipe> {
    public static final ResourceLocation UID = new ResourceLocation(TitanOres.MOD_ID, "titan_crafting");
    private static final ResourceLocation TEXTURE = new ResourceLocation(TitanOres.MOD_ID, "textures/gui/titan_crafter_gui.png");

    private static final int WIDTH = 180;
    private static final int HEIGHT = 150;
    private static final int GRID_H = TitanCrafterRecipe.HEIGHT * 18;

    // Energy bar frame beside the grid (1 px black border, like the machine GUI).
    private static final int FRAME_X = 166;
    private static final int FRAME_W = 14;
    private static final int ENERGY_X = FRAME_X + 1;
    private static final int ENERGY_Y = 1;
    private static final int ENERGY_W = FRAME_W - 2;
    private static final int ENERGY_H = GRID_H - 2;

    // Bottom row: arrow (from the GUI texture) -> output slot, progress bar under the output.
    private static final int ARROW_X = 50;
    private static final int ARROW_Y = 117;
    private static final int OUTPUT_X = 80;
    private static final int OUTPUT_Y = 115;
    private static final int PROGRESS_X = OUTPUT_X;
    private static final int PROGRESS_Y = OUTPUT_Y + 19;
    private static final int PROGRESS_W = 16;
    private static final int PROGRESS_H = 3;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable arrow;

    public TitanCrafterCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModItems.TITAN_CRAFTER.get()));
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.drawableBuilder(TEXTURE, TitanCrafterScreen.ARROW_X, TitanCrafterScreen.ARROW_Y,
                TitanCrafterScreen.ARROW_W, TitanCrafterScreen.ARROW_H).setTextureSize(256, 256).build();
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Override
    public Class<? extends TitanCrafterRecipe> getRecipeClass() {
        return TitanCrafterRecipe.class;
    }

    @Override
    @SuppressWarnings("deprecation")
    public String getTitle() {
        return getTitleAsTextComponent().getString();
    }

    @Override
    public ITextComponent getTitleAsTextComponent() {
        return new TranslationTextComponent("jei.titanores.titan_crafter");
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
    public void setIngredients(TitanCrafterRecipe recipe, IIngredients ingredients) {
        // 54 entries in slot order (row by row), used by the "+" transfer button.
        ingredients.setInputIngredients(recipe.getIngredients());
        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, TitanCrafterRecipe recipe, IIngredients ingredients) {
        IGuiItemStackGroup stacks = layout.getItemStacks();
        for (int row = 0; row < TitanCrafterRecipe.HEIGHT; row++) {
            for (int col = 0; col < TitanCrafterRecipe.WIDTH; col++) {
                stacks.init(row * TitanCrafterRecipe.WIDTH + col, true, col * 18, row * 18);
            }
        }
        stacks.init(TitanCrafterTileEntity.OUTPUT_SLOT, false, OUTPUT_X - 1, OUTPUT_Y - 1);
        stacks.set(ingredients);
    }

    @Override
    public void draw(TitanCrafterRecipe recipe, MatrixStack matrixStack, double mouseX, double mouseY) {
        for (int row = 0; row < TitanCrafterRecipe.HEIGHT; row++) {
            for (int col = 0; col < TitanCrafterRecipe.WIDTH; col++) {
                slot.draw(matrixStack, col * 18, row * 18);
            }
        }
        slot.draw(matrixStack, OUTPUT_X - 1, OUTPUT_Y - 1);
        arrow.draw(matrixStack, ARROW_X, ARROW_Y);

        AbstractGui.fill(matrixStack, FRAME_X, 0, FRAME_X + FRAME_W, GRID_H, 0xFF000000);
        double progress = JeiDrawUtil.cycleProgress(recipe.getTime());
        JeiDrawUtil.energyBar(matrixStack, ENERGY_X, ENERGY_Y, ENERGY_W, ENERGY_H, recipe.getEnergy(), TitanCrafterTileEntity.ENERGY_CAPACITY);
        JeiDrawUtil.progressBar(matrixStack, PROGRESS_X, PROGRESS_Y, PROGRESS_W, PROGRESS_H, progress);
        JeiDrawUtil.outputGlow(matrixStack, OUTPUT_X, OUTPUT_Y, progress);
        JeiDrawUtil.centeredText(matrixStack, new TranslationTextComponent("jei.titanores.titan_factory.info",
                String.format("%,d", recipe.getEnergy()), JeiDrawUtil.formatSeconds(recipe.getTime())).getString(), WIDTH, HEIGHT - 9);
    }

    @Override
    public List<ITextComponent> getTooltipStrings(TitanCrafterRecipe recipe, double mouseX, double mouseY) {
        if (JeiDrawUtil.inside(mouseX, mouseY, ENERGY_X, ENERGY_Y, ENERGY_W, ENERGY_H)) {
            return Collections.singletonList(new TranslationTextComponent("jei.titanores.titan_factory.energy",
                    String.format("%,d", recipe.getEnergy())));
        }
        if (JeiDrawUtil.inside(mouseX, mouseY, PROGRESS_X, PROGRESS_Y - 2, PROGRESS_W, PROGRESS_H + 4)) {
            return Collections.singletonList(new TranslationTextComponent("jei.titanores.titan_factory.time",
                    JeiDrawUtil.formatSeconds(recipe.getTime()), String.format("%,d", recipe.getEnergyPerTick())));
        }
        return Collections.emptyList();
    }
}
