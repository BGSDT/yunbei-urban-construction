package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.GantryFrameLedEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class GantryFrameLedEntityRenderer extends AbstractTextDisplayEntityRenderer<GantryFrameLedEntity> {
    public GantryFrameLedEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, GantryFrameLedEntity entity) {
        Direction facing = entity.getCachedState().get(net.minecraft.state.property.Properties.HORIZONTAL_FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(GantryFrameLedEntity entity) {
        return 6f / 16f + 0.0125f;
    }
}
