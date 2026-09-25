package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardTimeRange1;
import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange1Entity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class ZonesBoardTimeRange1EntityRenderer extends AbstractTextDisplayEntityRenderer<ZonesBoardTimeRange1Entity> {

    public ZonesBoardTimeRange1EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, ZonesBoardTimeRange1Entity entity) {
        Direction facing = entity.getBlockState().getValue(ZonesBoardTimeRange1.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(ZonesBoardTimeRange1Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(ZonesBoardTimeRange1.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
