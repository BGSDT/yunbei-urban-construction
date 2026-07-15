package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideLaneIndicator1;
import com.beigu.yunbeiuc.entity.SignGuideLaneIndicator1Entity;
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

public class SignGuideLaneIndicator1EntityRenderer implements BlockEntityRenderer<SignGuideLaneIndicator1Entity> {
    private final TextRenderer textRenderer;

    public SignGuideLaneIndicator1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    private static final Identifier ARROW_LEFT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_left_turn.png");
    private static final Identifier ARROW_STRAIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_straight.png");
    private static final Identifier ARROW_RIGHT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_right_turn.png");
    private static final Identifier ARROW_STRAIGHT_LEFT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_straight_left_turn.png");
    private static final Identifier ARROW_STRAIGHT_RIGHT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_straight_right_turn.png");
    private static final Identifier ARROW_LEFT_TURN_AROUND = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_straight_left_turn_around.png");

    @Override
    public void render(SignGuideLaneIndicator1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideLaneIndicator1Entity.Direction direction1 = entity.getDirection1();
        SignGuideLaneIndicator1Entity.Direction direction2 = entity.getDirection2();
        SignGuideLaneIndicator1Entity.Direction direction3 = entity.getDirection3();
        SignGuideLaneIndicator1Entity.Direction direction4 = entity.getDirection4();

        Direction facing = entity.getCachedState().get(SignGuideLaneIndicator1.FACING);
        SignGuideLaneIndicator1.Type type = entity.getCachedState().get(SignGuideLaneIndicator1.TYPE);

        renderArrow(matrices, vertexConsumers, light, overlay, facing, direction1, -17.5f, 3f, type);
        renderArrow(matrices, vertexConsumers, light, overlay, facing, direction2, -6f, 3f, type);
        renderArrow(matrices, vertexConsumers, light, overlay, facing, direction3, 6f, 3f, type);
        renderArrow(matrices, vertexConsumers, light, overlay, facing, direction4, 17.5f, 3f, type);
    }

    private void renderArrow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideLaneIndicator1Entity.Direction direction, float andX, float andY, SignGuideLaneIndicator1.Type type) {
        Identifier texture = switch (direction) {
            case LEFT_TURN -> ARROW_LEFT_TURN;
            case STRAIGHT -> ARROW_STRAIGHT;
            case RIGHT_TURN -> ARROW_RIGHT_TURN;
            case STRAIGHT_LEFT_TURN -> ARROW_STRAIGHT_LEFT_TURN;
            case STRAIGHT_RIGHT_TURN -> ARROW_STRAIGHT_RIGHT_TURN;
            case LEFT_TURN_AROUND -> ARROW_LEFT_TURN_AROUND;
        };

        matrices.push();

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float arrowSize = 0.9f;
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

    @Override
    public boolean rendersOutsideBoundingBox(SignGuideLaneIndicator1Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}