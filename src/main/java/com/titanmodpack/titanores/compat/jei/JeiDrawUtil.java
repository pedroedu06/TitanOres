package com.titanmodpack.titanores.compat.jei;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;

// Drawing helpers shared by the machine categories, so JEI pages look like the machine GUIs.
final class JeiDrawUtil {
    // Animation length: the real recipe time, kept between 2 and 6 seconds so long recipes still look alive.
    private static final long MIN_CYCLE_MS = 2000;
    private static final long MAX_CYCLE_MS = 6000;
    // Last part of each cycle shows the finished craft (full bar, glowing output).
    private static final double DONE_PART = 0.2;

    // AbstractGui.fillGradient is a protected instance method, so a tiny subclass exposes it.
    private static final Gradient GRADIENT = new Gradient();

    private JeiDrawUtil() {
    }

    // 0..1 while working, then 1.0 during the "done" part of the cycle.
    static double cycleProgress(int recipeTicks) {
        long cycle = Math.max(MIN_CYCLE_MS, Math.min(MAX_CYCLE_MS, recipeTicks * 50L));
        double t = (double) (System.currentTimeMillis() % cycle) / cycle;
        return Math.min(1.0, t / (1.0 - DONE_PART));
    }

    // Static bar holding only the recipe energy (relative to the machine buffer), same gradient as the machine GUIs.
    static void energyBar(MatrixStack matrixStack, int x, int y, int width, int height, int energy, int capacity) {
        int filled = Math.max(2, (int) Math.round((double) height * energy / capacity));
        int bottom = y + height;
        GRADIENT.draw(matrixStack, x, bottom - Math.min(height, filled), x + width, bottom, 0xFFFF8A1A, 0xFFB3151B);
    }

    // Progress bar (same colors as the machine GUIs).
    static void progressBar(MatrixStack matrixStack, int x, int y, int width, int height, double progress) {
        AbstractGui.fill(matrixStack, x, y, x + width, y + height, 0xFF373737);
        int filled = (int) (width * progress);
        if (filled > 0) {
            AbstractGui.fill(matrixStack, x, y, x + filled, y + height, 0xFFFFC21A);
        }
    }

    // Glow behind the output item when the craft is done. x/y = where the item is drawn.
    static void outputGlow(MatrixStack matrixStack, int x, int y, double progress) {
        if (progress >= 1.0) {
            AbstractGui.fill(matrixStack, x - 2, y - 2, x + 18, y + 18, 0x66FFE27A);
        }
    }

    static void centeredText(MatrixStack matrixStack, String text, int width, int y) {
        FontRenderer font = Minecraft.getInstance().font;
        font.draw(matrixStack, text, (width - font.width(text)) / 2.0F, y, 0xFF404040);
    }

    static String formatSeconds(int ticks) {
        double seconds = ticks / 20.0;
        return seconds == Math.floor(seconds) ? String.valueOf((int) seconds) : String.format("%.2f", seconds).replaceAll("0+$", "");
    }

    static boolean inside(double mouseX, double mouseY, int x, int y, int width, int height) {
        return mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
    }

    private static class Gradient extends AbstractGui {
        void draw(MatrixStack matrixStack, int x1, int y1, int x2, int y2, int topColor, int bottomColor) {
            fillGradient(matrixStack, x1, y1, x2, y2, topColor, bottomColor);
        }
    }
}
