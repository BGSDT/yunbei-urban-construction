package com.beigu.yunbeiuc.mixin;

import com.beigu.yunbeiuc.util.LegacyRoadMigration;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

// 1.18.2 ~ 1.20.4：read 只带 PoiManager（1.21.1 起多带 RegionStorageInfo）。
// read(...) 返回 ProtoChunk，所以回调必须收 CallbackInfoReturnable，否则运行时报 InvalidInjectionException。
@Mixin(ChunkSerializer.class)
public class LegacyRoadStateMixin {

    private static final String READ = "read(Lnet/minecraft/server/level/ServerLevel;"
        + "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;"
        + "Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)"
        + "Lnet/minecraft/world/level/chunk/ProtoChunk;";

    @Inject(method = READ, at = @At("HEAD"), require = 0)
    private static void yunbeiuc$migrate(ServerLevel level, @Coerce Object poiManager, ChunkPos pos, CompoundTag tag,
                                         CallbackInfoReturnable<ProtoChunk> callbackInfo) {
        LegacyRoadMigration.migrate(tag);
    }
}
