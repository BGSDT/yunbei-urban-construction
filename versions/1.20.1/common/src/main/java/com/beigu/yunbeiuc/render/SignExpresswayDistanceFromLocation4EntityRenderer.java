package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation4;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation4Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignExpresswayDistanceFromLocation4EntityRenderer extends BaseSignRenderer<SignExpresswayDistanceFromLocation4Entity> {

    private static final Identifier ORDINARY_MUNICIPAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_ordinary_municipal_road_logo_1.png");
    private static final Identifier ORDINARY_MUNICIPAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_ordinary_municipal_road_logo_2.png");

    public SignExpresswayDistanceFromLocation4EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

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

        SignType signType = SignTypeConverter.convert(type);

        renderLogo(matrices, vertexConsumers, light, overlay, facing, roadType1, -8f, 12f, signType, text1);
        renderLogo(matrices, vertexConsumers, light, overlay, facing, roadType2, -8f, 0f, signType, text2);
        renderLogo(matrices, vertexConsumers, light, overlay, facing, roadType3, -8f, -12f, signType, text3);
        renderLeftTextWithRoadType(matrices, vertexConsumers, light, facing, text1, signType, -17f, 12f, roadType1);
        renderLeftTextWithRoadType(matrices, vertexConsumers, light, facing, text2, signType, -17f, 0f, roadType2);
        renderLeftTextWithRoadType(matrices, vertexConsumers, light, facing, text3, signType, -17f, -12f, roadType3);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length1, signType, 13f, 12f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length2, signType, 13f, 0f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length3, signType, 13f, -12f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 17f, 11.5f, 0.025f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 17f, -0.5f, 0.025f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 17f, -12.5f, 0.025f, 0xFFFFFF);
    }

    private void renderLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayDistanceFromLocation4Entity.RoadType roadType, float andX, float andY, SignType type, String text) {
        Identifier texture = switch (roadType) {
            case EXPRESSWAY -> null;
            case ORDINARY_MUNICIPAL -> {
                if (text == null || text.trim().isEmpty()) {
                    yield ORDINARY_MUNICIPAL_1;
                } else {
                    yield text.length() <= 3 ? ORDINARY_MUNICIPAL_1 : ORDINARY_MUNICIPAL_2;
                }
            }
        };

        if (texture == null) return;

        float adjustedX = andX;
        if (texture == ORDINARY_MUNICIPAL_2) {
            adjustedX = andX + 3f;
        }
        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, adjustedX, andY, 1.55f);
    }

    private void renderLeftTextWithRoadType(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignType type, float andX, float andY, SignExpresswayDistanceFromLocation4Entity.RoadType roadType) {
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

        renderTextWithCustomZ(matrices, vertexConsumers, light, facing, text, andX, andY, zOffset, 0.04f, 0xFFFFFF, TextAlignment.LEFT);
    }
}