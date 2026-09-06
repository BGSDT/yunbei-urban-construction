package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning6;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning6Entity;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignGuideIntersectionAdvanceWarning6EntityRenderer extends BaseSignRenderer<SignGuideIntersectionAdvanceWarning6Entity> {

    private static final Identifier LEFT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_left.png");
    private static final Identifier LEFT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_left_turn.png");
    private static final Identifier STRAIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_straight.png");
    private static final Identifier RIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_right.png");
    private static final Identifier RIGHT_TURN = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_right_turn.png");

    public SignGuideIntersectionAdvanceWarning6EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

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

        SignType signType = SignTypeConverter.convert(type);

        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6.get()){
            renderDirectionLogo1(matrices, vertexConsumers, light, overlay, facing, direction1, -13f, 6f, signType);
            renderDirectionLogo1(matrices, vertexConsumers, light, overlay, facing, direction2, -13f, -6f, signType);
            renderTextWithDirectionAdjustment(matrices, vertexConsumers, light, facing, text1, signType, 6f, 6f, direction1);
            renderTextWithDirectionAdjustment(matrices, vertexConsumers, light, facing, text2, signType, 6f, -6f, direction2);
        }else{
            renderDirectionLogo2(matrices, vertexConsumers, light, overlay, facing, direction1, -9f, -3f, signType);
            renderDirectionLogo2(matrices, vertexConsumers, light, overlay, facing, direction2, 9f, -3f, signType);
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -9f, 6f, 0.035f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, text2, signType, 9f, 6f, 0.035f, 0xFFFFFF);
        }
    }

    private void renderDirectionLogo1(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideIntersectionAdvanceWarning6Entity.Direction direction, float andX, float andY, SignType type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT;
            case STRAIGHT -> STRAIGHT;
            case RIGHT -> RIGHT;
        };

        float adjustedX = (texture == RIGHT) ? -andX : andX;
        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, adjustedX, andY, 0.4f);
    }

    private void renderDirectionLogo2(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideIntersectionAdvanceWarning6Entity.Direction direction, float andX, float andY, SignType type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT_TURN;
            case STRAIGHT -> STRAIGHT;
            case RIGHT -> RIGHT_TURN;
        };

        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, andX, andY, 0.4f);
    }

    private void renderTextWithDirectionAdjustment(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignType type, float andX, float andY, SignGuideIntersectionAdvanceWarning6Entity.Direction direction) {
        float adjustedX = (direction == SignGuideIntersectionAdvanceWarning6Entity.Direction.RIGHT) ? -andX : andX;
        renderCenteredText(matrices, vertexConsumers, light, facing, text, type, adjustedX, andY, 0.035f, 0xFFFFFF);
    }
}
