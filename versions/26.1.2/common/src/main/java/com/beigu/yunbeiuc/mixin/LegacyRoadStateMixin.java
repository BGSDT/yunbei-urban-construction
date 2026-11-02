package com.beigu.yunbeiuc.mixin;

import org.spongepowered.asm.mixin.Mixin;

/**
 * Placeholder for the legacy road-state migration mixin.
 *
 * <p>26.x removed {@code ChunkSerializer}, which the original mixin targeted, so this version keeps
 * an empty mixin (the shared mixin config still lists it) until the migration logic is ported to
 * the new chunk serialization API.</p>
 */
@Mixin(net.minecraft.world.level.chunk.LevelChunk.class)
public abstract class LegacyRoadStateMixin {
}
