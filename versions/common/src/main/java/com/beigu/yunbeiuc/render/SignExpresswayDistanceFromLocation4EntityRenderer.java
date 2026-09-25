package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation4;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation4Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class SignExpresswayDistanceFromLocation4EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayDistanceFromLocation4Entity> {

    public SignExpresswayDistanceFromLocation4EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayDistanceFromLocation4Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayDistanceFromLocation4.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignExpresswayDistanceFromLocation4Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayDistanceFromLocation4.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
