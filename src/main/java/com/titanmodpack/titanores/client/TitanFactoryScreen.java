package com.titanmodpack.titanores.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.titanmodpack.titanores.TitanOres;
import com.titanmodpack.titanores.container.TitanFactoryContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class TitanFactoryScreen extends ContainerScreen<TitanFactoryContainer> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(TitanOres.MOD_ID, "textures/gui/titan_factory.png");

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
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(TEXTURE);
        blit(matrixStack, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
