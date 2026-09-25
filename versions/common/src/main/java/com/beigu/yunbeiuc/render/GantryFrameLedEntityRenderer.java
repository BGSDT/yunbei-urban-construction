package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.GantryFrameLedEntity;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

public class GantryFrameLedEntityRenderer extends AbstractTextDisplayEntityRenderer<GantryFrameLedEntity> {
    public GantryFrameLedEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    @Override
    protected void applyTransforms(PoseStack matrices, GantryFrameLedEntity entity) {
        Direction facing = entity.getBlockState().getValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.HORIZONTAL_FACING);
        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
    }

    @Override
    protected float getZOffset(GantryFrameLedEntity entity) {
        return 6f / 16f + 0.0125f;
    }
}
