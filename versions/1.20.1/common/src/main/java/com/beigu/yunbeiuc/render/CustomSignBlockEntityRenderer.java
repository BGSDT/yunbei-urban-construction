package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.CustomSignTypeBlock;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class CustomSignBlockEntityRenderer extends AbstractTextDisplayEntityRenderer<CustomSignBlockEntity> {
    public CustomSignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, CustomSignBlockEntity entity) {
        Direction facing = entity.getCachedState().get(net.minecraft.state.property.Properties.HORIZONTAL_FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(CustomSignBlockEntity entity) {
        CustomSignTypeBlock.Type type = entity.getCachedState().get(CustomSignTypeBlock.TYPE);
        return switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.46f;
        };
    }
}
