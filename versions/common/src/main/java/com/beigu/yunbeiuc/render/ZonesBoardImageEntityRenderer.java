package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardImage;
import com.beigu.yunbeiuc.entity.ZonesBoardImageEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class ZonesBoardImageEntityRenderer extends AbstractTextDisplayEntityRenderer<ZonesBoardImageEntity> {

    public ZonesBoardImageEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, ZonesBoardImageEntity entity) {
        Direction facing = entity.getBlockState().getValue(ZonesBoardImage.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(ZonesBoardImageEntity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(ZonesBoardImage.TYPE))) {
            case POLE_L -> -1.75f;
            case POLE_H -> -1.79f;
            case NORMAL -> -1.43f;
        };
    }
}
