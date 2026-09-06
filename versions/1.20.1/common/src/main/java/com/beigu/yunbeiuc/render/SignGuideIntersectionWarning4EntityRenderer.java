package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning4;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

import java.util.Map;

public class SignGuideIntersectionWarning4EntityRenderer extends BaseSignRenderer<SignGuideIntersectionWarning4Entity> {
    private static final Identifier LEFT1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_left.png");
    private static final Identifier STRAIGHT1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_straight.png");
    private static final Identifier RIGHT1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_right.png");
    private static final Identifier LEFT2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_intersection_warning_5_left.png");
    private static final Identifier STRAIGHT2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_intersection_warning_5_straight.png");
    private static final Identifier RIGHT2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_guide_intersection_warning_5_right.png");

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

    public SignGuideIntersectionWarning4EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignGuideIntersectionWarning4Entity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideIntersectionWarning4Entity.Direction direction1 = entity.getDirection1();
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionWarning4.FACING);
        SignGuideIntersectionWarning4.Type type = entity.getCachedState().get(SignGuideIntersectionWarning4.TYPE);
        SignType signType = convertToSignType(type);

        if (currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_4.get()) {
            renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction1, -13f, 0f, signType);
            renderText1(matrices, vertexConsumers, light, facing, text1, signType, 4f, 0f, direction1);
        } else {
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 0f, 0.035f, 0xFFFFFF);
            renderBackgroundLogo(matrices, vertexConsumers, light, overlay, facing, direction1, 0f, 0f, signType);
            renderDirectionText(matrices, vertexConsumers, light, facing, "cnLeft", signType, true, true);
            renderDirectionText(matrices, vertexConsumers, light, facing, "cnRight", signType, false, true);
            renderDirectionText(matrices, vertexConsumers, light, facing, "enLeft", signType, true, false);
            renderDirectionText(matrices, vertexConsumers, light, facing, "enRight", signType, false, false);
        }
    }

    private SignType convertToSignType(SignGuideIntersectionWarning4.Type type) {
        return switch (type) {
            case POLE_L -> SignType.POLE_L;
            case POLE_H -> SignType.POLE_H;
            case NORMAL -> SignType.NORMAL;
        };
    }

    private void renderDirectionLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay,
                                     Direction facing, SignGuideIntersectionWarning4Entity.Direction direction,
                                     float andX, float andY, SignType type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT1;
            case STRAIGHT -> STRAIGHT1;
            case RIGHT -> RIGHT1;
        };

        float x = andX;
        if (texture == RIGHT1) x = -x;

        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, x, andY, 0.4f);
    }

    private void renderBackgroundLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay,
                                      Direction facing, SignGuideIntersectionWarning4Entity.Direction direction,
                                      float andX, float andY, SignType type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT2;
            case STRAIGHT -> STRAIGHT2;
            case RIGHT -> RIGHT2;
        };

        float x = andX;
        if (texture == RIGHT1) x = -x;

        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, x, andY, 1.75f);
    }

    private void renderDirectionText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                                     Direction facing, String directionKey, SignType type, boolean leftTF, boolean cnTF) {
        String directionText = DIRECTION_MAP.get(facing).get(directionKey);
        float x = leftTF ? 16f : -16f;
        float y = cnTF ? 4f : -4f;

        renderCenteredText(matrices, vertexConsumers, light, facing, directionText, type, x, y, 0.02f, 0xFFFFFF);
    }

    private void renderText1(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
                            Direction facing, String text, SignType type, float andX, float andY,
                            SignGuideIntersectionWarning4Entity.Direction direction) {
        float x = (direction == SignGuideIntersectionWarning4Entity.Direction.RIGHT) ? -andX : andX;
        renderCenteredText(matrices, vertexConsumers, light, facing, text, type, x, andY, 0.04f, 0xFFFFFF);
    }
}
