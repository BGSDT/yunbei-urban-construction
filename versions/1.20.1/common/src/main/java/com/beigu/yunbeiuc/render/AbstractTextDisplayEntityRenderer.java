package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
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

    protected AbstractTextDisplayEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public final void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        applyTransforms(matrices, entity);
        float zOffset = getZOffset(entity);
        int effectiveLight = entity.isGlowingText() ? LightmapTextureManager.MAX_LIGHT_COORDINATE : light;

        List<CustomSignBlockEntity.TextLineData> lines = entity.getTextLines();
        int editingIndex = entity.getEditingLineIndex();
        for (int i = 0; i < lines.size(); i++) {
            renderTextLine(matrices, vertexConsumers, effectiveLight, overlay, zOffset, lines.get(i), i == editingIndex);
        }

        matrices.pop();
    }

    // 子类实现前置变换（平移/旋转）
    protected abstract void applyTransforms(MatrixStack matrices, T entity);

    // 子类实现 Z 轴偏移
    protected abstract float getZOffset(T entity);

    private void renderTextLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float zOffset, CustomSignBlockEntity.TextLineData lineData, boolean editing) {
        if (lineData.getText().isEmpty()) return;
        String text = lineData.getText().trim();

        if (text.startsWith("-rect")) {
            String[] parts = text.split("\\s+");
            if (parts.length >= 3) {
                try {
                    float w = Float.parseFloat(parts[1]);
                    float h = Float.parseFloat(parts[2]);
                    renderRect(matrices, zOffset, lineData, w, h, editing);
                    return;
                } catch (NumberFormatException ignored) {}
            }
        }

        if (text.startsWith("-texture")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length >= 2) {
                // Identifier 仅允许小写字符，大写字母会导致 tryParse 失败而回退为文本渲染，这里统一转小写
                Identifier textureId = Identifier.tryParse(parts[1].trim().toLowerCase(Locale.ROOT));
                if (textureId != null) {
                    renderTexture(matrices, vertexConsumers, light, overlay, zOffset, lineData, textureId, editing);
                    return;
                }
            }
        }

        if (text.startsWith("-json")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length >= 2) {
                try {
                    Text jsonText = Text.Serializer.fromLenientJson(parts[1]);
                    if (jsonText != null) {
                        renderJsonText(matrices, vertexConsumers, light, zOffset, lineData, jsonText, editing);
                        return;
                    }
                } catch (Exception ignored) {}
            }
        }

        renderText(matrices, vertexConsumers, light, zOffset, lineData, editing);
    }

    private void applyRotation(MatrixStack matrices, CustomSignBlockEntity.TextLineData lineData) {
        if (lineData.getRotX() != 0) matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(lineData.getRotX()));
        if (lineData.getRotY() != 0) matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(lineData.getRotY()));
        if (lineData.getRotZ() != 0) matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(lineData.getRotZ()));
    }

    private void renderJsonText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float zOffset, CustomSignBlockEntity.TextLineData lineData, Text jsonText, boolean editing) {
        matrices.push();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);
        applyRotation(matrices, lineData);
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

        this.textRenderer.draw(renderText, renderX, renderY, lineData.getColor(), lineData.isShadow(),
                matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

        if (editing) renderEditingOutline(matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize());

        matrices.pop();
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, float zOffset, CustomSignBlockEntity.TextLineData lineData, boolean editing) {
        matrices.push();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f + zOffset;

        matrices.translate(xPos, yPos, zPos);
        applyRotation(matrices, lineData);
        matrices.scale(baseScale * lineData.getScaleX(), -baseScale * lineData.getScaleY(), baseScale * lineData.getScaleZ());

        Style style = Style.EMPTY
                .withBold(lineData.isBold())
                .withItalic(lineData.isItalic())
                .withUnderline(lineData.isUnderline())
                .withFont(new Identifier("minecraft", "uniform"));

        Text renderText = Text.literal(lineData.getText()).setStyle(style);
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

        this.textRenderer.draw(renderText, renderX, renderY, lineData.getColor(), lineData.isShadow(),
                matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

        if (editing) renderEditingOutline(matrices, renderX, renderY, textWidth, textHeight, 0.05f * lineData.getFontSize());

        matrices.pop();
    }

    private void renderRect(MatrixStack matrices, float zOffset, CustomSignBlockEntity.TextLineData lineData, float width, float height, boolean editing) {
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

        if (editing) renderEditingOutline(matrices, -halfW, -halfH, halfW * 2, halfH * 2, 1f);

        matrices.pop();
    }

    private void renderTexture(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, float zOffset, CustomSignBlockEntity.TextLineData lineData, Identifier textureId, boolean editing) {
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
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        consumer.vertex(matrix, -halfWidth, -halfHeight, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfWidth, -halfHeight, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfWidth, halfHeight, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, -halfWidth, halfHeight, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();

        if (editing) renderEditingOutline(matrices, -halfWidth, -halfHeight, halfWidth * 2, halfHeight * 2, 1f);

        matrices.pop();
    }

    // 在当前矩阵局部坐标系内绘制一个包住 (left,top)-(left+w,top+h) 区域的绿色描边矩形框，用于标注正在编辑的文本行
    // unitScale：局部坐标 1 单位对应的方块尺寸（1 像素 = 1/16 方块），保证描边在世界空间中恒为 0.25px 粗
    private void renderEditingOutline(MatrixStack matrices, float left, float top, float w, float h, float unitScale) {
        float px = 0.25f / 16f / unitScale;
        float margin = px;
        float thickness = px;
        float x0 = left - margin, y0 = top - margin, x1 = left + w + margin, y1 = top + h + margin;

        int r = 0, g = 255, b = 0, a = 255;
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.disableCull();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        addOutlineBar(buffer, matrix, x0, y0, x1, y0 + thickness, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y1 - thickness, x1, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x0, y0, x0 + thickness, y1, r, g, b, a);
        addOutlineBar(buffer, matrix, x1 - thickness, y0, x1, y1, r, g, b, a);
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
