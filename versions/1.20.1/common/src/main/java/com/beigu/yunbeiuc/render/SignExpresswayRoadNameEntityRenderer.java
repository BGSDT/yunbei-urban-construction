package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayRoadName;
import com.beigu.yunbeiuc.entity.SignExpresswayRoadNameEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class SignExpresswayRoadNameEntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayRoadNameEntity> {

    public SignExpresswayRoadNameEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, SignExpresswayRoadNameEntity entity) {
        Direction facing = entity.getCachedState().get(SignExpresswayRoadName.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(SignExpresswayRoadNameEntity entity) {
        return switch (SignTypeConverter.convert(entity.getCachedState().get(SignExpresswayRoadName.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
