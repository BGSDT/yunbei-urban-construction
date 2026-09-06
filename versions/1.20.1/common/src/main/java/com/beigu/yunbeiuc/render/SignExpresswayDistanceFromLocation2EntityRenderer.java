package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation2;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation2Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignExpresswayDistanceFromLocation2EntityRenderer extends BaseSignRenderer<SignExpresswayDistanceFromLocation2Entity> {

    private static final Identifier NATIONAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_1.png");
    private static final Identifier PROVINCIAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_1.png");
    private static final Identifier NATIONAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_2.png");
    private static final Identifier PROVINCIAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_2.png");

    public SignExpresswayDistanceFromLocation2EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayDistanceFromLocation2Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignExpresswayDistanceFromLocation2Entity.Expressway expressway1 = entity.getExpressway1();
        String text1 = entity.getText1();
        String expresswayNumber = entity.getExpresswayNumber();
        String text3 = entity.getText3();
        String length1 = entity.getLength1();
        String length2 = entity.getLength2();
        String length3 = entity.getLength3();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (expresswayNumber == null || expresswayNumber.isEmpty()) expresswayNumber = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";
        if (length1 == null || length1.isEmpty()) length1 = " ";
        if (length2 == null || length2.isEmpty()) length2 = " ";
        if (length3 == null || length3.isEmpty()) length3 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayDistanceFromLocation2.FACING);
        SignExpresswayDistanceFromLocation2.Type type = entity.getCachedState().get(SignExpresswayDistanceFromLocation2.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        renderLeftAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -15f, 11f, 0.04f, 0xFFFFFF);
        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, -11f, 0f, signType, expresswayNumber);
        renderExpresswayText(matrices, vertexConsumers, light, facing, expresswayNumber, signType, -11f, -1f);
        renderLeftAlignedText(matrices, vertexConsumers, light, facing, text3, signType, -15f, -11f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length1, signType, 10f, 11f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length2, signType, 10f, 0f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length3, signType, 10f, -11f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 14f, 10.5f, 0.025f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 14f, -0.5f, 0.025f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 14f, -11.5f, 0.025f, 0xFFFFFF);
    }

    private void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayDistanceFromLocation2Entity.Expressway expressway, float andX, float andY, SignType type, String expresswayNumber) {
        Identifier texture = switch (expressway) {
            case NATIONAL -> {
                if (expresswayNumber == null || expresswayNumber.trim().isEmpty() || !expresswayNumber.matches(".*\\d.*")) {
                    yield NATIONAL_1;
                } else {
                    String digits = expresswayNumber.replaceAll("[^0-9]", "");
                    yield digits.length() == 1 ? NATIONAL_2 : NATIONAL_1;
                }
            }
            case PROVINCIAL -> {
                if (expresswayNumber == null || expresswayNumber.trim().isEmpty() || !expresswayNumber.matches(".*\\d.*")) {
                    yield PROVINCIAL_1;
                } else {
                    String digits = expresswayNumber.replaceAll("[^0-9]", "");
                    yield digits.length() == 1 ? PROVINCIAL_2 : PROVINCIAL_1;
                }
            }
        };

        float adjustedX = andX;
        if (texture == PROVINCIAL_1 || texture == NATIONAL_1) {
            adjustedX = andX + 1f;
        }
        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, adjustedX, andY, 0.65f);
    }

    private void renderExpresswayText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignType type, float andX, float andY) {
        String digits = text.replaceAll("[^0-9]", "");
        float adjustedX = andX;
        if (text.trim().isEmpty() || !text.matches(".*\\d.*") || digits.length() != 1) {
            adjustedX = andX + 1f;
        }

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.80f;
            case NORMAL -> -0.45f;
        };

        renderTextWithCustomZ(matrices, vertexConsumers, light, facing, text, adjustedX, andY, zOffset, 0.045f, 0xFFFFFF, TextAlignment.CENTER);
    }
}