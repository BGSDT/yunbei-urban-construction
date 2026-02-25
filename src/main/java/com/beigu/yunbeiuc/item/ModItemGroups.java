package com.beigu.yunbeiuc.item;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.ModBlocks;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {

    public static final ItemGroup YUNBEIUC_ROAD_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(YunbeiUrbanConstruction.MOD_ID, "road"),
            ItemGroup.create(null, -1)
                    .displayName(Text.translatable("itemGroup.yunbeicu.road_blocks"))
                    .icon(() -> new ItemStack(ModBlocks.ROAD_WITH_WHITE_DOUBLE_LINE))
                    .entries((displayContext, entries) -> {
                        // 地面标识类方块
                        entries.add(ModBlocks.LEFT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.STRAIGHT_GROUND_MARK);
                        entries.add(ModBlocks.RIGHT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.LEFT_TURN_AROUND_GROUND_MARK);
                        entries.add(ModBlocks.RIGHT_TURN_AROUND_GROUND_MARK);
                        entries.add(ModBlocks.STRAIGHT_LEFT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.STRAIGHT_RIGHT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.STRAIGHT_LEFT_RIGHT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.LEFT_TURN_MERGE_GROUND_MARK);
                        entries.add(ModBlocks.RIGHT_TURN_MERGE_GROUND_MARK);
                        entries.add(ModBlocks.LEFT_TURN_AROUND_SINGLE_GROUND_MARK);
                        entries.add(ModBlocks.RIGHT_TURN_AROUND_SINGLE_GROUND_MARK);
                        entries.add(ModBlocks.SLOWDOWN_ANNOUNCEMENT_GROUND_MARK);
                        entries.add(ModBlocks.SLOWDOWN_YIELD_GROUND_MARK);
                        entries.add(ModBlocks.MANHOLE_COVER);

// 交通灯类方块
                        entries.add(ModBlocks.TRAFFIC_LIGHTS_STRAIGHT);
                        entries.add(ModBlocks.TRAFFIC_LIGHTS_LEFT);
                        entries.add(ModBlocks.TRAFFIC_LIGHTS_PAVEMENT);

// 花箱类方块
                        entries.add(ModBlocks.ROAD_FLOWER_BOX_1);
                        entries.add(ModBlocks.ROAD_FLOWER_BOX_2);
                        entries.add(ModBlocks.ROAD_FLOWER_BOX_2_FENCE);

// 检测/照明类方块
                        entries.add(ModBlocks.ROAD_DETECTION_CAMERA);
                        entries.add(ModBlocks.ROADWAY_LIGHTING_LAMP);
                        entries.add(ModBlocks.RADAR_SPEED_DETECTOR);

// 路障/防撞类方块
                        entries.add(ModBlocks.TRAFFIC_CONE);
                        entries.add(ModBlocks.ROAD_COLLISION_BARREL);
                        entries.add(ModBlocks.WATER_SAFETY_BARRIER_RED);

// 垃圾桶类方块
                        entries.add(ModBlocks.RUBBISH_BIN_WHITE);
                        entries.add(ModBlocks.RUBBISH_BIN_GRAY_GREEN);

// 减速类方块
                        entries.add(ModBlocks.SPEED_BUMP);

// 振动标线类方块
                        entries.add(ModBlocks.VIBRATION_MARKING_LINE);

// 停车位挡块类方块
                        entries.add(ModBlocks.PARKING_SPACE_BARRIER);

// 交通标识类方块
                        entries.add(ModBlocks.SIGN_SPEED_LIMIT_BLOCK);
                        entries.add(ModBlocks.SIGN_CANCEL_SPEED_LIMIT_BLOCK);
                        entries.add(ModBlocks.SIGN_NO_ENTRY_FOR_VEHICLES_BLOCK);
                        entries.add(ModBlocks.SIGN_NO_DIRECTION_BLOCK);
                        entries.add(ModBlocks.SIGN_HEIGHT_LIMIT_BLOCK);
                        entries.add(ModBlocks.SIGN_WIDTH_LIMIT_BLOCK);
                        entries.add(ModBlocks.SIGN_WEIGHT_LIMIT_BLOCK);
                        entries.add(ModBlocks.SIGN_NO_SPECIAL_BLOCK);
                        entries.add(ModBlocks.SIGN_INDICATION_DIRECTION_BLOCK);
                        entries.add(ModBlocks.SIGN_INDICATION_LANE_DIRECTION_BLOCK);
                        entries.add(ModBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_BLOCK);

// 龙门架类方块
                        entries.add(ModBlocks.GANTRY_FRAME_SIDE);
                        entries.add(ModBlocks.GANTRY_FRAME_CONNECTION);
                        entries.add(ModBlocks.GANTRY_FRAME_MAIN);
                        entries.add(ModBlocks.GANTRY_FRAME_RAILING);
                        entries.add(ModBlocks.GANTRY_FRAME_RAILING_LADDER);
                        entries.add(ModBlocks.GANTRY_FRAME_LED_SIDE);
                        entries.add(ModBlocks.GANTRY_FRAME_LED_MAIN);

// 防眩网类方块
                        entries.add(ModBlocks.ANTI_GLARE_NET);
                        entries.add(ModBlocks.ANTI_GLARE_NET_POLE);
                        entries.add(ModBlocks.ANTI_GLARE_VERSION);

// 交通护栏类方块
                        entries.add(ModBlocks.TRAFFIC_BARRIER);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_YELLOW_DOUBLE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_YELLOW);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_RED);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_RED_DOUBLE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_OBLIQUE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_OBLIQUE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_YELLOW);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_RED);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_OBLIQUE);

