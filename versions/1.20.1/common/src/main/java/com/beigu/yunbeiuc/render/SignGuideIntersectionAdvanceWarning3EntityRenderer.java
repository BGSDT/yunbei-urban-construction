package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning3;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning3Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SignGuideIntersectionAdvanceWarning3EntityRenderer extends AbstractTextDisplayEntityRenderer<SignGuideIntersectionAdvanceWarning3Entity> {

    public SignGuideIntersectionAdvanceWarning3EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, SignGuideIntersectionAdvanceWarning3Entity entity) {
        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning3.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(SignGuideIntersectionAdvanceWarning3Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(SignGuideIntersectionAdvanceWarning3.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
