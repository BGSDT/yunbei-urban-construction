package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoard1;
import com.beigu.yunbeiuc.entity.ZonesBoard1Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class ZonesBoard1EntityRenderer extends AbstractTextDisplayEntityRenderer<ZonesBoard1Entity> {

    public ZonesBoard1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, ZonesBoard1Entity entity) {
        Direction facing = entity.getCachedState().get(ZonesBoard1.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(ZonesBoard1Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(ZonesBoard1.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
