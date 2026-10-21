package com.beigu.yunbeiuc.api.gui;

import com.beigu.yunbeiuc.screen.DrawContext;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;

public interface RenderPlatform {
    enum LinePrimitive {
        LINES,
        LINE_STRIP
    }

    VertexConsumer beginLines(LinePrimitive primitive);

    void endLines();

    void fill(DrawContext context, int left, int top, int right, int bottom, int color);

    void drawBorder(DrawContext context, int x, int y, int width, int height, int color);

    void drawText(DrawContext context, Font font, Component text, int x, int y, int color, boolean shadow);

    void drawCenteredText(DrawContext context, Font font, Component text, int x, int y, int color);

    void drawTexture(DrawContext context, ResourceLocation texture, int x, int y, int width, int height,
                     int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight);

    void enableScissor(DrawContext context, int left, int top, int right, int bottom);

    void disableScissor(DrawContext context);

    void rotateY(PoseStack matrices, float degrees);

    void drawInBatch(Font font, Component text, float x, float y, int color, boolean shadow,
                     PoseStack matrices, MultiBufferSource buffers, int backgroundColor, int light);

    void vertex(VertexConsumer consumer, PoseStack matrices, float x, float y, float z);

    void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z,
                float red, float green, float blue, float alpha);

    void line(VertexConsumer consumer, PoseStack.Pose pose,
              float x1, float y1, float z1, float x2, float y2, float z2,
              float red, float green, float blue, float alpha);

    void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z,
                int red, int green, int blue, int alpha, float u, float v, int overlay, int light,
                float normalX, float normalY, float normalZ);
}
