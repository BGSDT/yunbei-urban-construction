package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation1;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation1Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SignExpresswayDistanceFromLocation1EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayDistanceFromLocation1Entity> {

    public SignExpresswayDistanceFromLocation1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, SignExpresswayDistanceFromLocation1Entity entity) {
        Direction facing = entity.getCachedState().get(SignExpresswayDistanceFromLocation1.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(SignExpresswayDistanceFromLocation1Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(SignExpresswayDistanceFromLocation1.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
