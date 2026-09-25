package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayExit8;
import com.beigu.yunbeiuc.entity.SignExpresswayExit8Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class SignExpresswayExit8EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayExit8Entity> {

    public SignExpresswayExit8EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayExit8Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayExit8.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
    }

    @Override
    protected float getZOffset(SignExpresswayExit8Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayExit8.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
