package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardTimeRange2;
import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange2Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class ZonesBoardTimeRange2EntityRenderer extends AbstractTextDisplayEntityRenderer<ZonesBoardTimeRange2Entity> {

    public ZonesBoardTimeRange2EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, ZonesBoardTimeRange2Entity entity) {
        Direction facing = entity.getBlockState().getValue(ZonesBoardTimeRange2.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
    }

    @Override
    protected float getZOffset(ZonesBoardTimeRange2Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(ZonesBoardTimeRange2.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
