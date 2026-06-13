package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning6;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning6Entity;
import net.minecraft.block.Block;
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

public class SignGuideIntersectionAdvanceWarning6EntityRenderer implements BlockEntityRenderer<SignGuideIntersectionAdvanceWarning6Entity> {
    private final TextRenderer textRenderer;

    public SignGuideIntersectionAdvanceWarning6EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    private static final Identifier LEFT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_left.png");
    private static final Identifier LEFT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_left_turn.png");
    private static final Identifier STRAIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_straight.png");
    private static final Identifier RIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_right.png");
    private static final Identifier RIGHT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_right_turn.png");

    @Override
    public void render(SignGuideIntersectionAdvanceWarning6Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideIntersectionAdvanceWarning6Entity.Direction direction1 = entity.getDirection1();
        SignGuideIntersectionAdvanceWarning6Entity.Direction direction2 = entity.getDirection2();
        String text1 = entity.getText1();
        String text2 = entity.getText2();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning6.FACING);
        SignGuideIntersectionAdvanceWarning6.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning6.TYPE);
        Block currentBlock = entity.getCachedState().getBlock();

        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6){
            renderDirectionLogo1(matrices, vertexConsumers, light, overlay, facing, direction1, -13f, 6f, type);
            renderDirectionLogo1(matrices, vertexConsumers, light, overlay, facing, direction2, -13f, -6f, type);
            renderText1(matrices, vertexConsumers, light, facing, text1, type, 6f, 6f, direction1);
            renderText1(matrices, vertexConsumers, light, facing, text2, type, 6f, -6f, direction2);
        }else{
            renderDirectionLogo2(matrices, vertexConsumers, light, overlay, facing, direction1, -9f, -3f, type);
            renderDirectionLogo2(matrices, vertexConsumers, light, overlay, facing, direction2, 9f, -3f, type);
            renderText2(matrices, vertexConsumers, light, facing, text1, type, -9f, 6f, direction1);
            renderText2(matrices, vertexConsumers, light, facing, text2, type, 9f, 6f, direction2);
        }

    }

    private void renderDirectionLogo1(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideIntersectionAdvanceWarning6Entity.Direction direction, float andX, float andY, SignGuideIntersectionAdvanceWarning6.Type type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT;
            case STRAIGHT -> STRAIGHT;
            case RIGHT -> RIGHT;
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
        if(texture == RIGHT) x = -x;
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

    private void renderDirectionLogo2(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideIntersectionAdvanceWarning6Entity.Direction direction, float andX, float andY, SignGuideIntersectionAdvanceWarning6.Type type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT_TURN;
            case STRAIGHT -> STRAIGHT;
            case RIGHT -> RIGHT_TURN;
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

    private void renderText1(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionAdvanceWarning6.Type type, float andX, float andY, SignGuideIntersectionAdvanceWarning6Entity.Direction direction) {
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

        if(direction == SignGuideIntersectionAdvanceWarning6Entity.Direction.RIGHT) andX = -andX;
        float centeredX = andX / 16f - (textWidth * scaleValue) / 2f;
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

    private void renderText2(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionAdvanceWarning6.Type type, float andX, float andY, SignGuideIntersectionAdvanceWarning6Entity.Direction direction) {
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
    public boolean rendersOutsideBoundingBox(SignGuideIntersectionAdvanceWarning6Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}