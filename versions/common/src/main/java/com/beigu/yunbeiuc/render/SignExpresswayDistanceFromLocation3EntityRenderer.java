package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation3;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation3Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class SignExpresswayDistanceFromLocation3EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayDistanceFromLocation3Entity> {

    public SignExpresswayDistanceFromLocation3EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayDistanceFromLocation3Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayDistanceFromLocation3.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
    }

    @Override
    protected float getZOffset(SignExpresswayDistanceFromLocation3Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayDistanceFromLocation3.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
