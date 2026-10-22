package com.beigu.yunbeiuc.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.storage.ChunkSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ChunkSerializer.class)
public class LegacyRoadStateMixin {
    private static final String READ_OLD = "read(Lnet/minecraft/server/level/ServerLevel;"
        + "Lnet/minecraft/world/level/levelgen/structure/templatesystem/StructureManager;"
        + "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;"
        + "Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)"
        + "Lnet/minecraft/world/level/chunk/ProtoChunk;";

    private static final String READ_POI = "read(Lnet/minecraft/server/level/ServerLevel;"
        + "Lnet/minecraft/world/entity/ai/village/poi/PoiManager;"
        + "Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)"
        + "Lnet/minecraft/world/level/chunk/ProtoChunk;";

    private static final String READ_STORAGE = "read(Lnet/minecraft/server/level/ServerLevel;"
        + "Lnet/minecraft/world/level/chunk/storage/RegionStorageInfo;"
        + "Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/nbt/CompoundTag;)"
        + "Lnet/minecraft/world/level/chunk/ProtoChunk;";

    private static final Set<String> AXIS_ROADS = Set.of(
        "yunbeiuc:road_with_white_line",
        "yunbeiuc:road_with_white_double_line",
        "yunbeiuc:road_with_white_thick_line",
        "yunbeiuc:road_with_yellow_line",
        "yunbeiuc:road_with_yellow_double_line",
        "yunbeiuc:road_with_yellow_thick_line",
        "yunbeiuc:road_with_white_three_square_line",
        "yunbeiuc:road_with_white_six_square_line",
        "yunbeiuc:road_slab_with_white_line",
        "yunbeiuc:road_slab_with_white_double_line",
        "yunbeiuc:road_slab_with_white_thick_line",
        "yunbeiuc:road_slab_with_yellow_line",
        "yunbeiuc:road_slab_with_yellow_double_line",
        "yunbeiuc:road_slab_with_yellow_thick_line",
        "yunbeiuc:road_slab_with_white_three_square_line",
        "yunbeiuc:road_slab_with_white_six_square_line"
    );

    private static final Set<String> PERPENDICULAR_ROADS = Set.of(
        "yunbeiuc:road_with_white_tshape_line",
        "yunbeiuc:road_with_yellow_tshape_line",
        "yunbeiuc:road_with_white_tshape_double_line",
        "yunbeiuc:road_with_white_tshape_thick_line",
        "yunbeiuc:road_with_white_double_tshape_line",
        "yunbeiuc:road_with_white_thick_tshape_line",
        "yunbeiuc:road_with_white_tshape_yellow_line",
        "yunbeiuc:road_with_yellow_tshape_white_line",
        "yunbeiuc:road_with_white_tshape_yellowdouble_line",
        "yunbeiuc:road_with_white_thick_tshape_yellow_line",
        "yunbeiuc:road_with_whitethick_tshape_yellowdouble_line",
        "yunbeiuc:road_slab_with_white_tshape_line",
        "yunbeiuc:road_slab_with_yellow_tshape_line",
        "yunbeiuc:road_slab_with_white_tshape_double_line",
        "yunbeiuc:road_slab_with_white_tshape_thick_line",
        "yunbeiuc:road_slab_with_white_double_tshape_line",
        "yunbeiuc:road_slab_with_white_thick_tshape_line",
        "yunbeiuc:road_slab_with_white_tshape_yellow_line",
        "yunbeiuc:road_slab_with_yellow_tshape_white_line",
        "yunbeiuc:road_slab_with_white_tshape_yellowdouble_line",
        "yunbeiuc:road_slab_with_white_thick_tshape_yellow_line",
        "yunbeiuc:road_slab_with_whitethick_tshape_yellowdouble_line",
        "yunbeiuc:road_with_white_bevel_line",
        "yunbeiuc:road_with_white_bevel_double_line",
        "yunbeiuc:road_with_white_bevel_thick_line",
        "yunbeiuc:road_with_yellow_bevel_line",
        "yunbeiuc:road_with_yellow_bevel_double_line",
        "yunbeiuc:road_with_yellow_bevel_thick_line",
        "yunbeiuc:road_with_white_rightangle_line",
        "yunbeiuc:road_with_yellow_rightangle_line",
        "yunbeiuc:road_with_white_offset_in_rightangle_line",
        "yunbeiuc:road_with_yellow_offset_in_rightangle_line",
        "yunbeiuc:road_with_white_offset_out_rightangle_line",
        "yunbeiuc:road_with_yellow_offset_out_rightangle_line",
        "yunbeiuc:road_with_white_offset_in_bevel_rightangle_line",
        "yunbeiuc:road_with_yellow_offset_in_bevel_rightangle_line",
        "yunbeiuc:road_with_white_offset_out_bevel_rightangle_line",
        "yunbeiuc:road_with_yellow_offset_out_bevel_rightangle_line",
        "yunbeiuc:road_slab_with_white_bevel_line",
        "yunbeiuc:road_slab_with_white_bevel_double_line",
        "yunbeiuc:road_slab_with_white_bevel_thick_line",
        "yunbeiuc:road_slab_with_yellow_bevel_line",
        "yunbeiuc:road_slab_with_yellow_bevel_double_line",
        "yunbeiuc:road_slab_with_yellow_bevel_thick_line",
        "yunbeiuc:road_slab_with_white_rightangle_line",
        "yunbeiuc:road_slab_with_yellow_rightangle_line",
        "yunbeiuc:road_slab_with_white_offset_in_rightangle_line",
        "yunbeiuc:road_slab_with_yellow_offset_in_rightangle_line",
        "yunbeiuc:road_slab_with_white_offset_out_rightangle_line",
        "yunbeiuc:road_slab_with_yellow_offset_out_rightangle_line",
        "yunbeiuc:road_slab_with_white_offset_in_bevel_rightangle_line",
        "yunbeiuc:road_slab_with_yellow_offset_in_bevel_rightangle_line",
        "yunbeiuc:road_slab_with_white_offset_out_bevel_rightangle_line",
        "yunbeiuc:road_slab_with_yellow_offset_out_bevel_rightangle_line"
    );

