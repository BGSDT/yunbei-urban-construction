package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayEntranceAdvance10;
import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance10Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignExpresswayEntranceAdvance10EntityRenderer extends BaseSignRenderer<SignExpresswayEntranceAdvance10Entity> {

    private static final Identifier NATIONAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_1.png");
    private static final Identifier PROVINCIAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_1.png");
    private static final Identifier NATIONAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_2.png");
    private static final Identifier PROVINCIAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_2.png");

    public SignExpresswayEntranceAdvance10EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayEntranceAdvance10Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignExpresswayEntranceAdvance10Entity.Expressway expressway1 = entity.getExpressway1();
        SignExpresswayEntranceAdvance10Entity.Expressway expressway2 = entity.getExpressway2();
        String expresswayNumber1 = entity.getExpresswayNumber1();
        String expresswayNumber2 = entity.getExpresswayNumber2();
        String text1 = entity.getText1();
        String text2 = entity.getText2();

        if (expresswayNumber1 == null || expresswayNumber1.isEmpty()) expresswayNumber1 = " ";
        if (expresswayNumber2 == null || expresswayNumber2.isEmpty()) expresswayNumber2 = " ";
        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayEntranceAdvance10.FACING);
        SignExpresswayEntranceAdvance10.Type type = entity.getCachedState().get(SignExpresswayEntranceAdvance10.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, -6f, 7f, signType, expresswayNumber1);
        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway2, 6f, 7f, signType, expresswayNumber2);
        renderExpresswayText(matrices, vertexConsumers, light, facing, expresswayNumber1, signType, -6f, 6.5f, 0.045f, 0xFFFFFF, 0.002f, TextAlignment.CENTER);
        renderExpresswayText(matrices, vertexConsumers, light, facing, expresswayNumber2, signType, 6f, 6.5f, 0.045f, 0xFFFFFF, 0.002f, TextAlignment.CENTER);
        renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -7f, -2f, 0.035f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text2, signType, 7f, -2f, 0.035f, 0xFFFFFF);
    }

    private void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayEntranceAdvance10Entity.Expressway expressway, float andX, float andY, SignType type, String expresswayNumber) {
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
