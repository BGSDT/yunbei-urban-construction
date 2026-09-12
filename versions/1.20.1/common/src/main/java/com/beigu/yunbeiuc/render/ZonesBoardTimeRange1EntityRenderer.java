package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardTimeRange1;
import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange1Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class ZonesBoardTimeRange1EntityRenderer extends AbstractTextDisplayEntityRenderer<ZonesBoardTimeRange1Entity> {

    public ZonesBoardTimeRange1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, ZonesBoardTimeRange1Entity entity) {
        Direction facing = entity.getCachedState().get(ZonesBoardTimeRange1.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(ZonesBoardTimeRange1Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(ZonesBoardTimeRange1.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
