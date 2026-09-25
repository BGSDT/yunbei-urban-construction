package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayEntranceAdvance4;
import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance4Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class SignExpresswayEntranceAdvance4EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayEntranceAdvance4Entity> {

    public SignExpresswayEntranceAdvance4EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayEntranceAdvance4Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayEntranceAdvance4.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignExpresswayEntranceAdvance4Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayEntranceAdvance4.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
