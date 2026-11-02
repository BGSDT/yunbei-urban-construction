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

    public void fill(int x0, int y0, int x1, int y1, int color) {
        extractor.fill(x0, y0, x1, y1, color);
    }

    public void fillGradient(int x0, int y0, int x1, int y1, int colorFrom, int colorTo) {
        extractor.fillGradient(x0, y0, x1, y1, colorFrom, colorTo);
    }

    public void drawBorder(int x, int y, int width, int height, int color) {
        fill(x, y, x + width, y + 1, color);
        fill(x, y + height - 1, x + width, y + height, color);
        fill(x, y, x + 1, y + height, color);
        fill(x + width - 1, y, x + width, y + height, color);
    }

    public void drawCenteredTextWithShadow(Font font, Component text, int x, int y, int color) {
        extractor.centeredText(font, text, x, y, color);
    }

    public void drawCenteredTextWithShadow(Font font, String text, int x, int y, int color) {
        extractor.centeredText(font, text, x, y, color);
    }

    public void drawTextWithShadow(Font font, Component text, int x, int y, int color) {
        extractor.text(font, text, x, y, color, true);
    }

    public void drawTextWithShadow(Font font, String text, int x, int y, int color) {
        extractor.text(font, text, x, y, color, true);
    }

    public void drawText(Font font, Component text, int x, int y, int color, boolean shadow) {
        extractor.text(font, text, x, y, color, shadow);
    }

    public void drawText(Font font, String text, int x, int y, int color, boolean shadow) {
        extractor.text(font, text, x, y, color, shadow);
    }

    public void drawText(Font font, FormattedCharSequence text, int x, int y, int color, boolean shadow) {
        extractor.text(font, text, x, y, color, shadow);
    }

    public void drawTexture(Identifier texture, int x, int y, int width, int height,
                            int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        extractor.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, (float) u, (float) v,
                width, height, regionWidth, regionHeight, textureWidth);
    }

    public void drawTexture(Identifier texture, int x, int y, float u, float v,
                            int width, int height, int regionWidth, int regionHeight) {
        extractor.blit(RenderPipelines.GUI_TEXTURED, texture, x, y, u, v,
                width, height, regionWidth, regionHeight, regionWidth);
    }

    public void enableScissor(int x0, int y0, int x1, int y1) {
        extractor.enableScissor(x0, y0, x1, y1);
    }

    public void disableScissor() { extractor.disableScissor(); }

    /**
     * 26.x replaced the 3D {@code PoseStack} in GUI code with a 2D {@link Matrix3x2fStack} and draws
     * strictly in submission order, so the old Z translations (used only for layering) collapse into
     * plain 2D translations.
     */
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
