package com.beigu.yunbeiuc.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.font.TextRenderable;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;

// 26.3 删除了 Font#drawInBatch，改成 prepareText(...) → visit(GlyphVisitor) → render 的提交式管线；
public final class FontCompat {
    private FontCompat() {
    }

    public static void drawInBatch(Font font, Component text, float x, float y, int color, boolean shadow,
                                   Matrix4f matrix, MultiBufferSource buffers, Font.DisplayMode mode,
                                   int backgroundColor, int light) {
        drawPrepared(font.prepareText(text.getVisualOrderText(), x, y, color, shadow, false, backgroundColor),
                matrix, buffers, mode, light);
    }

    public static void drawInBatch8xOutline(Font font, FormattedCharSequence text, float x, float y,
                                            int color, int outlineColor, Matrix4f matrix,
                                            MultiBufferSource buffers, int light) {
        drawPrepared(font.prepare8xTextOutline(text, x, y, outlineColor),
                matrix, buffers, Font.DisplayMode.NORMAL, light);
    }

    private static void drawPrepared(Font.PreparedText prepared, Matrix4f matrix,
                                     MultiBufferSource buffers, Font.DisplayMode mode, int light) {
        if (prepared == null) {
            return;
        }
        prepared.visit(new Font.GlyphVisitor() {
            @Override
            public void acceptRenderable(TextRenderable renderable) {
                VertexConsumer consumer = buffers.getBuffer(renderable.renderType(mode));
                renderable.render(matrix, consumer, light, false);
            }
        });
    }
}
