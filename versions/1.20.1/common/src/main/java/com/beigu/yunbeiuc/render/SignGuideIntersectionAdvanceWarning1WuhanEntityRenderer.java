package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1Wuhan;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer extends AbstractTextDisplayEntityRenderer<SignGuideIntersectionAdvanceWarning1WuhanEntity> {

    public SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, SignGuideIntersectionAdvanceWarning1WuhanEntity entity) {
        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1Wuhan.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(SignGuideIntersectionAdvanceWarning1WuhanEntity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1Wuhan.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
