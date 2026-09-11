package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayExit8;
import com.beigu.yunbeiuc.entity.SignExpresswayExit8Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignExpresswayExit8EntityRenderer extends BaseSignRenderer<SignExpresswayExit8Entity> {

    private static final Identifier NATIONAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_1.png");
    private static final Identifier PROVINCIAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_1.png");
    private static final Identifier NATIONAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_2.png");
    private static final Identifier PROVINCIAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_2.png");
    private static final Identifier NORTH = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_north.png");
    private static final Identifier EAST = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_east.png");
    private static final Identifier SOUTH = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_south.png");
    private static final Identifier WEST = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_west.png");

    public SignExpresswayExit8EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayExit8Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignExpresswayExit8Entity.Direction direction1 = entity.getDirection1();
        SignExpresswayExit8Entity.Direction direction2 = entity.getDirection2();
        SignExpresswayExit8Entity.Expressway expressway1 = entity.getExpressway1();
        SignExpresswayExit8Entity.Expressway expressway2 = entity.getExpressway2();
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String exitNumber = entity.getExitNumber();
        String expresswayNumber1 = entity.getExpresswayNumber1();
        String expresswayNumber2 = entity.getExpresswayNumber2();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (exitNumber == null || exitNumber.isEmpty()) exitNumber = " ";
        if (expresswayNumber1 == null || expresswayNumber1.isEmpty()) expresswayNumber1 = " ";
        if (expresswayNumber2 == null || expresswayNumber2.isEmpty()) expresswayNumber2 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayExit8.FACING);
        SignExpresswayExit8.Type type = entity.getCachedState().get(SignExpresswayExit8.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, -8f, 9f, signType, expresswayNumber1);
        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway2, 8f, 9f, signType, expresswayNumber2);
        renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction1, -18.5f, 9.25f, signType);
        renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction2, 18.5f, 9.25f, signType);
        renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -14f, -2f, 0.03f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text2, signType, 14.5f, -2f, 0.03f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, exitNumber, signType, 19.75f, 20.5f, 0.03f, 0x2D9B47);
        renderExpresswayText(matrices, vertexConsumers, light, facing, expresswayNumber1, signType, -7.5f, 8.5f, 0.045f, 0xFFFFFF, 0.002f, TextAlignment.CENTER);
        renderExpresswayText(matrices, vertexConsumers, light, facing, expresswayNumber2, signType, 7.5f, 8.5f, 0.045f, 0xFFFFFF, 0.002f, TextAlignment.CENTER);
    }

    private void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayExit8Entity.Expressway expressway, float andX, float andY, SignType type, String expresswayNumber) {
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

    private void renderDirectionLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayExit8Entity.Direction direction, float andX, float andY, SignType type) {
        Identifier texture = switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
        };

        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, andX, andY, 0.4f);
    }
}
