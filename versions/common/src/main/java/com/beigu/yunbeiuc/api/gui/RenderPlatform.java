package com.beigu.yunbeiuc.api.gui;

import com.beigu.yunbeiuc.screen.DrawContext;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;

/** Version-neutral rendering operations for text, textures and primitives. */
public interface RenderPlatform {
    /**
     * The OpenGL primitive used by {@link #beginLines}.
     *
     * <p>1.16.5 takes a raw {@code GL_LINES}/{@code GL_LINE_STRIP} int, while 1.17+ take
     * {@code VertexFormat.Mode.LINES}/{@code LINE_STRIP}; this enum selects between them.
     */
    enum LinePrimitive {
        LINES,
        LINE_STRIP
    }

        /** Starts a `POSITION_COLOR` batch and returns the consumer to write vertices into. */
    VertexConsumer beginLines(LinePrimitive primitive);

    /** Finishes and submits the batch started by {@link #beginLines}. */
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

    /** Adds one {@code POSITION_COLOR} vertex with float colour components in the 0..1 range. */
    void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z,
                float red, float green, float blue, float alpha);

    /**
     * Adds one vertex carrying position, colour (0..255 components), uv, overlay, light and
     * normal. Attributes that the active vertex format does not consume are ignored.
     */
    void vertex(VertexConsumer consumer, PoseStack.Pose pose, float x, float y, float z,
                int red, int green, int blue, int alpha, float u, float v, int overlay, int light,
                float normalX, float normalY, float normalZ);
}