// 道路警示柱类方块
                        entries.add(ModBlocks.ROAD_WARNING_POLE_RED);
                        entries.add(ModBlocks.ROAD_WARNING_POLE_YELLOW);
                        entries.add(ModBlocks.ROAD_WARNING_POLE_GREEN);

// 铁马类方块
                        entries.add(ModBlocks.IRON_HORSE_YELLOW);
                        entries.add(ModBlocks.IRON_HORSE_RED);
                        entries.add(ModBlocks.IRON_HORSE_WHITE);
                        entries.add(ModBlocks.IRON_HORSE_GRAY);

// 反光标识类方块
                        entries.add(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_1);
                        entries.add(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_2);
                        entries.add(ModBlocks.REFLECTIVE_SIGN_RED_ALL_1);
                        entries.add(ModBlocks.REFLECTIVE_SIGN_RED_ALL_2);

// 仪器杆类方块
                        entries.add(ModBlocks.INSTRUMENT_POLE_FOUNDATIONS);
                        entries.add(ModBlocks.INSTRUMENT_POLE_LONGITUDINAL);
                        entries.add(ModBlocks.INSTRUMENT_CAMERA);

// 安全岛类方块
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_1);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_2);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_3);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_4);
                        entries.add(ModBlocks.SAFETY_ISLAND_GRAY);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_1);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_2);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_1);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_2);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_3);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_4);
                        entries.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_1);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_2);
                        entries.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB);

// 道路栏杆类方块
                        entries.add(ModBlocks.ROAD_RAILINGS_IRON);
                        entries.add(ModBlocks.ROAD_RAILINGS_IRON_ENDING_1);
                        entries.add(ModBlocks.ROAD_RAILINGS_IRON_ENDING_2);
                        entries.add(ModBlocks.ROAD_RAILINGS_IRON_POLE);
                        entries.add(ModBlocks.ROAD_RAILINGS_IRON_OBLIQUE);
                        entries.add(ModBlocks.ROAD_RAILINGS_GREEN);
                        entries.add(ModBlocks.ROAD_RAILINGS_GREEN_ENDING_1);
                        entries.add(ModBlocks.ROAD_RAILINGS_GREEN_ENDING_2);
                        entries.add(ModBlocks.ROAD_RAILINGS_GREEN_POLE);
                        entries.add(ModBlocks.ROAD_RAILINGS_GREEN_OBLIQUE);

// 道路封闭护栏类方块
                        entries.add(ModBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_1);
                        entries.add(ModBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_2);

// 道路杆件类方块
                        entries.add(ModBlocks.ROAD_POLES_FOUNDATIONS);
                        entries.add(ModBlocks.ROAD_POLES_LONGITUDINAL);
                        entries.add(ModBlocks.ROAD_POLES_HORIZONTAL);
                        entries.add(ModBlocks.ROAD_POLES_TSHAPE);
                        entries.add(ModBlocks.ROAD_LIGHT);

// 道路标线类方块
                        entries.add(ModBlocks.ROAD_BLOCK);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_CROSS_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_CROSS_LINE);
                        entries.add(ModBlocks.ROAD_WITH_AUTO_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE);

// 道路杆件展示类方块
                        entries.add(ModBlocks.ROAD_POLES_TEXT_DISPLAY);
                        entries.add(ModBlocks.ROAD_POLES_FLAG);
                    }).build());

    public static void registerGroups() {

    }
}