package com.titanmodpack.titanores.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.container.TitanCrafterContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TitanCrafterScreen extends ContainerScreen<TitanCrafterContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(TitanOres.MOD_ID, "textures/gui/titan_crafter_gui.png");

    // Energy bar = inside of the black area on the right of the texture, leaving a 1 px black border (like the factory).
    public static final int ENERGY_X = 219;
    public static final int ENERGY_Y = 16;
    public static final int ENERGY_W = 15;
    public static final int ENERGY_H = 108;
    // Progress bar drawn under the output slot.
    public static final int PROGRESS_X = 197;
    public static final int PROGRESS_Y = 73;
    public static final int PROGRESS_W = 16;
    public static final int PROGRESS_H = 3;
    // The arrow before the output slot (JEI click area).
    public static final int ARROW_X = 171;
    public static final int ARROW_Y = 56;
    public static final int ARROW_W = 21;
    public static final int ARROW_H = 13;

    public TitanCrafterScreen(TitanCrafterContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 240;
        this.imageHeight = 222;
        this.titleLabelY = 6;
        this.inventoryLabelY = TitanCrafterContainer.PLAYER_Y - 11;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);

        if (isInside(mouseX, mouseY, ENERGY_X, ENERGY_Y, ENERGY_W, ENERGY_H)) {
            renderTooltip(matrixStack, new TranslationTextComponent("tooltip.titanores.energy",
                    String.format("%,d", menu.getEnergy()), String.format("%,d", menu.getEnergyCapacity())), mouseX, mouseY);
        } else if (isInside(mouseX, mouseY, PROGRESS_X, PROGRESS_Y - 1, PROGRESS_W, PROGRESS_H + 2)) {
            renderTooltip(matrixStack, new TranslationTextComponent("tooltip.titanores.progress", progressPercent()), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(TEXTURE);
        blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        int capacity = menu.getEnergyCapacity();
        int filled = capacity <= 0 ? 0 : (int) ((long) ENERGY_H * menu.getEnergy() / capacity);
        if (filled > 0) {
            int bottom = topPos + ENERGY_Y + ENERGY_H;
            fillGradient(matrixStack, leftPos + ENERGY_X, bottom - filled, leftPos + ENERGY_X + ENERGY_W, bottom, 0xFFFF8A1A, 0xFFB3151B);
        }

        int px = leftPos + PROGRESS_X;
        int py = topPos + PROGRESS_Y;
        fill(matrixStack, px, py, px + PROGRESS_W, py + PROGRESS_H, 0xFF373737);
        int maxProgress = menu.getMaxProgress();
        if (maxProgress > 0 && menu.getProgress() > 0) {
            int width = PROGRESS_W * menu.getProgress() / maxProgress;
            fill(matrixStack, px, py, px + width, py + PROGRESS_H, 0xFFFFC21A);
        }
    }

    private int progressPercent() {
        int maxProgress = menu.getMaxProgress();
        return maxProgress <= 0 ? 0 : menu.getProgress() * 100 / maxProgress;
    }

    private boolean isInside(int mouseX, int mouseY, int x, int y, int width, int height) {
        int left = leftPos + x;
        int top = topPos + y;
        return mouseX >= left && mouseX < left + width && mouseY >= top && mouseY < top + height;
    }
}
