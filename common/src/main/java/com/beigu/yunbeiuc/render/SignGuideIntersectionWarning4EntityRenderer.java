package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning4;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning4;
import com.beigu.yunbeiuc.entity.*;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import net.minecraft.block.Block;
import net.minecraft.block.SignBlock;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

import java.util.Map;

public class SignGuideIntersectionWarning4EntityRenderer implements BlockEntityRenderer<SignGuideIntersectionWarning4Entity> {
    private final TextRenderer textRenderer;

    private static final Identifier LEFT1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_left.png");
    private static final Identifier STRAIGHT1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_straight.png");
    private static final Identifier RIGHT1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_right.png");
    private static final Identifier LEFT2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_intersection_warning_5_left.png");
    private static final Identifier STRAIGHT2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_intersection_warning_5_straight.png");
    private static final Identifier RIGHT2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_intersection_warning_5_right.png");

    public SignGuideIntersectionWarning4EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    private static final Map<Direction, Map<String, String>> DIRECTION_MAP = Map.of(
            Direction.NORTH, Map.of(
                    "cnLeft", "西", "cnRight", "东",
                    "enLeft", "W", "enRight", "E"
            ),
            Direction.SOUTH, Map.of(
                    "cnLeft", "东", "cnRight", "西",
                    "enLeft", "E", "enRight", "W"
            ),
            Direction.WEST, Map.of(
                    "cnLeft", "南", "cnRight", "北",
                    "enLeft", "S", "enRight", "N"
            ),
            Direction.EAST, Map.of(
                    "cnLeft", "北", "cnRight", "南",
                    "enLeft", "N", "enRight", "S"
            )
    );

    @Override
    public void render(SignGuideIntersectionWarning4Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideIntersectionWarning4Entity.Direction direction1 = entity.getDirection1();
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionWarning4.FACING);
        SignGuideIntersectionWarning4.Type type = entity.getCachedState().get(SignGuideIntersectionWarning4.TYPE);
        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_4.get()){
            renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction1, -13f, 0f, type);
            renderText1(matrices, vertexConsumers, light, facing, text1, type, 4f, 0f, direction1);
        }else{
            renderText2(matrices, vertexConsumers, light, facing, text1, type, 0f, 0f);
            renderBackgroundLogo(matrices, vertexConsumers, light, overlay, facing, direction1, 0f, 0f, type);
            renderDirectionText(matrices, vertexConsumers, light, facing, "cnLeft", type, true, true);
            renderDirectionText(matrices, vertexConsumers, light, facing, "cnRight", type, false, true);
            renderDirectionText(matrices, vertexConsumers, light, facing, "enLeft", type, true, false);
            renderDirectionText(matrices, vertexConsumers, light, facing, "enRight", type, false, false);
        }
    }

    private void renderDirectionLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideIntersectionWarning4Entity.Direction direction, float andX, float andY, SignGuideIntersectionWarning4.Type type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT1;
            case STRAIGHT -> STRAIGHT1;
            case RIGHT -> RIGHT1;
        };

        matrices.push();

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float arrowSize = 0.4f;
        float halfSize = arrowSize / 2f;

        float x = andX / 16f;
        if(texture == RIGHT1) x = -x;
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

    private void renderBackgroundLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideIntersectionWarning4Entity.Direction direction, float andX, float andY, SignGuideIntersectionWarning4.Type type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT2;
            case STRAIGHT -> STRAIGHT2;
            case RIGHT -> RIGHT2;
        };

        matrices.push();

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float arrowSize = 1.75f;
        float halfSize = arrowSize / 2f;

        float x = andX / 16f;
        if(texture == RIGHT1) x = -x;
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

    private void renderText2(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionWarning4.Type type, float andX, float andY) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.035f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        float centeredX = andX / 16f - (textWidth * scaleValue) / 2f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                0xFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    private void renderDirectionText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String directionKey, SignGuideIntersectionWarning4.Type type, boolean leftTF, boolean cnTF) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        String directionText = DIRECTION_MAP.get(facing).get(directionKey);
        Text styledText = Text.literal(directionText).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;
        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        float x = leftTF? 16f : -16f;
        float y = cnTF? 4f : -4f;
        float centeredX = x / 16f - (textWidth * 0.02f) / 2f;
        float centeredY = y / 16f;
        matrices.translate(centeredX, centeredY, zOffset);
        matrices.scale(0.02f, -0.02f, 0.02f);

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

    private void renderText1(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionWarning4.Type type, float andX, float andY, SignGuideIntersectionWarning4Entity.Direction direction) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.04f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        if(direction == SignGuideIntersectionWarning4Entity.Direction.RIGHT) andX = -andX;
        float centeredX = andX / 16f - (textWidth * scaleValue) / 2f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                0xFFFFFF,
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
    public boolean rendersOutsideBoundingBox(SignGuideIntersectionWarning4Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}