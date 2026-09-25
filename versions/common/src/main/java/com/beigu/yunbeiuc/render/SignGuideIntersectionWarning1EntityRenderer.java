package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning1;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning1Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class SignGuideIntersectionWarning1EntityRenderer extends AbstractTextDisplayEntityRenderer<SignGuideIntersectionWarning1Entity> {

    public SignGuideIntersectionWarning1EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignGuideIntersectionWarning1Entity entity) {
        Direction facing = entity.getBlockState().getValue(SignGuideIntersectionWarning1.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignGuideIntersectionWarning1Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignGuideIntersectionWarning1.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
