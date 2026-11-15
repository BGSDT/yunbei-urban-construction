package com.beigu.yunbeiuc.screen;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix3x2fStack;

public final class DrawContext {
    private final GuiGraphicsExtractor extractor;

    public DrawContext(GuiGraphicsExtractor extractor) {
        this.extractor = extractor;
    }

    public GuiGraphicsExtractor getGraphics() { return extractor; }

    public Matrices getMatrices() { return new Matrices(extractor.pose()); }

    private static int opaque(int argb) {
        if (argb == 0) return 0;
        return (argb & 0xFF000000) == 0 ? (argb | 0xFF000000) : argb;
    }

    public void fill(int x0, int y0, int x1, int y1, int color) {
        extractor.fill(x0, y0, x1, y1, opaque(color));
    }

    public void fillGradient(int x0, int y0, int x1, int y1, int colorFrom, int colorTo) {
        extractor.fillGradient(x0, y0, x1, y1, opaque(colorFrom), opaque(colorTo));
    }

    public void drawBorder(int x, int y, int width, int height, int color) {
        fill(x, y, x + width, y + 1, color);
        fill(x, y + height - 1, x + width, y + height, color);
        fill(x, y, x + 1, y + height, color);
        fill(x + width - 1, y, x + width, y + height, color);
    }

    public void drawCenteredTextWithShadow(Font font, Component text, int x, int y, int color) {
        extractor.centeredText(font, text, x, y, opaque(color));
    }

    public void drawCenteredTextWithShadow(Font font, String text, int x, int y, int color) {
        extractor.centeredText(font, text, x, y, opaque(color));
    }

    public void drawTextWithShadow(Font font, Component text, int x, int y, int color) {
        extractor.text(font, text, x, y, opaque(color), true);
    }

    public void drawTextWithShadow(Font font, String text, int x, int y, int color) {
        extractor.text(font, text, x, y, opaque(color), true);
    }

    public void drawText(Font font, Component text, int x, int y, int color, boolean shadow) {
        extractor.text(font, text, x, y, opaque(color), shadow);
    }

    public void drawText(Font font, String text, int x, int y, int color, boolean shadow) {
        extractor.text(font, text, x, y, opaque(color), shadow);
    }

    public void drawText(Font font, FormattedCharSequence text, int x, int y, int color, boolean shadow) {
        extractor.text(font, text, x, y, opaque(color), shadow);
    }

    public void drawTexture(Identifier texture, int x, int y, int width, int height,
                            int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        int tw = Math.max(1, textureWidth);
        int th = Math.max(1, textureHeight);
        int rw = Math.max(1, regionWidth);
        int rh = Math.max(1, regionHeight);
        extractor.blit(texture, x, y, x + width, y + height,
                u / (float) tw, (u + rw) / (float) tw,
                v / (float) th, (v + rh) / (float) th);
    }

    public void drawTexture(Identifier texture, int x, int y, float u, float v,
                            int width, int height, int regionWidth, int regionHeight) {
        drawTexture(texture, x, y, width, height, Math.round(u), Math.round(v),
                regionWidth, regionHeight, Math.max(1, regionWidth), Math.max(1, regionHeight));
    }
    public void enableScissor(int x0, int y0, int x1, int y1) {
        extractor.enableScissor(x0, y0, x1, y1);
    }

    public void disableScissor() { extractor.disableScissor(); }

    public static final class Matrices {
        private final Matrix3x2fStack pose;

        Matrices(Matrix3x2fStack pose) {
            this.pose = pose;
        }

        public void pushPose() { pose.pushMatrix(); }

        public void popPose() { pose.popMatrix(); }

        public void translate(float x, float y, float z) { pose.translate(x, y); }
    }
}
