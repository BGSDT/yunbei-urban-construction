package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardOverWeight;
import com.beigu.yunbeiuc.entity.ZonesBoardOverWeightEntity;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class ZonesBoardOverWeightEntityRenderer extends BaseSignRenderer<ZonesBoardOverWeightEntity> {

    public ZonesBoardOverWeightEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(ZonesBoardOverWeightEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(ZonesBoardOverWeight.FACING);
        ZonesBoardOverWeight.Type type = entity.getCachedState().get(ZonesBoardOverWeight.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        if(currentBlock == SignBlocks.ZONES_BOARD_OVER_WEIGHT.get() || currentBlock == SignBlocks.ZONES_BOARD_TIME_LIMIT.get()) {
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -5f, 5.5f, 0.03f, 0x000000);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_SUGGESTED_SPEED.get()) {
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -6f, 0f, 0.04f, 0x000000);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_LENGTH.get()) {
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 1f, 0f, 0.04f, 0x000000);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_DISTANCE_LENGTH.get()) {
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 0f, 0.04f, 0x000000);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_LEFT.get()) {
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 1f, 0.03f, 0x000000);
        } else if (currentBlock == SignBlocks.ZONES_BOARD_DISTANCE_LENGTH_RIGHT.get()) {
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -2f, 1f, 0.03f, 0x000000);
        }
    }
}