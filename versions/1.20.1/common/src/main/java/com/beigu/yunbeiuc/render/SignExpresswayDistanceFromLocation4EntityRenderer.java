package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation4;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation4Entity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class SignExpresswayDistanceFromLocation4EntityRenderer implements BlockEntityRenderer<SignExpresswayDistanceFromLocation4Entity> {
    private final TextRenderer textRenderer;

    public SignExpresswayDistanceFromLocation4EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    private static final Identifier ORDINARY_MUNICIPAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_ordinary_municipal_road_logo_1.png");
    private static final Identifier ORDINARY_MUNICIPAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_ordinary_municipal_road_logo_2.png");

    @Override
    public void render(SignExpresswayDistanceFromLocation4Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();
        SignExpresswayDistanceFromLocation4Entity.RoadType roadType1 = entity.getRoadType1();
        SignExpresswayDistanceFromLocation4Entity.RoadType roadType2 = entity.getRoadType2();
        SignExpresswayDistanceFromLocation4Entity.RoadType roadType3 = entity.getRoadType3();
        String length1 = entity.getLength1();
        String length2 = entity.getLength2();
        String length3 = entity.getLength3();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";
        if (length1 == null || length1.isEmpty()) length1 = " ";
        if (length2 == null || length2.isEmpty()) length2 = " ";
        if (length3 == null || length3.isEmpty()) length3 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayDistanceFromLocation4.FACING);
        SignExpresswayDistanceFromLocation4.Type type = entity.getCachedState().get(SignExpresswayDistanceFromLocation4.TYPE);

        renderLogo(matrices, vertexConsumers, light, overlay, facing, roadType1, -8f, 12f, type, text1);
        renderLogo(matrices, vertexConsumers, light, overlay, facing, roadType2, -8f, 0f, type, text2);
        renderLogo(matrices, vertexConsumers, light, overlay, facing, roadType3, -8f, -12f, type, text3);
        renderLeftText(matrices, vertexConsumers, light, facing, text1, type, -17f, 12f, roadType1);
        renderLeftText(matrices, vertexConsumers, light, facing, text2, type, -17f, 0f, roadType2);
        renderLeftText(matrices, vertexConsumers, light, facing, text3, type, -17f, -12f, roadType3);
        renderRightText(matrices, vertexConsumers, light, facing, length1, type, 13f, 12f, false);
        renderRightText(matrices, vertexConsumers, light, facing, length2, type, 13f, 0f, false);
        renderRightText(matrices, vertexConsumers, light, facing, length3, type, 13f, -12f, false);
        renderRightText(matrices, vertexConsumers, light, facing, "km", type, 17f, 11.5f, true);
        renderRightText(matrices, vertexConsumers, light, facing, "km", type, 17f, -0.5f, true);
        renderRightText(matrices, vertexConsumers, light, facing, "km", type, 17f, -12.5f, true);
    }

    private void renderLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayDistanceFromLocation4Entity.RoadType roadType, float andX, float andY, SignExpresswayDistanceFromLocation4.Type type, String text) {
        Identifier texture = switch (roadType) {
            case EXPRESSWAY -> null;
            case ORDINARY_MUNICIPAL -> {
                if (text == null || text.trim().isEmpty()) {
                    yield ORDINARY_MUNICIPAL_1;
                } else {
                    if (text.length() <= 3) {
                        yield ORDINARY_MUNICIPAL_1;
                    } else {
                        yield ORDINARY_MUNICIPAL_2;
                    }
                }
            }
        };

        if (texture == null) return;

        matrices.push();

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float arrowSize = 1.55f;
        float halfSize = arrowSize / 2f;

        float x = andX / 16f;
        if (texture == ORDINARY_MUNICIPAL_2) x = x + 3f / 16f;
        float y = andY / 16f;

        matrices.translate(x, y, zOffset);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        consumer.vertex(matrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, -halfSize, halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();

        matrices.pop();
    }

    private void renderLeftText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignExpresswayDistanceFromLocation4.Type type, float andX, float andY, SignExpresswayDistanceFromLocation4Entity.RoadType roadType) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.04f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true).withFont(new Identifier("minecraft", "uniform")));
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (roadType){
            case EXPRESSWAY -> switch (type) {
                case POLE_L -> -0.75f;
                case POLE_H -> -0.79f;
                case NORMAL -> -0.43f;
            };
            case ORDINARY_MUNICIPAL -> switch (type) {
                case POLE_L -> -0.74f;
                case POLE_H -> -0.78f;
                case NORMAL -> -0.42f;
            };
        };

        float centeredX = andX / 16f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                0XFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    private void renderRightText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignExpresswayDistanceFromLocation4.Type type, float andX, float andY, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = isSmallScale ? 0.025f : 0.04f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true).withFont(new Identifier("minecraft", "uniform")));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        float centeredX = andX / 16f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                -textWidth,
                -textHeight / 2.0f,
                0XFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(SignExpresswayDistanceFromLocation4Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}