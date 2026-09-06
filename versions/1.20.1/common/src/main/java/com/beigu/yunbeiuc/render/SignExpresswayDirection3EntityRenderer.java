package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDirection3;
import com.beigu.yunbeiuc.entity.SignExpresswayDirection3Entity;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignExpresswayDirection3EntityRenderer extends BaseSignRenderer<SignExpresswayDirection3Entity> {

    private static final Identifier NATIONAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_1.png");
    private static final Identifier PROVINCIAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_1.png");
    private static final Identifier NATIONAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_2.png");
    private static final Identifier PROVINCIAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_2.png");

    public SignExpresswayDirection3EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayDirection3Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignExpresswayDirection3Entity.Expressway expressway1 = entity.getExpressway1();
        String text1 = entity.getText1();
        String expresswayNumber1 = entity.getExpresswayNumber1();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (expresswayNumber1 == null || expresswayNumber1.isEmpty()) expresswayNumber1 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayDirection3.FACING);
        SignExpresswayDirection3.Type type = entity.getCachedState().get(SignExpresswayDirection3.TYPE);
        Block currentBlock = entity.getCachedState().getBlock();

        SignType signType = SignTypeConverter.convert(type);

        if (currentBlock == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_3.get()){
            renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, 4.5f, 7f, signType, expresswayNumber1);
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 4.5f, -7f, 0.05f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, expresswayNumber1, signType, 4.5f, 6.5f, 0.06f, 0xFFFFFF);
        } else if (currentBlock == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_4.get()) {
            renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, -4.5f, 7f, signType, expresswayNumber1);
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -4.5f, -7f, 0.05f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, expresswayNumber1, signType, -4.5f, 6.5f, 0.06f, 0xFFFFFF);
        }
    }

    private void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayDirection3Entity.Expressway expressway, float andX, float andY, SignType type, String expresswayNumber) {
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

        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, andX, andY, 0.85f);
    }
}