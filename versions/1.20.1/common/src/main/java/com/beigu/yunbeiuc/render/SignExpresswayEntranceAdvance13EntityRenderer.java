package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayEntranceAdvance13;
import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance13Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignExpresswayEntranceAdvance13EntityRenderer extends BaseSignRenderer<SignExpresswayEntranceAdvance13Entity> {

    private static final Identifier NATIONAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_1.png");
    private static final Identifier PROVINCIAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_1.png");
    private static final Identifier NATIONAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_2.png");
    private static final Identifier PROVINCIAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_2.png");

    public SignExpresswayEntranceAdvance13EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayEntranceAdvance13Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignExpresswayEntranceAdvance13Entity.Expressway expressway1 = entity.getExpressway1();
        SignExpresswayEntranceAdvance13Entity.Expressway expressway2 = entity.getExpressway2();
        String expresswayNumber1 = entity.getExpresswayNumber1();
        String expresswayNumber2 = entity.getExpresswayNumber2();
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();
        String text4 = entity.getText4();

        if (expresswayNumber1 == null || expresswayNumber1.isEmpty()) expresswayNumber1 = " ";
        if (expresswayNumber2 == null || expresswayNumber2.isEmpty()) expresswayNumber2 = " ";
        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";
        if (text4 == null || text4.isEmpty()) text4 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayEntranceAdvance13.FACING);
        SignExpresswayEntranceAdvance13.Type type = entity.getCachedState().get(SignExpresswayEntranceAdvance13.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, -6f, 10f, signType, expresswayNumber1);
        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway2, 6f, 10f, signType, expresswayNumber2);
        renderCenteredText(matrices, vertexConsumers, light, facing, expresswayNumber1, signType, -6f, 9.5f, 0.045f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, expresswayNumber2, signType, 6f, 9.5f, 0.045f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -7f, 1f, 0.035f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text2, signType, -7f, -6f, 0.035f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text3, signType, 7f, 1f, 0.035f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text4, signType, 7f, -6f, 0.035f, 0xFFFFFF);
    }

    private void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayEntranceAdvance13Entity.Expressway expressway, float andX, float andY, SignType type, String expresswayNumber) {
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

        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, andX, andY, 0.65f);
    }
}
