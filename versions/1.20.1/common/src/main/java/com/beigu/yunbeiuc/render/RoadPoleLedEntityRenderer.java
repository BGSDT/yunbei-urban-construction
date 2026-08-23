package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.RoadPoleLedEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class RoadPoleLedEntityRenderer extends AbstractTextDisplayEntityRenderer<RoadPoleLedEntity> {
    public RoadPoleLedEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, RoadPoleLedEntity entity) {
        Direction facing = entity.getCachedState().get(net.minecraft.state.property.Properties.HORIZONTAL_FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(RoadPoleLedEntity entity) {
        return 2.5f / 16f + 0.0125f;
    }
}
