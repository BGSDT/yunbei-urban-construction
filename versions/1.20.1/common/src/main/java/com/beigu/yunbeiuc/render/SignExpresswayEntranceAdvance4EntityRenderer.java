package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayEntranceAdvance4;
import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance4Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignExpresswayEntranceAdvance4EntityRenderer extends BaseSignRenderer<SignExpresswayEntranceAdvance4Entity> {

    private static final Identifier NATIONAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_1.png");
    private static final Identifier PROVINCIAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_1.png");
    private static final Identifier NATIONAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_2.png");
    private static final Identifier PROVINCIAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_2.png");
    private static final Identifier NORTH = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_north.png");
    private static final Identifier EAST = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_east.png");
    private static final Identifier SOUTH = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_south.png");
    private static final Identifier WEST = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_west.png");

    public SignExpresswayEntranceAdvance4EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayEntranceAdvance4Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignExpresswayEntranceAdvance4Entity.Direction direction1 = entity.getDirection1();
        SignExpresswayEntranceAdvance4Entity.Expressway expressway1 = entity.getExpressway1();
        String text1 = entity.getText1();
        String expresswayNumber1 = entity.getExpresswayNumber1();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (expresswayNumber1 == null || expresswayNumber1.isEmpty()) expresswayNumber1 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayEntranceAdvance4.FACING);
        SignExpresswayEntranceAdvance4.Type type = entity.getCachedState().get(SignExpresswayEntranceAdvance4.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, -3f, 7f, signType, expresswayNumber1);
        renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction1, 6f, 7f, signType);
        renderCenteredText(matrices, vertexConsumers, light, facing, text1 + "方向", signType, 0f, -2f, 0.035f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, expresswayNumber1, signType, -3f, 6.5f, 0.045f, 0xFFFFFF);
    }

    private void renderDirectionLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayEntranceAdvance4Entity.Direction direction, float andX, float andY, SignType type) {
        Identifier texture = switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
        };

        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, andX, andY, 0.4f);
    }

    private void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayEntranceAdvance4Entity.Expressway expressway, float andX, float andY, SignType type, String expresswayNumber) {
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