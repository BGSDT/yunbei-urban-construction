package com.beigu.yunbeiuc.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** 1.16.5 GUI drawing bridge for the shared screen layout code. */
public final class DrawContext {
    private final PoseStack matrices;

    public DrawContext(PoseStack matrices) {
        this.matrices = matrices;
    }

    public PoseStack getMatrices() { return matrices; }

    public void fill(int x0, int y0, int x1, int y1, int color) {
        GuiComponent.fill(matrices, x0, y0, x1, y1, color);
    }

    public void drawBorder(int x, int y, int width, int height, int color) {
        fill(x, y, x + width, y + 1, color);
        fill(x, y + height - 1, x + width, y + height, color);
        fill(x, y, x + 1, y + height, color);
        fill(x + width - 1, y, x + width, y + height, color);
    }

    public void drawCenteredTextWithShadow(Font font, Component text, int x, int y, int color) {
        GuiComponent.drawCenteredString(matrices, font, text, x, y, color);
    }

    public void drawCenteredTextWithShadow(Font font, String text, int x, int y, int color) {
        GuiComponent.drawCenteredString(matrices, font, text, x, y, color);
    }

    public void drawTextWithShadow(Font font, Component text, int x, int y, int color) {
        GuiComponent.drawString(matrices, font, text, x, y, color);
    }

    public void drawTextWithShadow(Font font, String text, int x, int y, int color) {
        GuiComponent.drawString(matrices, font, text, x, y, color);
    }

    public void drawText(Font font, Component text, int x, int y, int color, boolean shadow) {
        if (shadow) font.drawShadow(matrices, text, x, y, color);
        else font.draw(matrices, text, x, y, color);
    }

    public void drawTexture(ResourceLocation texture, int x, int y, int width, int height,
                            int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        Minecraft.getInstance().getTextureManager().bind(texture);
        GuiComponent.blit(matrices, x, y, width, height, (float) u, (float) v,
                regionWidth, regionHeight, textureWidth, textureHeight);
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        var window = Minecraft.getInstance().getWindow();
        double scale = window.getGuiScale();
        RenderSystem.enableScissor((int) (x0 * scale),
                (int) ((window.getGuiScaledHeight() - y1) * scale),
                (int) ((x1 - x0) * scale), (int) ((y1 - y0) * scale));
    }

    public void disableScissor() { RenderSystem.disableScissor(); }
}
