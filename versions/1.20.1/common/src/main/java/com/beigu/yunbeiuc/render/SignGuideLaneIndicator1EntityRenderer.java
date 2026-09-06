package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideLaneIndicator1;
import com.beigu.yunbeiuc.entity.SignGuideLaneIndicator1Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignGuideLaneIndicator1EntityRenderer extends BaseSignRenderer<SignGuideLaneIndicator1Entity> {

    private static final Identifier ARROW_LEFT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_left_turn.png");
    private static final Identifier ARROW_STRAIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_straight.png");
    private static final Identifier ARROW_RIGHT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_right_turn.png");
    private static final Identifier ARROW_STRAIGHT_LEFT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_straight_left_turn.png");
    private static final Identifier ARROW_STRAIGHT_RIGHT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_straight_right_turn.png");
    private static final Identifier ARROW_LEFT_TURN_AROUND = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_lane_arrow_straight_left_turn_around.png");

    public SignGuideLaneIndicator1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignGuideLaneIndicator1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideLaneIndicator1Entity.Direction direction1 = entity.getDirection1();
        SignGuideLaneIndicator1Entity.Direction direction2 = entity.getDirection2();
        SignGuideLaneIndicator1Entity.Direction direction3 = entity.getDirection3();
        SignGuideLaneIndicator1Entity.Direction direction4 = entity.getDirection4();

        Direction facing = entity.getCachedState().get(SignGuideLaneIndicator1.FACING);
        SignGuideLaneIndicator1.Type type = entity.getCachedState().get(SignGuideLaneIndicator1.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        renderArrow(matrices, vertexConsumers, light, overlay, facing, direction1, -17.5f, 3f, signType);
        renderArrow(matrices, vertexConsumers, light, overlay, facing, direction2, -6f, 3f, signType);
        renderArrow(matrices, vertexConsumers, light, overlay, facing, direction3, 6f, 3f, signType);
        renderArrow(matrices, vertexConsumers, light, overlay, facing, direction4, 17.5f, 3f, signType);
    }

    private void renderArrow(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideLaneIndicator1Entity.Direction direction, float andX, float andY, SignType type) {
        Identifier texture = switch (direction) {
            case LEFT_TURN -> ARROW_LEFT_TURN;
            case STRAIGHT -> ARROW_STRAIGHT;
            case RIGHT_TURN -> ARROW_RIGHT_TURN;
            case STRAIGHT_LEFT_TURN -> ARROW_STRAIGHT_LEFT_TURN;
            case STRAIGHT_RIGHT_TURN -> ARROW_STRAIGHT_RIGHT_TURN;
            case LEFT_TURN_AROUND -> ARROW_LEFT_TURN_AROUND;
        };

        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, andX, andY, 0.9f);
    }
}