package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.block.custom.sign.SignImageMigration;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.SerializableChunkData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 26.x removed ChunkSerializer; chunks are read by SerializableChunkData.read.
@Mixin(SerializableChunkData.class)
public abstract class LegacyRoadStateMixin {

    private static final String READ = "read(Lnet/minecraft/server/level/ServerLevel;"
        + "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;"
        + "Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;"
        + "Lnet/minecraft/world/level/ChunkPos;)"
        + "Lnet/minecraft/world/level/chunk/ProtoChunk;";

    @Inject(method = READ, at = @At("RETURN"), require = 0)
    private void yunbeiuc$migrateSigns(CallbackInfoReturnable<ProtoChunk> callbackInfo) {
        SignImageMigration.migrate(callbackInfo.getReturnValue());
    }
}
