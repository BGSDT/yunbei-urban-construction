package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardOverWeight;
import com.beigu.yunbeiuc.entity.ZonesBoardOverWeightEntity;
import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class ZonesBoardOverWeightEntityRenderer implements BlockEntityRenderer<ZonesBoardOverWeightEntity> {
    private final TextRenderer textRenderer;

    public ZonesBoardOverWeightEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(ZonesBoardOverWeightEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(ZonesBoardOverWeight.FACING);
        ZonesBoardOverWeight.Type type = entity.getCachedState().get(ZonesBoardOverWeight.TYPE);
        if(currentBlock == SignBlocks.ZONES_BOARD_OVER_WEIGHT.get() || currentBlock == SignBlocks.ZONES_BOARD_TIME_LIMIT.get()) {
            renderRightText(matrices, vertexConsumers, light, facing, text1, type, -5f, 5.5f, true);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_SUGGESTED_SPEED.get()) {
            renderRightText(matrices, vertexConsumers, light, facing, text1, type, -6f, 0f, false);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_LENGTH.get()) {
            renderCentreText(matrices, vertexConsumers, light, facing, text1, type, 1f, 0f, false);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_DISTANCE_LENGTH.get()) {
            renderCentreText(matrices, vertexConsumers, light, facing, text1, type, 0f, 0f, false);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_LEFT.get()) {
            renderCentreText(matrices, vertexConsumers, light, facing, text1, type, 0f, 1f, true);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_RIGHT.get()) {
            renderCentreText(matrices, vertexConsumers, light, facing, text1, type, -2f, 1f, true);
        }
    }

    private void renderRightText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, ZonesBoardOverWeight.Type type, float andX, float andY, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = isSmallScale ? 0.03f : 0.04f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        float centeredX = andX / 16f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                0x000000,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    private void renderCentreText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, ZonesBoardOverWeight.Type type, float andX, float andY, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = isSmallScale ? 0.03f : 0.04f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        float centeredX = andX / 16f - (textWidth * scaleValue) / 2f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                0x000000,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(ZonesBoardOverWeightEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}