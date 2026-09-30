package com.titanmodpack.titanores.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.titanmodpack.titanores.TitanOres;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

// Keeps health (and absorption) on a single row of 10 hearts. Every 20 points is a new layer drawn
// over the previous one in another color, with an "xN" counter next to the row.
// Only takes over when the player has more than 20 max health or absorption; otherwise vanilla draws it.
// Fallback only: disabled when Mantle is installed.
@Mod.EventBusSubscriber(modid = TitanOres.MOD_ID, value = Dist.CLIENT)
public class CompactHealthBar {
    private static final ResourceLocation HEARTS = new ResourceLocation(TitanOres.MOD_ID, "textures/gui/hearts.png");
    private static final int[] LAYER_COLORS = {0xFF2626, 0xFF8C1A, 0xFFE21A, 0x3CD23C, 0x26D2E6, 0x3C6EFF, 0xA64CFF, 0xFF66C8};
    private static final int ABSORPTION_COLOR = 0xFFD21E;
    private static final int POISON_COLOR = 0x8CB43C;
    private static final int WITHER_COLOR = 0x3C3C3C;
    private static final Random RANDOM = new Random();
    // Mantle (Tinkers' library) has its own colored heart bar; if it is installed, let it draw.
    private static final boolean MANTLE_LOADED = ModList.get().isLoaded("mantle");

    @SubscribeEvent
    public static void onRenderHealth(RenderGameOverlayEvent.Pre event) {
        if (MANTLE_LOADED || event.getType() != RenderGameOverlayEvent.ElementType.HEALTH) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (!(mc.getCameraEntity() instanceof PlayerEntity)) {
            return;
        }
        PlayerEntity player = (PlayerEntity) mc.getCameraEntity();
        int absorption = MathHelper.ceil(player.getAbsorptionAmount());
        if (player.getMaxHealth() <= 20.0F && absorption <= 20) {
            return;
        }
        event.setCanceled(true);

        MatrixStack matrixStack = event.getMatrixStack();
        int left = event.getWindow().getGuiScaledWidth() / 2 - 91;
        int height = event.getWindow().getGuiScaledHeight();
        int health = MathHelper.ceil(player.getHealth());
        boolean shake = health <= 4;
        int effectColor = player.hasEffect(Effects.POISON) ? POISON_COLOR : player.hasEffect(Effects.WITHER) ? WITHER_COLOR : -1;
        RANDOM.setSeed(mc.gui.getGuiTicks() * 312871L);

        RenderSystem.enableBlend();
        drawRow(mc, matrixStack, left, height - ForgeIngameGui.left_height, health, true, effectColor, false, shake);
        ForgeIngameGui.left_height += 10;
        if (absorption > 0) {
            drawRow(mc, matrixStack, left, height - ForgeIngameGui.left_height, absorption, false, -1, true, false);
            ForgeIngameGui.left_height += 10;
        }
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        mc.getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
    }

    private static void drawRow(Minecraft mc, MatrixStack matrixStack, int left, int top, int value, boolean containers,
                                int effectColor, boolean absorption, boolean shake) {
        int layer = value <= 0 ? 0 : (value - 1) / 20;
        int rest = value - layer * 20;
        mc.getTextureManager().bind(HEARTS);
        for (int i = 0; i < 10; i++) {
            int x = left + i * 8;
            int y = top + (shake ? RANDOM.nextInt(2) : 0);
            if (containers) {
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
                AbstractGui.blit(matrixStack, x, y, 0, 0, 9, 9, 32, 16);
            }
            if (layer > 0) {
                setColor(effectColor >= 0 ? effectColor : colorOf(layer - 1, absorption));
                AbstractGui.blit(matrixStack, x, y, 9, 0, 9, 9, 32, 16);
            }
            int points = rest - i * 2;
            if (points > 0) {
                setColor(effectColor >= 0 ? effectColor : colorOf(layer, absorption));
                AbstractGui.blit(matrixStack, x, y, points >= 2 ? 9 : 18, 0, 9, 9, 32, 16);
            }
        }
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        if (layer > 0) {
            String text = "x" + (layer + 1);
            mc.font.drawShadow(matrixStack, text, left - mc.font.width(text) - 2, top + 1, 0xFFFFFF);
        }
    }

    private static int colorOf(int layer, boolean absorption) {
        if (absorption) {
            return layer == 0 ? ABSORPTION_COLOR : LAYER_COLORS[layer % LAYER_COLORS.length];
        }
        return LAYER_COLORS[layer % LAYER_COLORS.length];
    }

    private static void setColor(int rgb) {
        RenderSystem.color4f(((rgb >> 16) & 0xFF) / 255.0F, ((rgb >> 8) & 0xFF) / 255.0F, (rgb & 0xFF) / 255.0F, 1.0F);
    }
}
