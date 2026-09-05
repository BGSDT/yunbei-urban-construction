package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDirection5;
import com.beigu.yunbeiuc.entity.SignExpresswayDirection5Entity;
import com.beigu.yunbeiuc.render.base.BaseSignRenderer;
import com.beigu.yunbeiuc.render.base.SignTypeConverter;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class SignExpresswayDirection5EntityRenderer extends BaseSignRenderer<SignExpresswayDirection5Entity> {

    private static final Identifier NATIONAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_1.png");
    private static final Identifier PROVINCIAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_1.png");
    private static final Identifier NATIONAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_2.png");
    private static final Identifier PROVINCIAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_2.png");
    private static final Identifier NORTH = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_north.png");
    private static final Identifier EAST = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_east.png");
    private static final Identifier SOUTH = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_south.png");
    private static final Identifier WEST = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_west.png");

    public SignExpresswayDirection5EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayDirection5Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignExpresswayDirection5Entity.Expressway expressway1 = entity.getExpressway1();
        String text1 = entity.getText1();
        String expresswayNumber1 = entity.getExpresswayNumber1();
        SignExpresswayDirection5Entity.Direction direction1 = entity.getDirection1();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (expresswayNumber1 == null || expresswayNumber1.isEmpty()) expresswayNumber1 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayDirection5.FACING);
        SignExpresswayDirection5.Type type = entity.getCachedState().get(SignExpresswayDirection5.TYPE);
        Block currentBlock = entity.getCachedState().getBlock();

        SignType signType = SignTypeConverter.convert(type);

        if (currentBlock == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_5.get()){
            renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, 4.5f, 7f, signType, expresswayNumber1);
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 4.5f, -7f, 0.05f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, expresswayNumber1, signType, 4.5f, 6.5f, 0.06f, 0xFFFFFF);
            renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction1, -8.5f, 8.5f, signType);
        } else if (currentBlock == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_6.get()) {
            renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, -4.5f, 7f, signType, expresswayNumber1);
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -4.5f, -7f, 0.05f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, expresswayNumber1, signType, -4.5f, 6.5f, 0.06f, 0xFFFFFF);
            renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction1, 8.5f, 8.5f, signType);
        }
    }

    private void renderDirectionLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayDirection5Entity.Direction direction, float andX, float andY, SignType type) {
        Identifier texture = switch (direction) {
            case NORTH -> NORTH;
            case EAST -> EAST;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
        };

        renderTexture(matrices, vertexConsumers, light, overlay, facing, texture, type, andX, andY, 0.5f);
    }

    private void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayDirection5Entity.Expressway expressway, float andX, float andY, SignType type, String expresswayNumber) {
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