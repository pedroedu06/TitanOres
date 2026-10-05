package com.titanmodpack.titanores.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.block.TitanFactoryTileEntity;
import com.titanmodpack.titanores.container.TitanFactoryContainer;
import com.titanmodpack.titanores.init.ModItems;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class TitanFactoryScreen extends ContainerScreen<TitanFactoryContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(TitanOres.MOD_ID, "textures/gui/titan_factory.png");

    // Energy bar = inside of the black area on the right of the texture.
    private static final int ENERGY_X = 153;
    private static final int ENERGY_Y = 12;
    private static final int ENERGY_W = 15;
    private static final int ENERGY_H = 63;
    // Progress bar drawn under the output slot.
    private static final int PROGRESS_X = 65;
    private static final int PROGRESS_Y = 57;
    private static final int PROGRESS_W = 24;
    private static final int PROGRESS_H = 3;

    // Upgrade tab outside the right edge, next to the energy bar (overlaps the GUI border by 3 px so it looks attached).
    private static final int TAB_X = 173;
    private static final int TAB_Y = 4;
    private static final int TAB_W = 28;
    private static final int TAB_H = 4 + 16 + 4 + TitanFactoryTileEntity.UPGRADE_SLOTS * 18 + 3;
    private static final int ICON_X = TitanFactoryContainer.UPGRADE_X;
    private static final int ICON_Y = TAB_Y + 4;

    public TitanFactoryScreen(TitanFactoryContainer container, PlayerInventory playerInventory, ITextComponent title) {
        super(container, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
        this.inventoryLabelY = this.imageHeight - 94;
        // The title would overlap the top slots, so it is drawn centered above the output slot.
        this.titleLabelY = 4;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        renderTooltip(matrixStack, mouseX, mouseY);

        if (isInside(mouseX, mouseY, ENERGY_X, ENERGY_Y, ENERGY_W, ENERGY_H)) {
            renderTooltip(matrixStack, new TranslationTextComponent("tooltip.titanores.energy",
                    String.format("%,d", menu.getEnergy()), String.format("%,d", menu.getEnergyCapacity())), mouseX, mouseY);
        } else if (isInside(mouseX, mouseY, ICON_X, ICON_Y, 16, 16)) {
            int upgrades = menu.getInstalledUpgrades();
            renderTooltip(matrixStack, new TranslationTextComponent("tooltip.titanores.speed_upgrades",
                    upgrades, TitanFactoryTileEntity.MAX_UPGRADES, 1 << upgrades), mouseX, mouseY);
        } else if (isInside(mouseX, mouseY, PROGRESS_X, PROGRESS_Y - 1, PROGRESS_W, PROGRESS_H + 2)) {
            renderTooltip(matrixStack, new TranslationTextComponent("tooltip.titanores.progress", progressPercent()), mouseX, mouseY);
        }
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(TEXTURE);
        renderUpgradeTab(matrixStack);
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

    // Vanilla-style panel with the upgrade icon on top and the upgrade slot below it.
    private void renderUpgradeTab(MatrixStack matrixStack) {
        int x0 = leftPos + TAB_X;
        int y0 = topPos + TAB_Y;
        int x1 = x0 + TAB_W;
        int y1 = y0 + TAB_H;
        // Black outline (rounded corners), white highlight on top, dark shadow on the right and bottom.
        fill(matrixStack, x0, y0, x1 - 1, y1, 0xFF000000);
        fill(matrixStack, x0, y0 + 1, x1, y1 - 1, 0xFF000000);
        fill(matrixStack, x0, y0 + 1, x1 - 1, y1 - 1, 0xFF555555);
        fill(matrixStack, x0, y0 + 1, x1 - 2, y1 - 2, 0xFFFFFFFF);
        fill(matrixStack, x0, y0 + 2, x1 - 2, y1 - 2, 0xFFC6C6C6);
        for (int i = 0; i < TitanFactoryTileEntity.UPGRADE_SLOTS; i++) {
            int sx = leftPos + TitanFactoryContainer.UPGRADE_X - 1;
            int sy = topPos + TitanFactoryContainer.UPGRADE_Y + i * 18 - 1;
            fill(matrixStack, sx, sy, sx + 18, sy + 18, 0xFF373737);
            fill(matrixStack, sx + 1, sy + 1, sx + 18, sy + 18, 0xFFFFFFFF);
            fill(matrixStack, sx + 1, sy + 1, sx + 17, sy + 17, 0xFF8B8B8B);
        }
        itemRenderer.renderGuiItem(new ItemStack(ModItems.SPEED_UPGRADE.get()), leftPos + ICON_X, topPos + ICON_Y);
    }

    // Clicks on the upgrade tab are inside the GUI (otherwise the held item would be dropped).
    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int left, int top, int button) {
        boolean onTab = mouseX >= left + TAB_X && mouseX < left + TAB_X + TAB_W
                && mouseY >= top + TAB_Y && mouseY < top + TAB_Y + TAB_H;
        return !onTab && super.hasClickedOutside(mouseX, mouseY, left, top, button);
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
