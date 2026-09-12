package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayExit8;
import com.beigu.yunbeiuc.entity.SignExpresswayExit8Entity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SignExpresswayExit8EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayExit8Entity> {

    public SignExpresswayExit8EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, SignExpresswayExit8Entity entity) {
        Direction facing = entity.getCachedState().get(SignExpresswayExit8.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(SignExpresswayExit8Entity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(SignExpresswayExit8.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
