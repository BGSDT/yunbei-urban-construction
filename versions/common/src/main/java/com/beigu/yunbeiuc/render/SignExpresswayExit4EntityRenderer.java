package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayExit4;
import com.beigu.yunbeiuc.entity.SignExpresswayExit4Entity;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class SignExpresswayExit4EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayExit4Entity> {

    public SignExpresswayExit4EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayExit4Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayExit4.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignExpresswayExit4Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayExit4.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
