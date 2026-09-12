package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning5;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning5Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SignGuideIntersectionAdvanceWarning5EntityRenderer extends AbstractTextDisplayEntityRenderer<SignGuideIntersectionAdvanceWarning5Entity> {

    public SignGuideIntersectionAdvanceWarning5EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, SignGuideIntersectionAdvanceWarning5Entity entity) {
        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning5.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(SignGuideIntersectionAdvanceWarning5Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(SignGuideIntersectionAdvanceWarning5.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
