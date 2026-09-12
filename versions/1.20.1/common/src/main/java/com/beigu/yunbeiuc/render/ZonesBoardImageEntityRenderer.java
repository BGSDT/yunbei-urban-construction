package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardImage;
import com.beigu.yunbeiuc.entity.ZonesBoardImageEntity;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class ZonesBoardImageEntityRenderer extends AbstractTextDisplayEntityRenderer<ZonesBoardImageEntity> {

    public ZonesBoardImageEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(MatrixStack matrices, ZonesBoardImageEntity entity) {
        Direction facing = entity.getCachedState().get(ZonesBoardImage.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
    }

    @Override
    protected float getZOffset(ZonesBoardImageEntity entity) {
        // 原渲染器 logo 使用自定义绝对 Z（renderTextureWithCustomZ，非标准基準值）；
        // 文本行比 logo 靠前 0.01 方块（默认行 zOffset = 0.01 * 16）
        return switch (SignTypeConverter.convert(entity.getCachedState().get(ZonesBoardImage.TYPE))) {
            case POLE_L -> -1.75f;
            case POLE_H -> -1.79f;
            case NORMAL -> -1.43f;
        };
    }
}
