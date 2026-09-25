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

        // 子类渲染固定内容（如枚举驱动的 logo 纹理），在动态文本行之前
        renderFixedContent(entity, matrices, vertexConsumers, tickDelta, light, overlay, zOffset);

        List<CustomSignBlockEntity.TextLineData> lines = entity.getTextLines();
        int editingIndex = entity.getEditingLineIndex();
        int gizmoMode = editingIndex >= 0 ? entity.getEditingGizmoMode() : -1;
        if (editingIndex >= 0) TextGizmo.beginFrameRects();
        for (int i = 0; i < lines.size(); i++) {
            gizmoLineIndex = i;
            renderTextLine(entity, matrices, vertexConsumers, effectiveLight, overlay, zOffset, lines.get(i), i == editingIndex, gizmoMode, baseFrame);
        }

        matrices.popPose();
    }

    // 子类实现前置变换（平移/旋转）
    protected abstract void applyTransforms(PoseStack matrices, T entity);

    // 子类实现 Z 轴偏移
    protected abstract float getZOffset(T entity);

    // 子类可选渲染固定内容（枚举驱动的 logo 纹理等），在动态文本行之前执行；默认不渲染
    protected void renderFixedContent(T entity, PoseStack matrices, MultiBufferSource vertexConsumers,
                                      float tickDelta, int light, int overlay, float zOffset) {
    }

    // 检查纹理资源是否真实存在，避免渲染缺失纹理时反复刷错误日志
    private boolean textureExists(ResourceLocation id) {
        try {
            return Minecraft.getInstance().getResourceManager().hasResource(id);
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
                    renderRect(matrices, zOffset, lineData, w, h, editing, gizmoMode, baseFrame);
                    return;
                } catch (NumberFormatException ignored) {}
            }
        }

        if (text.startsWith("-texture")) {
            String[] parts = text.split("\\s+", 3);
            if (parts.length >= 2) {
                // 路径支持 {字段名} 占位符（如按枚举/编号选择 logo 纹理），与 -json 分支一致先解析；
                // ResourceLocation 仅允许小写字符，大写字母会导致 tryParse 失败而回退为文本渲染，这里统一转小写
                ResourceLocation textureId = ResourceLocation.tryParse(entity.resolvePlaceholders(parts[1]).trim().toLowerCase(Locale.ROOT));
                // 仅当路径以 .png 结尾且资源真实存在时才按贴图渲染；
                // 目录条目会被 getResource 误判为存在（jar 内含目录项），必须用后缀过滤掉，否则输入过程会反复报错；
                // 无效路径（占位符按字段条件返回空、资源不存在、格式错误）静默跳过整行，不回退为文本渲染
                if (textureId != null && textureId.getPath().endsWith(".png") && textureExists(textureId)) {
                    // 可选第三参数：x 偏移占位符（单位同 xOffset，如窄/宽版 logo 联动的 ±1px），叠加到行 xOffset
                    float xShift = 0f;
                    if (parts.length >= 3) {
                        try { xShift = Float.parseFloat(entity.resolvePlaceholders(parts[2]).trim()); }
                        catch (NumberFormatException ignored) {}
                    }
                    CustomSignBlockEntity.TextLineData shifted = lineData;
                    if (xShift != 0f) {
                        shifted = lineData.copy();
                        shifted.setXOffset(lineData.getXOffset() + xShift);
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

        // 行内容走缓冲层帧末绘制，下方描边/gizmo 为立即模式绘制——
        // 编辑行先 flush 缓冲层，否则帧末内容会覆盖 gizmo 轴与编辑描边
        if (editing && vertexConsumers instanceof MultiBufferSource.BufferSource immediate) {
            immediate.endBatch();
        }

        if (editing) renderEditingOutline(matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize(), lineData.getScaleX(), lineData.getScaleY());
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

        // 渲染时解析 {字段名} 占位符为固定 NBT 字段的值
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

        // 行内容走缓冲层帧末绘制，下方描边/gizmo 为立即模式绘制——
        // 编辑行先 flush 缓冲层，否则帧末内容会覆盖 gizmo 轴与编辑描边
        if (editing && vertexConsumers instanceof MultiBufferSource.BufferSource immediate) {
            immediate.endBatch();
        }

        if (editing) renderEditingOutline(matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize(), lineData.getScaleX(), lineData.getScaleY());
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, renderX, renderY, textWidth, textHeight, 1f, 1f);
        if (gizmoFrame != null) TextGizmo.updateAndRender(gizmoMode, baseFrame, gizmoFrame, lineData, zOffset, textWidth / 2f * baseScale, textHeight / 2f * baseScale);

        matrices.popPose();
    }

    private void drawStyledText(Component renderText, float x, float y, CustomSignBlockEntity.TextLineData lineData, Matrix4f matrix, MultiBufferSource vertexConsumers, int light) {
        boolean outline = lineData.isOutline();
        boolean shadow = lineData.isShadow();
        if (outline) {
            if (shadow) {
                this.textRenderer.drawInBatch(renderText, x, y, lineData.getColor(), true,
                        matrix, vertexConsumers, false, 0, light);
            }
            this.textRenderer.drawInBatch8xOutline(renderText.getVisualOrderText(), x, y, lineData.getColor(), lineData.getOutlineColor(),
                    matrix, vertexConsumers, light);
        } else {
            this.textRenderer.drawInBatch(renderText, x, y, lineData.getColor(), shadow,
                    matrix, vertexConsumers, false, 0, light);
        }
    }

    private void renderRect(PoseStack matrices, float zOffset, CustomSignBlockEntity.TextLineData lineData, float width, float height, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.pushPose();
        float centerX = lineData.getXOffset() / 16f;
        float centerY = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;
        // 矩形大小乘以 fontSize 及 XY 缩放；矩形本身在局部 z=0 平面上，scaleZ 对其无可见效果
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

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        buffer.vertex(matrix, -halfW, -halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, halfW, -halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, halfW, halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, -halfW, -halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, halfW, halfH, 0).color(r, g, b, a).endVertex();
        buffer.vertex(matrix, -halfW, halfH, 0).color(r, g, b, a).endVertex();
        BufferUploader.end(buffer);

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        if (editing) renderEditingOutline(matrices, -halfW, -halfH, halfW * 2, halfH * 2, 1f, 1f, 1f);
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

        // 图片顶点在缓冲层，gizmo 轴/描边为立即模式——编辑行先 flush 贴图层，
        // 否则帧末绘制图片时会覆盖 gizmo 轴与编辑描边
        if (editing && vertexConsumers instanceof MultiBufferSource.BufferSource immediate) {
            immediate.endBatch();
        }

        if (editing) renderEditingOutline(matrices, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 1f, 1f, 1f);
        TextGizmo.addLineRect(matrices.last().pose(), gizmoLineIndex, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 1f, 1f);
        if (editing && gizmoMode >= 0) {
            TextGizmo.updateAndRender(gizmoMode, baseFrame, new Matrix4f(matrices.last().pose()), lineData,
                    zOffset, imageSize / 2f, imageSize / 2f);
        }

        matrices.popPose();
    }

    // 在当前矩阵局部坐标系内绘制一个包住 (left,top)-(left+w,top+h) 区域的绿色描边矩形框，用于标注正在编辑的文本行
    // unitScale：局部坐标 1 单位对应的方块尺寸（1 像素 = 1/16 方块）；scaleX/scaleY 为矩阵所含缩放，
    // 用于抵消厚度被放大，保证描边在世界空间中恒为 0.25px 粗
    private void renderEditingOutline(PoseStack matrices, float left, float top, float w, float h, float unitScale, float scaleX, float scaleY) {
        float px = 0.25f / 16f / Math.max(1e-5f, unitScale);
        float thicknessX = px / Math.max(0.01f, scaleX);
        float thicknessY = px / Math.max(0.01f, scaleY);
        float marginX = thicknessX;
        float marginY = thicknessY;
        float x0 = left - marginX, y0 = top - marginY, x1 = left + w + marginX, y1 = top + h + marginY;

        int r = 0, g = 255, b = 255, a = 255;
        Matrix4f matrix = matrices.last().pose();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();
        buffer.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR);
        addOutlineBar(buffer, matrix, x0, y0, x1, y0 + thicknessY, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y1 - thicknessY, x1, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y0, x0 + thicknessX, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x1 - thicknessX, y0, x1, y1, r, g, b, a);
        BufferUploader.end(buffer);

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
