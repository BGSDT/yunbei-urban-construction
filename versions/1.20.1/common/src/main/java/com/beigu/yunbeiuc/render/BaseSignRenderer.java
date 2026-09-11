package com.beigu.yunbeiuc.render;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

/**
 * 标志牌渲染器基类
 * 提供通用的文本和图片渲染方法
 */
public abstract class BaseSignRenderer<T extends BlockEntity> implements BlockEntityRenderer<T> {
    protected final TextRenderer textRenderer;

    public BaseSignRenderer(TextRenderer textRenderer) {
        this.textRenderer = textRenderer;
    }

    @Override
    public boolean rendersOutsideBoundingBox(T blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }

    /**
     * 根据标志牌类型获取 Z 轴偏移量
     */
    protected float getZOffset(SignType type) {
        return switch (type) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }

    /**
     * 渲染居中文本
     */
    protected void renderCenteredText(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                      int light, Direction facing, String text, SignType type,
                                      float andX, float andY, float scale, int color) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true)
                .withFont(new Identifier("minecraft", "uniform")));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = getZOffset(type);
        float centeredX = andX / 16f - (textWidth * scale) / 2f;
        float centeredY = andY / 16f;

        matrices.translate(centeredX, centeredY, zOffset);
        matrices.scale(scale, -scale, scale);

        this.textRenderer.draw(styledText, 0, -textHeight / 2.0f, color, false,
                matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }

    /**
     * 渲染左对齐文本
     */
    protected void renderLeftAlignedText(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                         int light, Direction facing, String text, SignType type,
                                         float andX, float andY, float scale, int color) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true)
                .withFont(new Identifier("minecraft", "uniform")));
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = getZOffset(type);
        float leftX = andX / 16f;
        float centeredY = andY / 16f;

        matrices.translate(leftX, centeredY, zOffset);
        matrices.scale(scale, -scale, scale);

        this.textRenderer.draw(styledText, 0, -textHeight / 2.0f, color, false,
                matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }

    /**
     * 渲染右对齐文本
     */
    protected void renderRightAlignedText(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                          int light, Direction facing, String text, SignType type,
                                          float andX, float andY, float scale, int color) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true)
                .withFont(new Identifier("minecraft", "uniform")));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = getZOffset(type);
        float rightX = andX / 16f;
        float centeredY = andY / 16f;

        matrices.translate(rightX, centeredY, zOffset);
        matrices.scale(scale, -scale, scale);

        this.textRenderer.draw(styledText, -textWidth, -textHeight / 2.0f, color, false,
                matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }

    /**
     * 渲染自定义 zOffset 的文本（用于特殊情况）
     */
    protected void renderTextWithCustomZ(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                         int light, Direction facing, String text,
                                         float andX, float andY, float zOffset, float scale,
                                         int color, TextAlignment alignment) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true)
                .withFont(new Identifier("minecraft", "uniform")));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float xPos = switch (alignment) {
            case CENTER -> andX / 16f - (textWidth * scale) / 2f;
            case LEFT -> andX / 16f;
            case RIGHT -> andX / 16f - textWidth * scale;
        };
        float yPos = andY / 16f;

        matrices.translate(xPos, yPos, zOffset);
        matrices.scale(scale, -scale, scale);

        float renderX = alignment == TextAlignment.RIGHT ? textWidth : 0;
        this.textRenderer.draw(styledText, alignment == TextAlignment.RIGHT ? -textWidth : 0,
                -textHeight / 2.0f, color, false,
                matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }

    /**
     * 渲染图片/纹理
     */
    protected void renderTexture(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                 int light, int overlay, Direction facing, Identifier texture,
                                 SignType type, float andX, float andY, float size) {
        matrices.push();

        float zOffset = getZOffset(type);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float halfSize = size / 2f;
        float x = andX / 16f;
        float y = andY / 16f;

        matrices.translate(x, y, zOffset);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        org.joml.Vector3f normalVec = matrices.peek().getNormalMatrix().transform(new org.joml.Vector3f(0, 0, 1));

        consumer.vertex(positionMatrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, -halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();

        matrices.pop();
    }

    /**
     * 渲染自定义 zOffset 的纹理
     */
    protected void renderTextureWithCustomZ(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                           int light, int overlay, Direction facing, Identifier texture,
                                           float andX, float andY, float zOffset, float size) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float halfSize = size / 2f;
        float x = andX / 16f;
        float y = andY / 16f;

        matrices.translate(x, y, zOffset);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        org.joml.Vector3f normalVec = matrices.peek().getNormalMatrix().transform(new org.joml.Vector3f(0, 0, 1));

        consumer.vertex(positionMatrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, -halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();

        matrices.pop();
    }

    /**
     * 标志牌类型枚举（与各个实体的 Type 枚举对应）
     */
    public enum SignType {
        POLE_L,
        POLE_H,
        NORMAL
    }

    /**
     * 文本对齐方式
     */
    public enum TextAlignment {
        LEFT,
        CENTER,
        RIGHT
    }

    /**
     * 渲染高速公路Logo(支持居中/左对齐)
     * @param zOffsetDelta 相对于默认zOffset的额外偏移(负值=向前,正值=向后)
     */
    protected void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                        int light, int overlay, Direction facing, Identifier logoTexture,
                                        SignType type, float andX, float andY, float size,
                                        float zOffsetDelta, TextAlignment alignment) {
        matrices.push();

        float zOffset = getZOffset(type) + zOffsetDelta;
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float halfSize = size / 2f;
        float x = andX / 16f;
        float y = andY / 16f;

        // 根据对齐方式调整X坐标
        if (alignment == TextAlignment.CENTER) {
            x = x - halfSize; // 居中时减去半宽
        } else if (alignment == TextAlignment.LEFT) {
            // 左对齐时andX已经是左边缘
        }

        matrices.translate(x, y, zOffset);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(logoTexture));
        Matrix4f positionMatrix = matrices.peek().getPositionMatrix();
        org.joml.Vector3f normalVec = matrices.peek().getNormalMatrix().transform(new org.joml.Vector3f(0, 0, 1));

        consumer.vertex(positionMatrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 1.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();
        consumer.vertex(positionMatrix, -halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 0.0f).overlay(overlay).light(light).normal(normalVec.x, normalVec.y, normalVec.z).next();

        matrices.pop();
    }

    /**
     * 渲染高速文本(支持居中/左对齐/右对齐,自动处理单/双位数字偏移)
     * @param zOffsetDelta 相对于默认zOffset的额外偏移(负值=向前,正值=向后)
     */
    protected void renderExpresswayText(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                        int light, Direction facing, String text, SignType type,
                                        float andX, float andY, float scale, int color,
                                        float zOffsetDelta, TextAlignment alignment) {
        // 自动处理单位数字时的X偏移(单位数字的logo更窄,需要向右调整)
        String digits = text.replaceAll("[^0-9]", "");
        float adjustedX = andX;
        if (alignment == TextAlignment.CENTER &&
            (text.trim().isEmpty() || !text.matches(".*\\d.*") || digits.length() == 1)) {
            adjustedX = andX + 1f;
        }

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true)
                .withFont(new Identifier("minecraft", "uniform")));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = getZOffset(type) + zOffsetDelta;
        float yPos = andY / 16f;

        float xPos = switch (alignment) {
            case CENTER -> adjustedX / 16f - (textWidth * scale) / 2f;
            case LEFT -> adjustedX / 16f;
            case RIGHT -> adjustedX / 16f - textWidth * scale;
        };

        matrices.translate(xPos, yPos, zOffset);
        matrices.scale(scale, -scale, scale);

        float renderX = alignment == TextAlignment.RIGHT ? -textWidth : 0;
        this.textRenderer.draw(styledText, renderX, -textHeight / 2.0f, color, false,
                matrices.peek().getPositionMatrix(), vertexConsumers,
                TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }
}
