package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.CustomSignTypeBlock;
import com.beigu.yunbeiuc.block.custom.sign.WallTextDisplayBlock;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class CustomSignBlockEntityRenderer extends AbstractTextDisplayEntityRenderer<CustomSignBlockEntity> {
    public CustomSignBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, CustomSignBlockEntity entity) {
        BlockState state = entity.getBlockState();
        matrices.translate(0.5, 0.5, 0.5);
        if (state.getBlock() instanceof WallTextDisplayBlock) {
            Direction facing = state.getValue(WallTextDisplayBlock.FACING);
            if (facing.getAxis() == Direction.Axis.Y) {
                VersionServices.render().rotateX(matrices, facing == Direction.UP ? -90.0f : 90.0f);
            } else {
                VersionServices.render().rotateY(matrices, -facing.toYRot());
            }
            return;
        }
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(CustomSignBlockEntity entity) {
        BlockState state = entity.getBlockState();
        if (state.getBlock() instanceof WallTextDisplayBlock) return -0.499f;
        CustomSignTypeBlock.Type type = state.getValue(CustomSignTypeBlock.TYPE);
        return switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.81f;
            case NORMAL -> -0.46f;
        };
    }
}
