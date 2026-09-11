package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation6;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation6Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignExpresswayDistanceFromLocation6EntityRenderer extends BaseSignRenderer<SignExpresswayDistanceFromLocation6Entity> {

    private static final Identifier NATIONAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_1.png");
    private static final Identifier PROVINCIAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_1.png");
    private static final Identifier NATIONAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_2.png");
    private static final Identifier PROVINCIAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_2.png");

    public SignExpresswayDistanceFromLocation6EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayDistanceFromLocation6Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();
        String length1 = entity.getLength1();
        String length2 = entity.getLength2();
        String length3 = entity.getLength3();
        SignExpresswayDistanceFromLocation6Entity.Expressway expressway1 = entity.getExpressway1();
        SignExpresswayDistanceFromLocation6Entity.Expressway expressway2 = entity.getExpressway2();
        String expresswayNumber1 = entity.getExpresswayNumber1();
        String expresswayNumber2 = entity.getExpresswayNumber2();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";
        if (length1 == null || length1.isEmpty()) length1 = " ";
        if (length2 == null || length2.isEmpty()) length2 = " ";
        if (length3 == null || length3.isEmpty()) length3 = " ";
        if (expresswayNumber1 == null || expresswayNumber1.isEmpty()) expresswayNumber1 = " ";
        if (expresswayNumber2 == null || expresswayNumber2.isEmpty()) expresswayNumber2 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayDistanceFromLocation6.FACING);
        SignExpresswayDistanceFromLocation6.Type type = entity.getCachedState().get(SignExpresswayDistanceFromLocation6.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        renderLeftAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -7f, 8f, 0.04f, 0xFFFFFF);
        renderLeftAlignedText(matrices, vertexConsumers, light, facing, text2, signType, -7f, -3f, 0.04f, 0xFFFFFF);
        renderLeftAlignedText(matrices, vertexConsumers, light, facing, text3, signType, -7f, -10f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length1, signType, 13.5f, 8f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length2, signType, 13.5f, -3f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length3, signType, 13.5f, -10f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 17.5f, 7.5f, 0.025f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 17.5f, -3.5f, 0.025f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 17.5f, -10.5f, 0.025f, 0xFFFFFF);
        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, -14f, 8f, signType, expresswayNumber1);
        renderExpresswayText(matrices, vertexConsumers, light, facing, expresswayNumber1, signType, -14f, 7.5f, 0.045f, 0xFFFFFF, 0.002f, TextAlignment.CENTER);
        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway2, -14f, -5.5f, signType, expresswayNumber2);
        renderExpresswayText(matrices, vertexConsumers, light, facing, expresswayNumber2, signType, -14f, -6f, 0.045f, 0xFFFFFF, 0.002f, TextAlignment.CENTER);
    }

    private void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayDistanceFromLocation6Entity.Expressway expressway, float andX, float andY, SignType type, String expresswayNumber) {
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
}
