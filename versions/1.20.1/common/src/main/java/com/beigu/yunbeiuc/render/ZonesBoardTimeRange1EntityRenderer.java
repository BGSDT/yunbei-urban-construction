package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardTimeRange1;
import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange1Entity;
import com.beigu.yunbeiuc.render.base.BaseSignRenderer;
import com.beigu.yunbeiuc.render.base.SignTypeConverter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class ZonesBoardTimeRange1EntityRenderer extends BaseSignRenderer<ZonesBoardTimeRange1Entity> {

    public ZonesBoardTimeRange1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(ZonesBoardTimeRange1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String time1 = entity.getTime1();
        String time2 = entity.getTime2();

        if (time1 == null || time1.isEmpty()) time1 = " ";
        if (time2 == null || time2.isEmpty()) time2 = " ";

        Direction facing = entity.getCachedState().get(ZonesBoardTimeRange1.FACING);
        ZonesBoardTimeRange1.Type type = entity.getCachedState().get(ZonesBoardTimeRange1.TYPE);

        SignType signType = SignTypeConverter.convert(type);
        renderCenteredText(matrices, vertexConsumers, light, facing, time1 + "-" + time2, signType, 0f, 5.5f, 0.02f, 0x000000);
    }
}