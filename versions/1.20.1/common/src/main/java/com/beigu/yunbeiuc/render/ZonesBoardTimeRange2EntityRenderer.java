package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardTimeRange2;
import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange2Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class ZonesBoardTimeRange2EntityRenderer extends AbstractTextDisplayEntityRenderer<ZonesBoardTimeRange2Entity> {

    public ZonesBoardTimeRange2EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, ZonesBoardTimeRange2Entity entity) {
        Direction facing = entity.getCachedState().get(ZonesBoardTimeRange2.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(ZonesBoardTimeRange2Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(ZonesBoardTimeRange2.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