    private static final Set<String> PARALLEL_ROADS = Set.of(
        "yunbeiuc:road_with_white_offset_line",
        "yunbeiuc:road_with_yellow_offset_line",
        "yunbeiuc:road_with_white_half_double_line",
        "yunbeiuc:road_with_yellow_half_double_line",
        "yunbeiuc:road_with_white_yellow_double_line",
        "yunbeiuc:road_slab_with_white_offset_line",
        "yunbeiuc:road_slab_with_yellow_offset_line",
        "yunbeiuc:road_slab_with_white_half_double_line",
        "yunbeiuc:road_slab_with_yellow_half_double_line",
        "yunbeiuc:road_slab_with_white_yellow_double_line"
    );

    @Inject(method = READ_OLD, at = @At("HEAD"), require = 0)
    private static void yunbeiuc$migrateOld(ServerLevel level, @Coerce Object structureManager,
                                            @Coerce Object poiManager, ChunkPos pos, CompoundTag tag,
                                            CallbackInfo callbackInfo) {
        migrate(tag);
    }

    @Inject(method = READ_POI, at = @At("HEAD"), require = 0)
    private static void yunbeiuc$migratePoi(ServerLevel level, @Coerce Object poiManager,
                                            ChunkPos pos, CompoundTag tag, CallbackInfo callbackInfo) {
        migrate(tag);
    }

    @Inject(method = READ_STORAGE, at = @At("HEAD"), require = 0)
    private static void yunbeiuc$migrateStorage(ServerLevel level, @Coerce Object storage,
                                                ChunkPos pos, CompoundTag tag, CallbackInfo callbackInfo) {
        migrate(tag);
    }

    private static void migrate(CompoundTag tag) {
        try {
            final ListTag sections = tag.getList("sections", 10);
            for (int i = 0; i < sections.size(); i++) {
                final CompoundTag section = sections.getCompound(i);
                if (!section.contains("block_states", 10)) {
                    continue;
                }
                final ListTag palette = section.getCompound("block_states").getList("palette", 10);
                for (int j = 0; j < palette.size(); j++) {
                    final CompoundTag entry = palette.getCompound(j);
                    final String name = entry.getString("Name");
                    if (!entry.contains("Properties", 10)) {
                        continue;
                    }
                    final CompoundTag properties = entry.getCompound("Properties");
                    if (!properties.contains("facing")) {
                        continue;
                    }
                    final String facing = properties.getString("facing");
                    if (AXIS_ROADS.contains(name) && !properties.contains("axis")) {
                        properties.remove("facing");
                        properties.putString("axis", "north".equals(facing) || "south".equals(facing) ? "z" : "x");
                    } else if (PERPENDICULAR_ROADS.contains(name) && !properties.contains("axis")) {
                        properties.remove("facing");
                        properties.putString("axis", axisOf(facing, true));
                        properties.putString("mirrored", mirroredOf(facing));
                    } else if (PARALLEL_ROADS.contains(name) && !properties.contains("axis")) {
                        properties.remove("facing");
                        properties.putString("axis", axisOf(facing, false));
                        properties.putString("mirrored", mirroredOf(facing));
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static String axisOf(String facing, boolean perpendicular) {
        final boolean northSouth = "north".equals(facing) || "south".equals(facing);
        return northSouth == perpendicular ? "x" : "z";
    }

    private static String mirroredOf(String facing) {
        return "south".equals(facing) || "west".equals(facing) ? "true" : "false";
    }
}
