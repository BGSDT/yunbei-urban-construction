package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.RoadPoleTextDisplayEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class RoadPoleTextDisplayEntityRenderer extends AbstractTextDisplayEntityRenderer<RoadPoleTextDisplayEntity> {
    public RoadPoleTextDisplayEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, RoadPoleTextDisplayEntity entity) {
        Direction facing = entity.getCachedState().get(net.minecraft.state.property.Properties.HORIZONTAL_FACING);
        matrices.translate(0.5, 0.5f - 7f / 16f, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-90));
    }

    @Override
    protected float getZOffset(RoadPoleTextDisplayEntity entity) {
        return 3.5f / 16f + 0.0125f;
    }
}
