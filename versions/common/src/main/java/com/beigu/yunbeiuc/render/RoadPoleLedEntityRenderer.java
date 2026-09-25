package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.RoadPoleLedEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

public class RoadPoleLedEntityRenderer extends AbstractTextDisplayEntityRenderer<RoadPoleLedEntity> {
    public RoadPoleLedEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, RoadPoleLedEntity entity) {
        Direction facing = entity.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
    }

    @Override
    protected float getZOffset(RoadPoleLedEntity entity) {
        return 2.5f / 16f + 0.0125f;
    }
}
