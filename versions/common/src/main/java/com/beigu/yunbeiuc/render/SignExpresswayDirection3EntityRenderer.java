package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDirection3;
import com.beigu.yunbeiuc.entity.SignExpresswayDirection3Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class SignExpresswayDirection3EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayDirection3Entity> {

    public SignExpresswayDirection3EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayDirection3Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayDirection3.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignExpresswayDirection3Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayDirection3.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
