package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayEntranceAdvance4;
import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance4Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SignExpresswayEntranceAdvance4EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayEntranceAdvance4Entity> {

    public SignExpresswayEntranceAdvance4EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, SignExpresswayEntranceAdvance4Entity entity) {
        Direction facing = entity.getCachedState().get(SignExpresswayEntranceAdvance4.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(SignExpresswayEntranceAdvance4Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(SignExpresswayEntranceAdvance4.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
