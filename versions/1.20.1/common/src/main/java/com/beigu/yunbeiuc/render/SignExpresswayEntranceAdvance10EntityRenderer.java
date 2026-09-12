package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayEntranceAdvance10;
import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance10Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SignExpresswayEntranceAdvance10EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayEntranceAdvance10Entity> {

    public SignExpresswayEntranceAdvance10EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, SignExpresswayEntranceAdvance10Entity entity) {
        Direction facing = entity.getCachedState().get(SignExpresswayEntranceAdvance10.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(SignExpresswayEntranceAdvance10Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(SignExpresswayEntranceAdvance10.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
