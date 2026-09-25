package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardOverWeight;
import com.beigu.yunbeiuc.entity.ZonesBoardOverWeightEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class ZonesBoardOverWeightEntityRenderer extends AbstractTextDisplayEntityRenderer<ZonesBoardOverWeightEntity> {

    public ZonesBoardOverWeightEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, ZonesBoardOverWeightEntity entity) {
        Direction facing = entity.getBlockState().getValue(ZonesBoardOverWeight.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
    }

    @Override
    protected float getZOffset(ZonesBoardOverWeightEntity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(ZonesBoardOverWeight.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
