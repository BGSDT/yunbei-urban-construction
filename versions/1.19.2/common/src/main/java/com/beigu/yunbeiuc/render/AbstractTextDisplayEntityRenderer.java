package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.resources.ResourceLocation;
import com.mojang.math.Vector3f;
import com.mojang.math.Matrix4f;

import java.util.List;
import java.util.Locale;

public abstract class AbstractTextDisplayEntityRenderer<T extends CustomSignBlockEntity> implements BlockEntityRenderer<T> {
    protected final Font textRenderer;
    private int gizmoLineIndex;

    protected AbstractTextDisplayEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.textRenderer = ctx.getFont();
    }

    @Override
    public final void render(T entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        matrices.pushPose();
        applyTransforms(matrices, entity);
        Matrix4f baseFrame = new Matrix4f(matrices.last().pose());
        float zOffset = getZOffset(entity);
        int effectiveLight = entity.isGlowingText() ? LightTexture.FULL_BRIGHT : light;

        renderFixedContent(entity, matrices, vertexConsumers, tickDelta, light, overlay, zOffset);

        List<CustomSignBlockEntity.TextLineData> lines = entity.getTextLines();
        int editingIndex = entity.getEditingLineIndex();
        int editingMode = entity.getEditingGizmoMode();
        int gizmoMode = editingIndex >= 0 ? editingMode : -1;
        if (editingIndex >= 0 || editingMode == -2) TextGizmo.beginFrameRects();
        for (int i = 0; i < lines.size(); i++) {
            gizmoLineIndex = i;
            renderTextLine(entity, matrices, vertexConsumers, effectiveLight, overlay, zOffset, lines.get(i), i == editingIndex, gizmoMode, baseFrame);
        }

        matrices.popPose();
    }

    protected abstract void applyTransforms(PoseStack matrices, T entity);

    protected abstract float getZOffset(T entity);

    protected void renderFixedContent(T entity, PoseStack matrices, MultiBufferSource vertexConsumers,
                                      float tickDelta, int light, int overlay, float zOffset) {
    }

    private boolean textureExists(ResourceLocation id) {
        try {
            return Minecraft.getInstance().getResourceManager().getResource(id).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    private void renderTextLine(T entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, float zOffset, CustomSignBlockEntity.TextLineData lineData, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        if (lineData.getText().isEmpty()) return;
        String text = lineData.getText().trim();

        if (text.startsWith("-rect")) {
            String[] parts = text.split("\\s+");
            if (parts.length >= 3) {
                try {
                    float w = Float.parseFloat(parts[1]);
                    float h = Float.parseFloat(parts[2]);
                    renderRect(matrices, vertexConsumers, zOffset, lineData, w, h, editing, gizmoMode, baseFrame);
                    return;
                } catch (NumberFormatException ignored) {}
            }
        }

        if (text.startsWith("-texture")) {
            String[] parts = text.split("\\s+", 4);
            if (parts.length >= 2) {
                ResourceLocation textureId = ResourceLocation.tryParse(entity.resolvePlaceholders(parts[1]).trim().toLowerCase(Locale.ROOT));
                if (textureId != null && textureId.getPath().endsWith(".png") && textureExists(textureId)) {
                    float xShift = 0f;
                    if (parts.length >= 3) {
                        try { xShift = Float.parseFloat(entity.resolvePlaceholders(parts[2]).trim()); }
                        catch (NumberFormatException ignored) {}
                    }
                    float rotZ = 0f;
                    if (parts.length >= 4) {
                        try { rotZ = Float.parseFloat(entity.resolvePlaceholders(parts[3]).trim()); }
                        catch (NumberFormatException ignored) {}
                    }
                    CustomSignBlockEntity.TextLineData shifted = lineData;
                    if (xShift != 0f || rotZ != 0f) {
                        shifted = lineData.copy();
                        if (xShift != 0f) shifted.setXOffset(lineData.getXOffset() + xShift);
                        if (rotZ != 0f) shifted.setRotZ(lineData.getRotZ() + rotZ);
                    }
                    renderTexture(matrices, vertexConsumers, light, overlay, zOffset, shifted, textureId, editing, gizmoMode, baseFrame);
                }
                return;
            }
        }

        if (text.startsWith("-json")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length >= 2) {
                try {
                    Component jsonText = Component.Serializer.fromJsonLenient(entity.resolvePlaceholders(parts[1]));
                    if (jsonText != null) {
                        renderJsonText(matrices, vertexConsumers, light, zOffset, lineData, jsonText, editing, gizmoMode, baseFrame);
                        return;
                    }
                } catch (Exception ignored) {}
            }
        }

        renderText(entity, matrices, vertexConsumers, light, zOffset, lineData, editing, gizmoMode, baseFrame);
    }

    private void applyRotation(PoseStack matrices, CustomSignBlockEntity.TextLineData lineData) {
        if (lineData.getRotX() != 0) matrices.mulPose(Vector3f.XP.rotationDegrees(lineData.getRotX()));
        if (lineData.getRotY() != 0) matrices.mulPose(Vector3f.YP.rotationDegrees(lineData.getRotY()));
        if (lineData.getRotZ() != 0) matrices.mulPose(Vector3f.ZP.rotationDegrees(lineData.getRotZ()));
    }

    private void renderJsonText(PoseStack matrices, MultiBufferSource vertexConsumers, int light, float zOffset, CustomSignBlockEntity.TextLineData lineData, Component jsonText, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.pushPose();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);
        applyRotation(matrices, lineData);
        Matrix4f gizmoFrame = editing && gizmoMode >= 0 ? new Matrix4f(matrices.last().pose()) : null;
        matrices.scale(baseScale * lineData.getScaleX(), -baseScale * lineData.getScaleY(), baseScale * lineData.getScaleZ());

        MutableComponent renderText = jsonText.copy();
        if (renderText.getStyle().getFont() == null) {
            renderText = renderText.copy().withStyle(s -> s.withFont(new ResourceLocation("minecraft", "uniform")));
        }
        if (lineData.isBold()) renderText = renderText.copy().withStyle(s -> s.withBold(true));
        if (lineData.isItalic()) renderText = renderText.copy().withStyle(s -> s.withItalic(true));
        if (lineData.isUnderline()) renderText = renderText.copy().withStyle(s -> s.withUnderlined(true));

        int textWidth = this.textRenderer.width(renderText);
        int textHeight = this.textRenderer.lineHeight;

        float renderX = switch (lineData.getAlignment().hAlign) {
            case 0 -> 0;
            case 2 -> -textWidth;
            default -> -textWidth / 2.0f;
        };
        float renderY = switch (lineData.getAlignment().vAlign) {
            case 0 -> 0;
            case 2 -> -textHeight;
            default -> -textHeight / 2.0f;
        };

        drawStyledText(renderText, renderX, renderY, lineData, matrices.last().pose(), vertexConsumers, light);

        if (editing && vertexConsumers instanceof MultiBufferSource.BufferSource immediate) {
            immediate.endBatch();
        }

        if (editing) renderEditingOutline(matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize(), lineData.getScaleX(), lineData.getScaleY(), 0x00FFFF);
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, renderX, renderY, textWidth, textHeight, 1f, 1f);
        if (gizmoFrame != null) TextGizmo.updateAndRender(gizmoMode, baseFrame, gizmoFrame, lineData, zOffset, textWidth / 2f * baseScale, textHeight / 2f * baseScale);

        matrices.popPose();
    }

    private void renderText(T entity, PoseStack matrices, MultiBufferSource vertexConsumers, int light, float zOffset, CustomSignBlockEntity.TextLineData lineData, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.pushPose();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);
        applyRotation(matrices, lineData);
        Matrix4f gizmoFrame = editing && gizmoMode >= 0 ? new Matrix4f(matrices.last().pose()) : null;
        matrices.scale(baseScale * lineData.getScaleX(), -baseScale * lineData.getScaleY(), baseScale * lineData.getScaleZ());

        Style style = Style.EMPTY
                .withBold(lineData.isBold())
                .withItalic(lineData.isItalic())
                .withUnderlined(lineData.isUnderline())
                .withFont(new ResourceLocation("minecraft", "uniform"));

        Component renderText = Text.literal(entity.resolvePlaceholders(lineData.getText())).setStyle(style);
        int textWidth = this.textRenderer.width(renderText);
        int textHeight = this.textRenderer.lineHeight;

        float renderX = switch (lineData.getAlignment().hAlign) {
            case 0 -> 0;
            case 2 -> -textWidth;
            default -> -textWidth / 2.0f;
        };
        float renderY = switch (lineData.getAlignment().vAlign) {
            case 0 -> 0;
            case 2 -> -textHeight;
            default -> -textHeight / 2.0f;
        };

        drawStyledText(renderText, renderX, renderY, lineData, matrices.last().pose(), vertexConsumers, light);

        if (editing && vertexConsumers instanceof MultiBufferSource.BufferSource immediate) {
            immediate.endBatch();
        }

        if (editing) renderEditingOutline(matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize(), lineData.getScaleX(), lineData.getScaleY(), 0x00FFFF);
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, renderX, renderY, textWidth, textHeight, 1f, 1f);
        if (gizmoFrame != null) TextGizmo.updateAndRender(gizmoMode, baseFrame, gizmoFrame, lineData, zOffset, textWidth / 2f * baseScale, textHeight / 2f * baseScale);

        matrices.popPose();
    }

    private void drawStyledText(Component renderText, float x, float y, CustomSignBlockEntity.TextLineData lineData, Matrix4f matrix, MultiBufferSource vertexConsumers, int light) {
        int textLight = lineData.isGlowing() ? 0xF000F0 : light;
        boolean outline = lineData.isOutline();
        boolean shadow = lineData.isShadow();
        if (outline) {
            if (shadow) {
                this.textRenderer.drawInBatch(renderText, x, y, lineData.getColor(), true,
                        matrix, vertexConsumers, false, 0, textLight);
            }
            int outlineColor = resolveOutlineColor(lineData);
            Component outlineText = renderText.copy()
                    .withStyle(s -> s.withColor(net.minecraft.network.chat.TextColor.fromRgb(outlineColor & 0xFFFFFF)));
            this.textRenderer.drawInBatch8xOutline(outlineText.getVisualOrderText(), x, y, lineData.getColor(), outlineColor,
                    matrix, vertexConsumers, textLight);
        } else {
            this.textRenderer.drawInBatch(renderText, x, y, lineData.getColor(), shadow,
                    matrix, vertexConsumers, false, 0, textLight);
        }
    }

    private void renderRect(PoseStack matrices, MultiBufferSource vertexConsumers, float zOffset, CustomSignBlockEntity.TextLineData lineData, float width, float height, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.pushPose();
        float centerX = lineData.getXOffset() / 16f;
        float centerY = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;
        float scale = lineData.getFontSize();
        float halfW = width / 16f / 2f * scale * lineData.getScaleX();
        float halfH = height / 16f / 2f * scale * lineData.getScaleY();

        float offsetX = switch (lineData.getAlignment().hAlign) {
            case 0 -> halfW;
            case 2 -> -halfW;
            default -> 0;
        };
        float offsetY = switch (lineData.getAlignment().vAlign) {
            case 0 -> -halfH;
            case 2 -> halfH;
            default -> 0;
        };

        matrices.translate(centerX + offsetX, centerY + offsetY, zPos);
        applyRotation(matrices, lineData);

        int color = lineData.getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) == 0 ? 255 : (color >> 24) & 0xFF;

        Matrix4f matrix = matrices.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, -halfW, -halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, halfW, -halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, halfW, halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, -halfW, -halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, halfW, halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, -halfW, halfH, 0).color(r, g, b, a).endVertex();
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        if (lineData.isOutline()) {
            flushBatch(vertexConsumers);
            renderEditingOutline(matrices, -halfW, -halfH, halfW * 2, halfH * 2, 0.25f, 1f, 1f, resolveOutlineColor(lineData));
        }

        if (editing) renderEditingOutline(matrices, -halfW, -halfH, halfW * 2, halfH * 2, 1f, 1f, 1f, 0x00FFFF);
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, -halfW, -halfH, halfW * 2, halfH * 2, 1f, 1f);
        if (editing && gizmoMode >= 0) {
            TextGizmo.updateAndRender(gizmoMode, baseFrame, new Matrix4f(matrices.last().pose()), lineData,
                    zOffset, width / 16f / 2f * lineData.getFontSize(), height / 16f / 2f * lineData.getFontSize());
        }

        matrices.popPose();
    }

    private void renderTexture(PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay, float zOffset, CustomSignBlockEntity.TextLineData lineData, ResourceLocation textureId, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.pushPose();

        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);

        float imageSize = 0.4f * lineData.getFontSize();
        float halfWidth = imageSize / 2f * lineData.getScaleX();
        float halfHeight = imageSize / 2f * lineData.getScaleY();

        float offsetX = switch (lineData.getAlignment().hAlign) {
            case 0 -> halfWidth;
            case 2 -> -halfWidth;
            default -> 0;
        };
        float offsetY = switch (lineData.getAlignment().vAlign) {
            case 0 -> -halfHeight;
            case 2 -> halfHeight;
            default -> 0;
        };

        matrices.translate(offsetX, offsetY, 0);
        applyRotation(matrices, lineData);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.entityCutout(textureId));
        Matrix4f positionMatrix = matrices.last().pose();

        consumer.vertex(positionMatrix, -halfWidth, -halfHeight, 0).color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();
        consumer.vertex(positionMatrix, halfWidth, -halfHeight, 0).color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();
        consumer.vertex(positionMatrix, halfWidth, halfHeight, 0).color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();
        consumer.vertex(positionMatrix, -halfWidth, halfHeight, 0).color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();

        if (editing && vertexConsumers instanceof MultiBufferSource.BufferSource immediate) {
            immediate.endBatch();
        }

        if (lineData.isOutline()) {
            flushBatch(vertexConsumers);
            renderEditingOutline(matrices, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 0.25f, 1f, 1f, resolveOutlineColor(lineData));
        }

        if (editing) renderEditingOutline(matrices, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 1f, 1f, 1f, 0x00FFFF);
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 1f, 1f);
        if (editing && gizmoMode >= 0) {
            TextGizmo.updateAndRender(gizmoMode, baseFrame, new Matrix4f(matrices.last().pose()), lineData,
                    zOffset, imageSize / 2f, imageSize / 2f);
        }

        matrices.popPose();
    }

    private static void flushBatch(MultiBufferSource vertexConsumers) {
        if (vertexConsumers instanceof MultiBufferSource.BufferSource queued) {
            queued.endBatch();
        }
    }
    private static int resolveOutlineColor(CustomSignBlockEntity.TextLineData lineData) {
        int outline = lineData.getOutlineColor();
        if (outline == -1) return 0x000000;
        int outlineRgb = outline & 0xFFFFFF;
        int textRgb = lineData.getColor() & 0xFFFFFF;
        if (outlineRgb != textRgb) return outlineRgb;
        int r = (textRgb >> 16) & 0xFF, g = (textRgb >> 8) & 0xFF, b = textRgb & 0xFF;
        int luma = (r * 299 + g * 587 + b * 114) / 1000;
        return luma >= 128 ? 0x000000 : 0xFFFFFF;
    }
    private void renderEditingOutline(PoseStack matrices, float left, float top, float w, float h, float unitScale, float scaleX, float scaleY, int rgb) {
        float px = 0.25f / 16f / Math.max(1e-5f, unitScale);
        float thicknessX = px / Math.max(0.01f, scaleX);
        float thicknessY = px / Math.max(0.01f, scaleY);
        float marginX = thicknessX;
        float marginY = thicknessY;
        float x0 = left - marginX, y0 = top - marginY, x1 = left + w + marginX, y1 = top + h + marginY;

        int r = (rgb >> 16) & 0xFF, g = (rgb >> 8) & 0xFF, b = rgb & 0xFF;
        int a = 255;
        Matrix4f matrix = matrices.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        addOutlineBar(buffer, matrix, x0, y0, x1, y0 + thicknessY, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y1 - thicknessY, x1, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y0, x0 + thicknessX, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x1 - thicknessX, y0, x1, y1, r, g, b, a);
        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
    }

    private void addOutlineBar(BufferBuilder buffer, Matrix4f matrix, float x0, float y0, float x1, float y1, int r, int g, int b, int a) {
        buffer.vertex(matrix, x0, y0, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y0, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x0, y0, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, x0, y1, 0).color(r, g, b, a).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(T blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
