package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation6;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation6Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class SignExpresswayDistanceFromLocation6EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayDistanceFromLocation6Entity> {

    public SignExpresswayDistanceFromLocation6EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayDistanceFromLocation6Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayDistanceFromLocation6.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignExpresswayDistanceFromLocation6Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayDistanceFromLocation6.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
