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
        // 原渲染器 logo 使用自定义绝对 Z（renderTextureWithCustomZ，非标准基準值）；
        // 文本行比 logo 靠前 0.01 方块（默认行 zOffset = 0.01 * 16）
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(ZonesBoardImage.TYPE))) {
            case POLE_L -> -1.75f;
            case POLE_H -> -1.79f;
            case NORMAL -> -1.43f;
        };
    }
}
