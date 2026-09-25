package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDirection5;
import com.beigu.yunbeiuc.entity.SignExpresswayDirection5Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class SignExpresswayDirection5EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayDirection5Entity> {

    public SignExpresswayDirection5EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayDirection5Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayDirection5.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignExpresswayDirection5Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayDirection5.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
