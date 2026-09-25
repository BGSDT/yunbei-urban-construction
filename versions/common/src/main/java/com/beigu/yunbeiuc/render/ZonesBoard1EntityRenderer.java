package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.ZonesBoard1;
import com.beigu.yunbeiuc.entity.ZonesBoard1Entity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class ZonesBoard1EntityRenderer extends AbstractTextDisplayEntityRenderer<ZonesBoard1Entity> {

    public ZonesBoard1EntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, ZonesBoard1Entity entity) {
        Direction facing = entity.getBlockState().getValue(ZonesBoard1.FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
    }

    @Override
    protected float getZOffset(ZonesBoard1Entity entity) {
        return switch (SignTypeConverter.convert(entity.getBlockState().getValue(ZonesBoard1.TYPE))) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.45f;
        };
    }
}
