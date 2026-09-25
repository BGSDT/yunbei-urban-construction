package dev.architectury.registry.client.rendering;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.level.block.Block;

/** 1.16.5 bridge for the renamed render type registry. */
public final class RenderTypeRegistry {
    private RenderTypeRegistry() {}
    public static void register(RenderType renderType, Block... blocks) {
        me.shedaniel.architectury.registry.RenderTypes.register(renderType, blocks);
    }
}
