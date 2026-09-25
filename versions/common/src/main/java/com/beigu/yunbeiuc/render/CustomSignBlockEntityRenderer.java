package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.CustomSignTypeBlock;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class CustomSignBlockEntityRenderer extends AbstractTextDisplayEntityRenderer<CustomSignBlockEntity> {
    public CustomSignBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, CustomSignBlockEntity entity) {
        Direction facing = entity.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
    }

    @Override
    protected float getZOffset(CustomSignBlockEntity entity) {
        CustomSignTypeBlock.Type type = entity.getBlockState().getValue(CustomSignTypeBlock.TYPE);
        return switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.46f;
        };
    }
}
