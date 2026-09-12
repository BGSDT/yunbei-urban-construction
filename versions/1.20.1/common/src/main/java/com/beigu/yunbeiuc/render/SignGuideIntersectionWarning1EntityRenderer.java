package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning1;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning1Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SignGuideIntersectionWarning1EntityRenderer extends AbstractTextDisplayEntityRenderer<SignGuideIntersectionWarning1Entity> {

    public SignGuideIntersectionWarning1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, SignGuideIntersectionWarning1Entity entity) {
        Direction facing = entity.getCachedState().get(SignGuideIntersectionWarning1.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(SignGuideIntersectionWarning1Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(SignGuideIntersectionWarning1.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
