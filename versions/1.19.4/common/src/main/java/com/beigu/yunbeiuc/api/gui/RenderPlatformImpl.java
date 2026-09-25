package com.beigu.yunbeiuc.api.gui;

import com.beigu.yunbeiuc.screen.DrawContext;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

/** Drawing operations for the Minecraft 1.19.4 GUI API. */
public final class RenderPlatformImpl implements RenderPlatform {
    @Override
    public void drawInBatch(Font font, Component text, float x, float y, int color, boolean shadow,
                            com.mojang.blaze3d.vertex.PoseStack matrices,
                            net.minecraft.client.renderer.MultiBufferSource buffers, int backgroundColor, int light) {
        font.drawInBatch(text, x, y, color, shadow, matrices.last().pose(), buffers,
                Font.DisplayMode.NORMAL, backgroundColor, light);
    }

    @Override
    public void vertex(com.mojang.blaze3d.vertex.VertexConsumer consumer,
                       com.mojang.blaze3d.vertex.PoseStack matrices, float x, float y, float z) {
        consumer.vertex(matrices.last().pose(), x, y, z);
    }
    @Override public void rotateY(com.mojang.blaze3d.vertex.PoseStack matrices, float degrees) {
        matrices.mulPose(com.mojang.math.Axis.YP.rotationDegrees(degrees));
    }
    @Override
    public void fill(DrawContext context, int left, int top, int right, int bottom, int color) {
        context.fill(left, top, right, bottom, color);
    }

    @Override
    public void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.drawBorder(x, y, width, height, color);
    }

    @Override
    public void drawText(DrawContext context, Font font, Component text, int x, int y, int color, boolean shadow) {
        context.drawText(font, text, x, y, color, shadow);
    }

    @Override
    public void drawCenteredText(DrawContext context, Font font, Component text, int x, int y, int color) {
        context.drawCenteredTextWithShadow(font, text, x, y, color);
    }

    @Override
    public void drawTexture(DrawContext context, ResourceLocation texture, int x, int y, int width, int height,
                            int u, int v, int regionWidth, int regionHeight, int textureWidth, int textureHeight) {
        context.drawTexture(texture, x, y, width, height, u, v, regionWidth, regionHeight, textureWidth, textureHeight);
    }

    @Override
    public void enableScissor(DrawContext context, int left, int top, int right, int bottom) {
        context.enableScissor(left, top, right, bottom);
    }

    @Override
    public void disableScissor(DrawContext context) {
        context.disableScissor();
    }
}
