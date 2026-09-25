package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning4;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class SignGuideIntersectionWarning4EntityRenderer extends AbstractTextDisplayEntityRenderer<SignGuideIntersectionWarning4Entity> {

    public SignGuideIntersectionWarning4EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignGuideIntersectionWarning4Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignGuideIntersectionWarning4.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignGuideIntersectionWarning4Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignGuideIntersectionWarning4.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
