package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardTimeRange2;
import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange2Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class ZonesBoardTimeRange2EntityRenderer extends BaseSignRenderer<ZonesBoardTimeRange2Entity> {

    public ZonesBoardTimeRange2EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(ZonesBoardTimeRange2Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String time1 = entity.getTime1();
        String time2 = entity.getTime2();
        String time3 = entity.getTime3();
        String time4 = entity.getTime4();

        if (time1 == null || time1.isEmpty()) time1 = " ";
        if (time2 == null || time2.isEmpty()) time2 = " ";
        if (time3 == null || time3.isEmpty()) time3 = " ";
        if (time4 == null || time4.isEmpty()) time4 = " ";

        Direction facing = entity.getCachedState().get(ZonesBoardTimeRange2.FACING);
        ZonesBoardTimeRange2.Type type = entity.getCachedState().get(ZonesBoardTimeRange2.TYPE);

        SignType signType = SignTypeConverter.convert(type);
        renderCenteredText(matrices, vertexConsumers, light, facing, time1 + "-" + time2, signType, 0f, 6f, 0.02f, 0x000000);
        renderCenteredText(matrices, vertexConsumers, light, facing, time3 + "-" + time4, signType, 0f, 3f, 0.02f, 0x000000);
    }
}