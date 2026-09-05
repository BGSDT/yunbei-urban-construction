package com.beigu.yunbeiuc.render.base;

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
            case POLE_L -> -0.75f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.46f;
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
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        consumer.vertex(matrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, -halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();

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
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        consumer.vertex(matrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, -halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(1.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, -halfSize, halfSize, 0).color(255, 255, 255, 255)
                .texture(0.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();

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
}
