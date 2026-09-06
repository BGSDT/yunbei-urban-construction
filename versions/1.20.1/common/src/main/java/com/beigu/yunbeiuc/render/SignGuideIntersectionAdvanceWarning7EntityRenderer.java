package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning7;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning7Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignGuideIntersectionAdvanceWarning7EntityRenderer extends BaseSignRenderer<SignGuideIntersectionAdvanceWarning7Entity> {

    private static final Identifier LEFT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_left.png");
    private static final Identifier STRAIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_straight.png");
    private static final Identifier RIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_right.png");

    public SignGuideIntersectionAdvanceWarning7EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignGuideIntersectionAdvanceWarning7Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideIntersectionAdvanceWarning7Entity.Direction direction1 = entity.getDirection1();
        SignGuideIntersectionAdvanceWarning7Entity.Direction direction2 = entity.getDirection2();
        SignGuideIntersectionAdvanceWarning7Entity.Direction direction3 = entity.getDirection3();
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning7.FACING);
        SignGuideIntersectionAdvanceWarning7.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning7.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction1, -13f, 12f, signType);
        renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction2, -13f, 0f, signType);
        renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction3, -13f, -12f, signType);
        renderTextWithDirectionAdjustment(matrices, vertexConsumers, light, facing, text1, signType, 6f, 12f, direction1);
        renderTextWithDirectionAdjustment(matrices, vertexConsumers, light, facing, text2, signType, 6f, 0f, direction2);
        renderTextWithDirectionAdjustment(matrices, vertexConsumers, light, facing, text3, signType, 6f, -12f, direction3);
    }

    private void renderDirectionLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideIntersectionAdvanceWarning7Entity.Direction direction, float andX, float andY, SignType type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT;
            case STRAIGHT -> STRAIGHT;
            case RIGHT -> RIGHT;
        };

        float adjustedX = (texture == RIGHT) ? -andX : andX;
        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, adjustedX, andY, 0.4f);
    }

    private void renderTextWithDirectionAdjustment(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignType type, float andX, float andY, SignGuideIntersectionAdvanceWarning7Entity.Direction direction) {
        float adjustedX = (direction == SignGuideIntersectionAdvanceWarning7Entity.Direction.RIGHT) ? -andX : andX;
        renderCenteredText(matrices, vertexConsumers, light, facing, text, type, adjustedX, andY, 0.035f, 0xFFFFFF);
    }
}
