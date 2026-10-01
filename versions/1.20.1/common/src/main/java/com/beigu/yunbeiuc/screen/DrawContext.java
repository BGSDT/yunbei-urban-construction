package com.beigu.yunbeiuc.screen;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.resources.ResourceLocation;

/** 1.20.1 GUI drawing bridge for the shared screen layout code. */
public final class DrawContext {
    private final GuiGraphics graphics;

    public DrawContext(GuiGraphics graphics) {
        this.graphics = graphics;
    }

    public GuiGraphics getGraphics() { return graphics; }

    public PoseStack getMatrices() { return graphics.pose(); }

    public void fill(int x0, int y0, int x1, int y1, int color) {
        graphics.fill(x0, y0, x1, y1, color);
    }

    public void drawBorder(int x, int y, int width, int height, int color) {
        fill(x, y, x + width, y + 1, color);
        fill(x, y + height - 1, x + width, y + height, color);
        fill(x, y, x + 1, y + height, color);
        fill(x + width - 1, y, x + width, y + height, color);
    }

    public void drawCenteredTextWithShadow(Font font, Component text, int x, int y, int color) {
        graphics.drawCenteredString(font, text, x, y, color);
    }

    public void drawCenteredTextWithShadow(Font font, String text, int x, int y, int color) {
        graphics.drawCenteredString(font, text, x, y, color);
    }

    public void drawTextWithShadow(Font font, Component text, int x, int y, int color) {
        graphics.drawString(font, text, x, y, color);
    }

    public void drawTextWithShadow(Font font, String text, int x, int y, int color) {
        graphics.drawString(font, text, x, y, color);
    }

    public void drawText(Font font, Component text, int x, int y, int color, boolean shadow) {
        graphics.drawString(font, text, x, y, color, shadow);
    }

    public void drawText(Font font, String text, int x, int y, int color, boolean shadow) {
        graphics.drawString(font, text, x, y, color, shadow);
    }

    public void drawTexture(ResourceLocation texture, int x, int y, int width, int height,
                            int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        graphics.blit(texture, x, y, width, height, (float) u, (float) v,
                regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public void drawTexture(ResourceLocation texture, int x, int y, float u, float v,
                            int width, int height, int regionWidth, int regionHeight) {
        graphics.blit(texture, x, y, u, v, width, height, regionWidth, regionHeight);
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        graphics.enableScissor(x0, y0, x1, y1);
    }

    public void disableScissor() { graphics.disableScissor(); }
    public void drawText(Font font, FormattedCharSequence text, int x, int y, int color, boolean shadow) {
        graphics.drawString(font, text, x, y, color, shadow);
    }
}
