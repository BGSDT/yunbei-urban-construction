package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.List;
import java.util.Locale;

public abstract class AbstractTextDisplayEntityRenderer<T extends CustomSignBlockEntity> implements BlockEntityRenderer<T> {
    protected final TextRenderer textRenderer;
    private int gizmoLineIndex;

    protected AbstractTextDisplayEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public final void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        applyTransforms(matrices, entity);
        Matrix4f baseFrame = new Matrix4f(matrices.peek().getPositionMatrix());
        float zOffset = getZOffset(entity);
        int effectiveLight = entity.isGlowingText() ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light;

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

        matrices.pop();
    }

    // 子类实现前置变换（平移/旋转）
    protected abstract void applyTransforms(MatrixStack matrices, T entity);

    // 子类实现 Z 轴偏移
    protected abstract float getZOffset(T entity);

    // 子类可选渲染固定内容（枚举驱动的 logo 纹理等），在动态文本行之前执行；默认不渲染
    protected void renderFixedContent(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                      float tickDelta, int light, int overlay, float zOffset) {
    }

    // 检查纹理资源是否真实存在，避免渲染缺失纹理时反复刷错误日志
    private boolean textureExists(Identifier id) {
        try {
            return MinecraftClient.getInstance().getResourceManager().getResource(id).isPresent();
        } catch (Exception e) {
            return false;
        }
    }

    private void renderTextLine(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float zOffset, CustomSignBlockEntity.TextLineData lineData, boolean editing, int gizmoMode, Matrix4f baseFrame) {
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
                // Identifier 仅允许小写字符，大写字母会导致 tryParse 失败而回退为文本渲染，这里统一转小写
                Identifier textureId = Identifier.tryParse(entity.resolvePlaceholders(parts[1]).trim().toLowerCase(Locale.ROOT));
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
                    Text jsonText = Text.Serializer.fromLenientJson(entity.resolvePlaceholders(parts[1]));
                    if (jsonText != null) {
                        renderJsonText(matrices, vertexConsumers, light, zOffset, lineData, jsonText, editing, gizmoMode, baseFrame);
                        return;
                    }
                } catch (Exception ignored) {}
            }
        }

        renderText(entity, matrices, vertexConsumers, light, zOffset, lineData, editing, gizmoMode, baseFrame);
    }

    private void applyRotation(MatrixStack matrices, CustomSignBlockEntity.TextLineData lineData) {
        if (lineData.getRotX() != 0) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(lineData.getRotX()));
        if (lineData.getRotY() != 0) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(lineData.getRotY()));
        if (lineData.getRotZ() != 0) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(lineData.getRotZ()));
    }

    private void renderJsonText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float zOffset, CustomSignBlockEntity.TextLineData lineData, Text jsonText, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.push();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);
        applyRotation(matrices, lineData);
        Matrix4f gizmoFrame = editing && gizmoMode >= 0 ? new Matrix4f(matrices.peek().getPositionMatrix()) : null;
        matrices.scale(baseScale * lineData.getScaleX(), -baseScale * lineData.getScaleY(), baseScale * lineData.getScaleZ());

        Text renderText = jsonText.copy();
        if (renderText.getStyle().getFont() == null) {
            renderText = renderText.copy().styled(s -> s.withFont(new Identifier("minecraft", "uniform")));
        }
        if (lineData.isBold()) renderText = renderText.copy().styled(s -> s.withBold(true));
        if (lineData.isItalic()) renderText = renderText.copy().styled(s -> s.withItalic(true));
        if (lineData.isUnderline()) renderText = renderText.copy().styled(s -> s.withUnderline(true));

        int textWidth = this.textRenderer.getWidth(renderText);
        int textHeight = this.textRenderer.fontHeight;

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

        drawStyledText(renderText, renderX, renderY, lineData, matrices.peek().getPositionMatrix(), vertexConsumers, light);

        if (editing) renderEditingOutline(matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize(), lineData.getScaleX(), lineData.getScaleY());
        TextGizmo.addLineRect(matrices.peek().getPositionMatrix(), gizmoLineIndex, renderX, renderY, textWidth, textHeight, 1f, 1f);
        if (gizmoFrame != null) TextGizmo.updateAndRender(gizmoMode, baseFrame, gizmoFrame, lineData, zOffset, textWidth / 2f * baseScale, textHeight / 2f * baseScale);

        matrices.pop();
    }

    private void renderText(T entity, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float zOffset, CustomSignBlockEntity.TextLineData lineData, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.push();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);
        applyRotation(matrices, lineData);
        Matrix4f gizmoFrame = editing && gizmoMode >= 0 ? new Matrix4f(matrices.peek().getPositionMatrix()) : null;
        matrices.scale(baseScale * lineData.getScaleX(), -baseScale * lineData.getScaleY(), baseScale * lineData.getScaleZ());

        Style style = Style.EMPTY
                .withBold(lineData.isBold())
                .withItalic(lineData.isItalic())
                .withUnderline(lineData.isUnderline())
                .withFont(new Identifier("minecraft", "uniform"));

        // 渲染时解析 {字段名} 占位符为固定 NBT 字段的值
        Text renderText = Text.literal(entity.resolvePlaceholders(lineData.getText())).setStyle(style);
        int textWidth = this.textRenderer.getWidth(renderText);
        int textHeight = this.textRenderer.fontHeight;

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

        drawStyledText(renderText, renderX, renderY, lineData, matrices.peek().getPositionMatrix(), vertexConsumers, light);

        if (editing) renderEditingOutline(matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize(), lineData.getScaleX(), lineData.getScaleY());
        TextGizmo.addLineRect(matrices.peek().getPositionMatrix(), gizmoLineIndex, renderX, renderY, textWidth, textHeight, 1f, 1f);
        if (gizmoFrame != null) TextGizmo.updateAndRender(gizmoMode, baseFrame, gizmoFrame, lineData, zOffset, textWidth / 2f * baseScale, textHeight / 2f * baseScale);

        matrices.pop();
    }

    private void drawStyledText(Text renderText, float x, float y, CustomSignBlockEntity.TextLineData lineData, Matrix4f matrix, VertexConsumerProvider vertexConsumers, int light) {
        boolean outline = lineData.isOutline();
        boolean shadow = lineData.isShadow();
        if (outline) {
            if (shadow) {
                this.textRenderer.draw(renderText, x, y, lineData.getColor(), true,
                        matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
            }
            this.textRenderer.drawWithOutline(renderText.asOrderedText(), x, y, lineData.getColor(), lineData.getOutlineColor(),
                    matrix, vertexConsumers, light);
        } else {
            this.textRenderer.draw(renderText, x, y, lineData.getColor(), shadow,
                    matrix, vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        }
    }

    private void renderRect(MatrixStack matrices, float zOffset, CustomSignBlockEntity.TextLineData lineData, float width, float height, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.push();
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

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, -halfW, -halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, halfW, -halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, halfW, halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, -halfW, -halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, halfW, halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, -halfW, halfH, 0).color(r, g, b, a).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        if (editing) renderEditingOutline(matrices, -halfW, -halfH, halfW * 2, halfH * 2, 1f, 1f, 1f);
        TextGizmo.addLineRect(matrices.peek().getPositionMatrix(), gizmoLineIndex, -halfW, -halfH, halfW * 2, halfH * 2, 1f, 1f);
        if (editing && gizmoMode >= 0) {
            TextGizmo.updateAndRender(gizmoMode, baseFrame, new Matrix4f(matrices.peek().getPositionMatrix()), lineData,
                    zOffset, width / 16f / 2f * lineData.getFontSize(), height / 16f / 2f * lineData.getFontSize());
        }

        matrices.pop();
    }

    private void renderTexture(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float zOffset, CustomSignBlockEntity.TextLineData lineData, Identifier textureId, boolean editing, int gizmoMode, Matrix4f baseFrame) {
        matrices.push();

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

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(textureId));
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        org.joml.Vector3f normalVec = matrices.peek().getNormalMatrix().transform(new org.joml.Vector3f(0, 0, 1));

        consumer.vertex(positionMatrix, -halfWidth, -halfHeight, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfWidth, -halfHeight, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfWidth, halfHeight, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, -halfWidth, halfHeight, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();

        if (editing) renderEditingOutline(matrices, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 1f, 1f, 1f);
        TextGizmo.addLineRect(matrices.peek().getPositionMatrix(), gizmoLineIndex, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 1f, 1f);
        if (editing && gizmoMode >= 0) {
            TextGizmo.updateAndRender(gizmoMode, baseFrame, new Matrix4f(matrices.peek().getPositionMatrix()), lineData,
                    zOffset, imageSize / 2f, imageSize / 2f);
        }

        matrices.pop();
    }

    // 在当前矩阵局部坐标系内绘制一个包住 (left,top)-(left+w,top+h) 区域的绿色描边矩形框，用于标注正在编辑的文本行
    // unitScale：局部坐标 1 单位对应的方块尺寸（1 像素 = 1/16 方块）；scaleX/scaleY 为矩阵所含缩放，
    // 用于抵消厚度被放大，保证描边在世界空间中恒为 0.25px 粗
    private void renderEditingOutline(MatrixStack matrices, float left, float top, float w, float h, float unitScale, float scaleX, float scaleY) {
        float px = 0.25f / 16f / Math.max(1e-5f, unitScale);
        float thicknessX = px / Math.max(0.01f, scaleX);
        float thicknessY = px / Math.max(0.01f, scaleY);
        float marginX = thicknessX;
        float marginY = thicknessY;
        float x0 = left - marginX, y0 = top - marginY, x1 = left + w + marginX, y1 = top + h + marginY;

        int r = 0, g = 255, b = 255, a = 255;
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        addOutlineBar(buffer, matrix, x0, y0, x1, y0 + thicknessY, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y1 - thicknessY, x1, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y0, x0 + thicknessX, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x1 - thicknessX, y0, x1, y1, r, g, b, a);
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
    }

    private void addOutlineBar(BufferBuilder buffer, Matrix4f matrix, float x0, float y0, float x1, float y1, int r, int g, int b, int a) {
        buffer.vertex(matrix, x0, y0, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, x1, y0, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, x0, y0, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, x1, y1, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, x0, y1, 0).color(r, g, b, a).next();
    }

    @Override
    public boolean rendersOutsideBoundingBox(T blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}
