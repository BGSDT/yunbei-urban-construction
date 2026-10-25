package com.beigu.yunbeiuc.util;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

import java.util.Set;

// 旧存档道路方块迁移：把 facing 属性改写成 axis / mirrored。
// 不能放在 com.beigu.yunbeiuc.mixin 包下：那个包被 yunbeiuc.mixins.json 声明为 mixin 专属包，
// 包内普通类被外部直接引用时 Mixin 会抛 IllegalClassLoadError。
// 真正做注入的是各版本目录下的 LegacyRoadStateMixin。
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
            // 1.18+ 用小写 sections / block_states / palette；
            // 1.16.5 ~ 1.17.1 用大写 Sections / BlockStates / Palette，两套都要读。
            for (String sectionKey : new String[] {"sections", "Sections"}) {
                if (!tag.contains(sectionKey, 9)) {
                    continue;
                }
                final ListTag sections = tag.getList(sectionKey, 10);
                for (int i = 0; i < sections.size(); i++) {
                    final CompoundTag section = sections.getCompound(i);
                    for (String stateKey : new String[] {"block_states", "BlockStates"}) {
                        if (!section.contains(stateKey, 10)) {
                            continue;
                        }
                        final CompoundTag states = section.getCompound(stateKey);
                        for (String paletteKey : new String[] {"palette", "Palette"}) {
                            if (!states.contains(paletteKey, 9)) {
                                continue;
                            }
                            migratePalette(states.getList(paletteKey, 10));
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static void migratePalette(ListTag palette) {
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
