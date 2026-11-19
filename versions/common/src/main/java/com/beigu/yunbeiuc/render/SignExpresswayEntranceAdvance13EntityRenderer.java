package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayEntranceAdvance13;
import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance13Entity;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;

public class SignExpresswayEntranceAdvance13EntityRenderer extends AbstractTextDisplayEntityRenderer<SignExpresswayEntranceAdvance13Entity> {

    public SignExpresswayEntranceAdvance13EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignExpresswayEntranceAdvance13Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignExpresswayEntranceAdvance13.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignExpresswayEntranceAdvance13Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignExpresswayEntranceAdvance13.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
