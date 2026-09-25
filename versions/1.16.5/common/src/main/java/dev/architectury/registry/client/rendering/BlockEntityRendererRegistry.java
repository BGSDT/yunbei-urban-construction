package dev.architectury.registry.client.rendering;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.function.Function;

/** 1.16.5 bridge for the renamed renderer registry. */
public final class BlockEntityRendererRegistry {
    private BlockEntityRendererRegistry() {}

    public static <T extends BlockEntity> void register(
            BlockEntityType<T> type,
            Function<BlockEntityRendererProvider.Context, ? extends BlockEntityRenderer<? super T>> factory) {
        me.shedaniel.architectury.registry.BlockEntityRenderers.registerRenderer(
                type, dispatcher -> factory.apply(new BlockEntityRendererProvider.Context(dispatcher)));
    }
}
