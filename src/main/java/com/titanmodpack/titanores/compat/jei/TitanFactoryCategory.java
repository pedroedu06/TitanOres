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
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
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

    // Animation length: the real recipe time, kept between 2 and 6 seconds so long recipes still look alive.
    private static final long MIN_CYCLE_MS = 2000;
    private static final long MAX_CYCLE_MS = 6000;
    // Last part of each cycle shows the finished craft (full bar, glowing output).
    private static final double DONE_PART = 0.2;

    // AbstractGui.fillGradient is a protected instance method, so a tiny subclass exposes it.
    private static final Gradient GRADIENT = new Gradient();

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
        double progress = cycleProgress(recipe);

        // Energy: a static bar with only what the recipe needs (relative to the buffer).
        int filled = Math.max(2, (int) Math.round((double) ENERGY_H * recipe.getEnergy() / TitanFactoryTileEntity.ENERGY_CAPACITY));
        {
            int bottom = ENERGY_Y + ENERGY_H;
            // Same gradient as the machine GUI (TitanFactoryScreen).
            GRADIENT.draw(matrixStack, ENERGY_X, bottom - filled, ENERGY_X + ENERGY_W, bottom, 0xFFFF8A1A, 0xFFB3151B);
        }

        // Progress bar under the output (same colors as the machine GUI).
        AbstractGui.fill(matrixStack, PROGRESS_X, PROGRESS_Y, PROGRESS_X + PROGRESS_W, PROGRESS_Y + PROGRESS_H, 0xFF373737);
        int width = (int) (PROGRESS_W * progress);
        if (width > 0) {
            AbstractGui.fill(matrixStack, PROGRESS_X, PROGRESS_Y, PROGRESS_X + width, PROGRESS_Y + PROGRESS_H, 0xFFFFC21A);
        }

        // Finished: the output slot glows behind the result.
        if (progress >= 1.0) {
            int x = OUTPUT_X - CROP_X;
            int y = OUTPUT_Y - CROP_Y;
            AbstractGui.fill(matrixStack, x - 2, y - 2, x + 18, y + 18, 0x66FFE27A);
        }

        FontRenderer font = Minecraft.getInstance().font;
        String info = new TranslationTextComponent("jei.titanores.titan_factory.info",
                String.format("%,d", recipe.getEnergy()), formatSeconds(recipe.getTime())).getString();
        font.draw(matrixStack, info, (WIDTH - font.width(info)) / 2.0F, HEIGHT + 2, 0xFF404040);
    }

    @Override
    public List<ITextComponent> getTooltipStrings(TitanFactoryRecipe recipe, double mouseX, double mouseY) {
        if (inside(mouseX, mouseY, ENERGY_X, ENERGY_Y, ENERGY_W, ENERGY_H)) {
            return Collections.singletonList(new TranslationTextComponent("jei.titanores.titan_factory.energy",
                    String.format("%,d", recipe.getEnergy())));
        }
        if (inside(mouseX, mouseY, PROGRESS_X, PROGRESS_Y - 2, PROGRESS_W, PROGRESS_H + 4)) {
            return Collections.singletonList(new TranslationTextComponent("jei.titanores.titan_factory.time",
                    formatSeconds(recipe.getTime()), String.format("%,d", recipe.getEnergyPerTick())));
        }
        if (inside(mouseX, mouseY, ARROWS_X, ARROWS_Y, ARROWS_W, ARROWS_H) && recipe.isMirrored()) {
            List<ITextComponent> lines = new ArrayList<>();
            lines.add(new TranslationTextComponent("jei.titanores.titan_factory.mirrored"));
            return lines;
        }
        return Collections.emptyList();
    }

    // 0..1 while working, then 1.0 during the "done" part of the cycle.
    private static double cycleProgress(TitanFactoryRecipe recipe) {
        long cycle = Math.max(MIN_CYCLE_MS, Math.min(MAX_CYCLE_MS, recipe.getTime() * 50L));
        double t = (double) (System.currentTimeMillis() % cycle) / cycle;
        return Math.min(1.0, t / (1.0 - DONE_PART));
    }

    private static String formatSeconds(int ticks) {
        double seconds = ticks / 20.0;
        return seconds == Math.floor(seconds) ? String.valueOf((int) seconds) : String.format("%.2f", seconds).replaceAll("0+$", "");
    }

    private static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static class Gradient extends AbstractGui {
        void draw(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int topColor, int bottomColor) {
            fillGradient(matrixStack, x1, y1, x2, y2, topColor, bottomColor);
        }
    }
}
