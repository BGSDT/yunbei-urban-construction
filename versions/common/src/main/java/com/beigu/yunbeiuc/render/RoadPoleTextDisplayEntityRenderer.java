package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.RoadPoleTextDisplayEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class RoadPoleTextDisplayEntityRenderer extends AbstractTextDisplayEntityRenderer<RoadPoleTextDisplayEntity> {
    public RoadPoleTextDisplayEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, RoadPoleTextDisplayEntity entity) {
        Direction facing = entity.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        matrices.translate(0.5, 0.5f - 7f / 16f, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
        matrices.mulPose(Vector3f.YP.rotationDegrees(-90));
    }

    @Override
    protected float getZOffset(RoadPoleTextDisplayEntity entity) {
        return 3.5f / 16f + 0.0125f;
    }
}
