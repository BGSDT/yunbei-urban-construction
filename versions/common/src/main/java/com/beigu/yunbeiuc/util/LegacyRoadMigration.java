package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.api.nbt.NbtCompat;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Set;

// 旧存档道路方块迁移：把 facing 属性改写成 axis / mirrored。
public final class LegacyRoadMigration {

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

    public static void migrate(CompoundTag tag) {
        try {
            for (String sectionKey : new String[] {"sections", "Sections"}) {
                if (!NbtCompat.contains(tag, sectionKey, 9)) {
                    continue;
                }
                final ListTag sections = NbtCompat.getList(tag, sectionKey, 10);
                for (int i = 0; i < sections.size(); i++) {
                    final CompoundTag section = NbtCompat.compoundAt(sections, i);
                    for (String stateKey : new String[] {"block_states", "BlockStates"}) {
                        if (!NbtCompat.contains(section, stateKey, 10)) {
                            continue;
                        }
                        final CompoundTag states = NbtCompat.getCompound(section, stateKey);
                        for (String paletteKey : new String[] {"palette", "Palette"}) {
                            if (!NbtCompat.contains(states, paletteKey, 9)) {
                                continue;
                            }
                            migratePalette(NbtCompat.getList(states, paletteKey, 10));
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static void migratePalette(ListTag palette) {
        for (int j = 0; j < palette.size(); j++) {
            final CompoundTag entry = NbtCompat.compoundAt(palette, j);
            final String name = NbtCompat.getString(entry, "Name");
            if (!NbtCompat.contains(entry, "Properties", 10)) {
                continue;
            }
            final CompoundTag properties = NbtCompat.getCompound(entry, "Properties");
            if (!properties.contains("facing")) {
                continue;
            }
            final String facing = NbtCompat.getString(properties, "facing");
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

    private static String axisOf(String facing, boolean perpendicular) {
        final boolean northSouth = "north".equals(facing) || "south".equals(facing);
        return northSouth == perpendicular ? "x" : "z";
    }

    private static String mirroredOf(String facing) {
        return "south".equals(facing) || "west".equals(facing) ? "true" : "false";
    }

    private LegacyRoadMigration() {
    }
}
