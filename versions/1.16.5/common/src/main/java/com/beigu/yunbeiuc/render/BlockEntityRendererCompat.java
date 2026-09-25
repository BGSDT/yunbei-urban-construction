package com.beigu.yunbeiuc.render;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;

/** 1.16.5 renderer base, where BlockEntityRenderer is an abstract class. */
public abstract class BlockEntityRendererCompat<T extends BlockEntity> extends BlockEntityRenderer<T> {
    protected BlockEntityRendererCompat(BlockEntityRendererProvider.Context context) {
        super(context.dispatcher());
    }
}
