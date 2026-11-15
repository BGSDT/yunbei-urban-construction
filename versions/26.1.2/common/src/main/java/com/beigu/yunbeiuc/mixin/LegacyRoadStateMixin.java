package com.beigu.yunbeiuc.mixin;

import org.spongepowered.asm.mixin.Mixin;

// 26.x removed ChunkSerializer (the original target), so this mixin is an empty placeholder
@Mixin(net.minecraft.world.level.chunk.LevelChunk.class)
public abstract class LegacyRoadStateMixin {
}
