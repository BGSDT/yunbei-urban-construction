package com.beigu.yunbeiuc.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class BlockEntityRendererCompat<T extends BlockEntity> implements BlockEntityRenderer<T> {
    protected BlockEntityRendererCompat(BlockEntityRendererProvider.Context context) {}
}
