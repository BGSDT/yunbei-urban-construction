package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.ZonesBoard1;
import com.beigu.yunbeiuc.entity.ZonesBoard1Entity;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class ZonesBoard1EntityRenderer extends BaseSignRenderer<ZonesBoard1Entity> {

    public ZonesBoard1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(ZonesBoard1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        Block currentBlock = entity.getCachedState().getBlock();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Direction facing = entity.getCachedState().get(ZonesBoard1.FACING);
        ZonesBoard1.Type type = entity.getCachedState().get(ZonesBoard1.TYPE);

        SignType signType = SignTypeConverter.convert(type);
        int color = currentBlock == SignBlocks.ZONES_BOARD_RED.get() ? 0xFFFFFF : 0x000000;
        renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 0f, 0.04f, color);
    }
}