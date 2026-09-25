package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDirection1;
import com.beigu.yunbeiuc.entity.SignExpresswayDirection1Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class SignExpresswayDirection1EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayDirection1Entity> {

    public SignExpresswayDirection1EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayDirection1Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayDirection1.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
    }

    @Override
    protected float getZOffset(SignExpresswayDirection1Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayDirection1.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
