package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1Wuhan;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer extends AbstractTextDisplayEntityRenderer<SignGuideIntersectionAdvanceWarning1WuhanEntity> {

    public SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, SignGuideIntersectionAdvanceWarning1WuhanEntity entity) {
        Direction facing = entity.getBlockState().getValue(SignGuideIntersectionAdvanceWarning1Wuhan.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(SignGuideIntersectionAdvanceWarning1WuhanEntity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(SignGuideIntersectionAdvanceWarning1Wuhan.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
