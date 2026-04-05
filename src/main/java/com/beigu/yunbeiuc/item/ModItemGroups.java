package com.beigu.yunbeiuc.item;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
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
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;


public class ModItemGroups {

    public static final ItemGroup YUNBEIUC_ROAD_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(YunbeiUrbanConstruction.MOD_ID, "rb"),
            ItemGroup.create(null, -1)
                    .displayName(Text.translatable("itemGroup.yunbeiuc_rb_group"))
                    .icon(() -> new ItemStack(ModBlocks.ROAD_WITH_WHITE_DOUBLE_LINE))
                    .entries((displayContext, entries) -> {
                        entries.add(ModItems.WAND);
                        entries.add(ModItems.TREE_WAND);
                        entries.add(ModItems.WATER_WAND);
                        entries.add(ModItems.ROTATED_WAND);
                        entries.add(ModItems.LINK_WAND);

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
                        entries.add(ModBlocks.ROAD_LIGHTING_LAMP);
                        entries.add(ModBlocks.ROAD_RADAR_SPEED_DETECTOR);

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

                        entries.add(ModBlocks.GANTRY_FRAME_SIDE);
                        entries.add(ModBlocks.GANTRY_FRAME_CONNECTION);
                        entries.add(ModBlocks.GANTRY_FRAME_MAIN);
                        entries.add(ModBlocks.GANTRY_FRAME_RAILING);
                        entries.add(ModBlocks.GANTRY_FRAME_LADDER);
                        entries.add(ModBlocks.GANTRY_FRAME_LED_SIDE);
                        entries.add(ModBlocks.GANTRY_FRAME_LED_MAIN);
                        entries.add(ModBlocks.GANTRY_FRAME_LED);
                        entries.add(ModBlocks.GANTRY_FRAME_DETECTION_CAMERA);
                        entries.add(ModBlocks.GANTRY_FRAME_LIGHTING_LAMP);
                        entries.add(ModBlocks.GANTRY_FRAME_RADAR_SPEED_DETECTOR);

                        entries.add(ModBlocks.HEIGHT_LIMIT_BARRIER_SIDE);
                        entries.add(ModBlocks.HEIGHT_LIMIT_BARRIER_CONNECTION);
                        entries.add(ModBlocks.HEIGHT_LIMIT_BARRIER_MAIN);

                        entries.add(ModBlocks.ANTI_GLARE_NET);
                        entries.add(ModBlocks.ANTI_GLARE_NET_POLE);
                        entries.add(ModBlocks.ANTI_GLARE_VERSION);

                        entries.add(ModBlocks.TRAFFIC_BARRIER);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_YELLOW_DOUBLE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_YELLOW);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_RED);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_RED_DOUBLE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_OBLIQUE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_OBLIQUE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_RED);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_RED_OBLIQUE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_YELLOW);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_YELLOW_OBLIQUE);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_YELLOW);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_RED);
                        entries.add(ModBlocks.TRAFFIC_BARRIER_GRAY_SLANT_OBLIQUE);

                        entries.add(ModBlocks.ROAD_WARNING_POLE_RED);
                        entries.add(ModBlocks.ROAD_WARNING_POLE_YELLOW);
                        entries.add(ModBlocks.ROAD_WARNING_POLE_GREEN);

                        entries.add(ModBlocks.IRON_HORSE_YELLOW);
                        entries.add(ModBlocks.IRON_HORSE_RED);
                        entries.add(ModBlocks.IRON_HORSE_WHITE);
                        entries.add(ModBlocks.IRON_HORSE_GRAY);

                        entries.add(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_1);
                        entries.add(ModBlocks.REFLECTIVE_SIGN_YELLOW_ALL_2);
                        entries.add(ModBlocks.REFLECTIVE_SIGN_RED_ALL_1);
                        entries.add(ModBlocks.REFLECTIVE_SIGN_RED_ALL_2);

                        entries.add(ModBlocks.INSTRUMENT_POLE_FOUNDATIONS);
                        entries.add(ModBlocks.INSTRUMENT_POLE_LONGITUDINAL);
                        entries.add(ModBlocks.INSTRUMENT_CAMERA);
                        entries.add(ModBlocks.INSTRUMENT_FEE_DISPLAY);
                        entries.add(ModBlocks.INSTRUMENT_LANE_INDICATOR);

                        entries.add(ModBlocks.BARRIER_GATE_1_MAIN);
                        entries.add(ModBlocks.BARRIER_GATE_1_MAIN_SLAB);
                        entries.add(ModBlocks.BARRIER_GATE_1_POLE_HORIZONTAL);
                        entries.add(ModBlocks.BARRIER_GATE_1_POLE_LONGITUDINAL);

                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_1);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_2);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_3);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_4);
                        entries.add(ModBlocks.SAFETY_ISLAND_GRAY);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_1);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_OBLIQUE_2);
                        entries.add(ModBlocks.SAFETY_ISLAND_GRAY_OBLIQUE);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_1);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_2);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_3);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_4);
                        entries.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_1);
                        entries.add(ModBlocks.SAFETY_ISLAND_YELLOW_SLAB_EDGE_OBLIQUE_2);
                        entries.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB);
                        entries.add(ModBlocks.SAFETY_ISLAND_GRAY_SLAB_EDGE_OBLIQUE);

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

                        entries.add(ModBlocks.WARNING_NETWORK);
                        entries.add(ModBlocks.WARNING_NETWORK_POLE);

                        entries.add(ModBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_1);
                        entries.add(ModBlocks.ROAD_CLOSED_BARRICADE_GUARDRAIL_2);

                        entries.add(ModBlocks.ROAD_POLE_FOUNDATIONS);
                        entries.add(ModBlocks.ROAD_POLE_LONGITUDINAL);
                        entries.add(ModBlocks.ROAD_POLE_HORIZONTAL);
                        entries.add(ModBlocks.ROAD_POLE_TSHAPE);
                        entries.add(ModBlocks.ROAD_LIGHT);
                        entries.add(ModBlocks.ROAD_POLE_TEXT_DISPLAY);
                        entries.add(ModBlocks.ROAD_POLE_FLAG);

                        entries.add(ModBlocks.ROAD_BLOCK);
                        entries.add(ModBlocks.ROAD_FULL_OF_WHITE);
                        entries.add(ModBlocks.ROAD_FULL_OF_YELLOW);
                        entries.add(ModBlocks.ROAD_WHITE_YELLOW);
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
                        entries.add(ModBlocks.ROAD_WITH_WHITE_LEFT_DIAMOND_SHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_RIGHT_DIAMOND_SHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_DOUBLE_DIAMOND_SHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_LEFT_DIAMOND_SHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_DOUBLE_DIAMOND_SHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_YELLOW_RIGHT_DIAMOND_SHAPE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_THREE_SQUARE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_SIX_SQUARE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_WHITE_NINE_SQUARE_LINE);
                        entries.add(ModBlocks.ROAD_WITH_AUTO_BEVEL_LINE);
                        entries.add(ModBlocks.ROAD_WITH_AUTO_RIGHTANGLE_LINE);

                        // 地面标识类方块
                        entries.add(ModBlocks.STRAIGHT_GROUND_MARK);
                        entries.add(ModBlocks.STRAIGHT_LEFT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.LEFT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.STRAIGHT_RIGHT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.RIGHT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.STRAIGHT_LEFT_RIGHT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.LEFT_TURN_AROUND_SINGLE_GROUND_MARK);
                        entries.add(ModBlocks.STRAIGHT_LEFT_TURN_AROUND_GROUND_MARK);
                        entries.add(ModBlocks.LEFT_TURN_AROUND_GROUND_MARK);
                        entries.add(ModBlocks.LEFT_RIGHT_TURN_GROUND_MARK);
                        entries.add(ModBlocks.LEFT_TURN_MERGE_GROUND_MARK);
                        entries.add(ModBlocks.RIGHT_TURN_MERGE_GROUND_MARK);
                        entries.add(ModBlocks.SLOWDOWN_ANNOUNCEMENT_GROUND_MARK);
                        entries.add(ModBlocks.DISTANCE_CONFIRMATION_GROUND_MARK);
                        entries.add(ModBlocks.TAXI_1_GROUND_MARK);
                        entries.add(ModBlocks.TAXI_2_GROUND_MARK);
                        entries.add(ModBlocks.VEHICLE_GROUND_MARK);
                        entries.add(ModBlocks.DISABLED_PEOPLE_GROUND_MARK);
                        entries.add(ModBlocks.NON_MOTOR_VEHICLES_GROUND_MARK);
                        entries.add(ModBlocks.BUS_1_GROUND_MARK);
                        entries.add(ModBlocks.BUS_2_GROUND_MARK);
                        entries.add(ModBlocks.SCHOOL_BUS_1_GROUND_MARK);
                        entries.add(ModBlocks.LARGE_SPEED_BUMP_GROUND_MARK);
                        entries.add(ModBlocks.SMALL_SPEED_BUMP_GROUND_MARK);
                        entries.add(ModBlocks.WHITE_040_GROUND_MARK);
                        entries.add(ModBlocks.WHITE_050_GROUND_MARK);
                        entries.add(ModBlocks.WHITE_060_GROUND_MARK);
                        entries.add(ModBlocks.WHITE_070_GROUND_MARK);
                        entries.add(ModBlocks.WHITE_080_GROUND_MARK);
                        entries.add(ModBlocks.WHITE_090_GROUND_MARK);
                        entries.add(ModBlocks.WHITE_100_GROUND_MARK);
                        entries.add(ModBlocks.WHITE_110_GROUND_MARK);
                        entries.add(ModBlocks.WHITE_120_GROUND_MARK);
                        entries.add(ModBlocks.YELLOW_040_GROUND_MARK);
                        entries.add(ModBlocks.YELLOW_050_GROUND_MARK);
                        entries.add(ModBlocks.YELLOW_060_GROUND_MARK);
                        entries.add(ModBlocks.YELLOW_070_GROUND_MARK);
                        entries.add(ModBlocks.YELLOW_080_GROUND_MARK);
                        entries.add(ModBlocks.YELLOW_090_GROUND_MARK);
                        entries.add(ModBlocks.YELLOW_100_GROUND_MARK);
                        entries.add(ModBlocks.YELLOW_110_GROUND_MARK);
                        entries.add(ModBlocks.YELLOW_120_GROUND_MARK);
                        entries.add(ModBlocks.ELECTRIC_VEHICLE_GROUND_MARK);
                        entries.add(ModBlocks.PEDESTRIAN_GROUND_MARK);
                        entries.add(ModBlocks.STOP_AND_YIELD_GROUND_MARK);
                        entries.add(ModBlocks.HOV_1_GROUND_MARK);
                        entries.add(ModBlocks.HOV_2_GROUND_MARK);
                        entries.add(ModBlocks.HOV_3_GROUND_MARK);
                        entries.add(ModBlocks.HOV_4_GROUND_MARK);
                        entries.add(ModBlocks.HOV_5_GROUND_MARK);
                        entries.add(ModBlocks.MANHOLE_COVER);
                    }).build());

    public static final ItemGroup YUNBEIUC_SIGN_GROUP = Registry.register(
            Registries.ITEM_GROUP,
            new Identifier(YunbeiUrbanConstruction.MOD_ID, "sings"),
            ItemGroup.create(null, -1)
                    .displayName(Text.translatable("itemGroup.yunbeiuc_sings_group"))
                    .icon(() -> new ItemStack(SignBlocks.SIGN_SPEED_LIMIT_005))
                    .entries((displayContext, entries) -> {
                        entries.add(SignBlocks.SIGN_STOP);
                        entries.add(SignBlocks.SIGN_YIELD);
                        entries.add(SignBlocks.SIGN_YIELD_TO_ONCOMING_TRAFFIC);
                        entries.add(SignBlocks.SIGN_NO_ALL);
                        entries.add(SignBlocks.SIGN_NO_ENTRY);
                        entries.add(SignBlocks.SIGN_NO_MOTOR_VEHICLES);
                        entries.add(SignBlocks.SIGN_NO_LARGE_BUS);
                        entries.add(SignBlocks.SIGN_NO_SMALL_PASSENGER_CAR);
                        entries.add(SignBlocks.SIGN_NO_TRUCK);
                        entries.add(SignBlocks.SIGN_NO_TRAILER);
                        entries.add(SignBlocks.SIGN_NO_TRACTOR);
                        entries.add(SignBlocks.SIGN_NO_THREE_WHEELED_VEHICLE);
                        entries.add(SignBlocks.SIGN_NO_MOTORCYCLE);
                        entries.add(SignBlocks.SIGN_NO_NON_MOTOR_VEHICLES);
                        entries.add(SignBlocks.SIGN_NO_ELECTRIC_VEHICLE);
                        entries.add(SignBlocks.SIGN_NO_ANIMAL_DRAWN_CART);
                        entries.add(SignBlocks.SIGN_NO_TRICYCLE);
                        entries.add(SignBlocks.SIGN_NO_HUMAN_POWERED_PASSENGER_TRICYCLE);
                        entries.add(SignBlocks.SIGN_NO_HUMAN_POWERED_CARGO_TRICYCLE);
                        entries.add(SignBlocks.SIGN_NO_RICKSHAW);
                        entries.add(SignBlocks.SIGN_NO_PEDESTRIAN);
                        entries.add(SignBlocks.SIGN_NO_LEFT_TURN);
                        entries.add(SignBlocks.SIGN_NO_STRAIGHT);
                        entries.add(SignBlocks.SIGN_NO_RIGHT_TURN);
                        entries.add(SignBlocks.SIGN_NO_LEFT_RIGHT_TURN);
                        entries.add(SignBlocks.SIGN_NO_STRAIGHT_LEFT_TURN);
                        entries.add(SignBlocks.SIGN_NO_STRAIGHT_RIGHT_TURN);
                        entries.add(SignBlocks.SIGN_NO_SINGLE_LEFT_TURN_AROUND);
                        entries.add(SignBlocks.SIGN_NO_OVERTAKE);
                        entries.add(SignBlocks.SIGN_CANCEL_OVERTAKE);
                        entries.add(SignBlocks.SIGN_NO_PARKING);
                        entries.add(SignBlocks.SIGN_NO_PARKING_LONG_TIME);
                        entries.add(SignBlocks.SIGN_NO_HONK_HORN);
                        entries.add(SignBlocks.SIGN_WIDTH_LIMIT_20);
                        entries.add(SignBlocks.SIGN_WIDTH_LIMIT_25);
                        entries.add(SignBlocks.SIGN_WIDTH_LIMIT_30);
                        entries.add(SignBlocks.SIGN_WIDTH_LIMIT_35);
                        entries.add(SignBlocks.SIGN_WIDTH_LIMIT_40);
                        entries.add(SignBlocks.SIGN_WIDTH_LIMIT_45);
                        entries.add(SignBlocks.SIGN_HEIGHT_LIMIT_20);
                        entries.add(SignBlocks.SIGN_HEIGHT_LIMIT_25);
                        entries.add(SignBlocks.SIGN_HEIGHT_LIMIT_30);
                        entries.add(SignBlocks.SIGN_HEIGHT_LIMIT_35);
                        entries.add(SignBlocks.SIGN_HEIGHT_LIMIT_40);
                        entries.add(SignBlocks.SIGN_HEIGHT_LIMIT_45);
                        entries.add(SignBlocks.SIGN_WEIGHT_LIMIT_10);
                        entries.add(SignBlocks.SIGN_WEIGHT_LIMIT_20);
                        entries.add(SignBlocks.SIGN_WEIGHT_LIMIT_30);
                        entries.add(SignBlocks.SIGN_WEIGHT_LIMIT_40);
                        entries.add(SignBlocks.SIGN_ALEX_WEIGHT_LIMIT_10);
                        entries.add(SignBlocks.SIGN_ALEX_WEIGHT_LIMIT_20);
                        entries.add(SignBlocks.SIGN_ALEX_WEIGHT_LIMIT_30);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_005);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_010);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_020);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_030);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_040);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_050);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_060);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_070);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_080);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_090);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_100);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_110);
                        entries.add(SignBlocks.SIGN_SPEED_LIMIT_120);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_005);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_010);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_020);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_030);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_040);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_050);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_060);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_070);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_080);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_090);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_100);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_110);
                        entries.add(SignBlocks.SIGN_CANCEL_SPEED_LIMIT_120);
                        entries.add(SignBlocks.SIGN_CHECK);
                        entries.add(SignBlocks.SIGN_PORT_CHECK);
                        entries.add(SignBlocks.SIGN_NO_HAZARDOUS_MATERIALS_TRANSPORT_VEHICLE);

                        entries.add(SignBlocks.SIGN_INDICATION_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LEFT_TURN);
                        entries.add(SignBlocks.SIGN_INDICATION_RIGHT_TURN);
                        entries.add(SignBlocks.SIGN_INDICATION_STRAIGHT_LEFT_TURN);
                        entries.add(SignBlocks.SIGN_INDICATION_STRAIGHT_RIGHT_TURN);
                        entries.add(SignBlocks.SIGN_INDICATION_LEFT_RIGHT_TURN);
                        entries.add(SignBlocks.SIGN_INDICATION_RIGHT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LEFT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_ROUNDABOUT);
                        entries.add(SignBlocks.SIGN_INDICATION_ONE_WAY_STREET_LEFT_RIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_ONE_WAY_STREET_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_HONK_HORN);
                        entries.add(SignBlocks.SIGN_INDICATION_HEADLIGHTS);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_005);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_010);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_020);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_030);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_040);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_050);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_060);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_070);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_080);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_090);
                        entries.add(SignBlocks.SIGN_MIN_SPEED_LIMIT_100);
                        entries.add(SignBlocks.SIGN_INDICATION_YIELD_TO_ONCOMING_TRAFFIC);
                        entries.add(SignBlocks.SIGN_INDICATION_ZEBRA_CROSSING);
                        entries.add(SignBlocks.SIGN_INDICATION_ZEBRA_CROSSING_FLUORESCENCE);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_RIGHT_TURN);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LEFT_TURN);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_STRAIGHT_RIGHT_TURN);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_STRAIGHT_LEFT_TURN);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SINGLE_LEFT_TURN_AROUND);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LEFT_TURN_AROUND);
                        entries.add(SignBlocks.SIGN_INDICATION_MOTOR_VEHICLES);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES_LEFT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES_RIGHT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR_LEFT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR_RIGHT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_LEFT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_RIGHT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT_LEFT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT_RIGHT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT_LEFT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT_RIGHT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_TRAM_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_HOV);
                        entries.add(SignBlocks.SIGN_INDICATION_NON_MOTOR_VEHICLES);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES_LEFT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES_RIGHT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_ELECTRIC_VEHICLE);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE_STRAIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE_LEFT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE_RIGHT_SIDE_MEDIAN_STRIP);
                        entries.add(SignBlocks.SIGN_INDICATION_PEDESTRIAN);
                        entries.add(SignBlocks.SIGN_INDICATION_PEDESTRIAN_NON_MOTOR_VEHICLES_1);
                        entries.add(SignBlocks.SIGN_INDICATION_PEDESTRIAN_NON_MOTOR_VEHICLES_2);
                        entries.add(SignBlocks.SIGN_INDICATION_PEDESTRIAN_NON_MOTOR_VEHICLES_3);
                        entries.add(SignBlocks.SIGN_INDICATION_PROMOTION_NON_MOTOR_VEHICLES);
                        entries.add(SignBlocks.SIGN_INDICATION_PROMOTION_DRIVE_RIGHT);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_1);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_2);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_3);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_4);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_DISABLED);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_SCHOOL_BUS);
                        entries.add(SignBlocks.SIGN_INDICATION_SCHOOL_BUS_WUXI);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_SCHOOL_BUS_FLUORESCENCE);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_TAXI);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_NON_MOTOR_VEHICLE);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_BUS);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_CHARGING);
                        entries.add(SignBlocks.SIGN_INDICATION_PARKING_COMPANY);
                        entries.add(SignBlocks.SIGN_INDICATION_TURN_AROUND);
                        entries.add(SignBlocks.SIGN_INDICATION_PAVED_SHOULDER_1);
                        entries.add(SignBlocks.SIGN_INDICATION_PAVED_SHOULDER_2);
                        entries.add(SignBlocks.SIGN_INDICATION_PAVED_SHOULDER_3);
                        entries.add(SignBlocks.SIGN_INDICATION_OK_TRUCK);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_1);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_2);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_3);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_4);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_5);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_6);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_7);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_8);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_9);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_10);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSING_11);
                        entries.add(SignBlocks.SIGN_WARNING_SHARP_TURN_1);
                        entries.add(SignBlocks.SIGN_WARNING_SHARP_TURN_2);
                        entries.add(SignBlocks.SIGN_WARNING_REVERSE_DETOUR_1);
                        entries.add(SignBlocks.SIGN_WARNING_REVERSE_DETOUR_2);
                        entries.add(SignBlocks.SIGN_WARNING_CONTINUOUS_WINDING_ROADS_1);
                        entries.add(SignBlocks.SIGN_WARNING_CONTINUOU_WINDING_ROADS_2);
                        entries.add(SignBlocks.SIGN_WARNING_STEEP_SLOPE_DOWN);
                        entries.add(SignBlocks.SIGN_WARNING_STEEP_SLOPE_UP);
                        entries.add(SignBlocks.SIGN_WARNING_CONTINUOUS_DOWNHILL);
                        entries.add(SignBlocks.SIGN_WARNING_NARROW_ROAD_DOUBLE);
                        entries.add(SignBlocks.SIGN_WARNING_NARROW_ROAD_LEFT);
                        entries.add(SignBlocks.SIGN_WARNING_NARROW_ROAD_RIGHT);
                        entries.add(SignBlocks.SIGN_WARNING_NARROW_BRIDGE);
                        entries.add(SignBlocks.SIGN_WARNING_TWO_WAY_TRAFFIC);
                        entries.add(SignBlocks.SIGN_WARNING_ZEBRA_CROSSING);
                        entries.add(SignBlocks.SIGN_WARNING_ZEBRA_CROSSING_FLUORESCENCE);
                        entries.add(SignBlocks.SIGN_WARNING_CHILDREN);
                        entries.add(SignBlocks.SIGN_WARNING_CHILDREN_FLUORESCENCE);
                        entries.add(SignBlocks.SIGN_WARNING_DISABLED);
                        entries.add(SignBlocks.SIGN_WARNING_DISABLED_FLUORESCENCE);
                        entries.add(SignBlocks.SIGN_WARNING_NON_MOTOR_VEHICLES);
                        entries.add(SignBlocks.SIGN_WARNING_ELECTRIC_VEHICLE);
                        entries.add(SignBlocks.SIGN_WARNING_LIVESTOCK);
                        entries.add(SignBlocks.SIGN_WARNING_WILDLIFE);
                        entries.add(SignBlocks.SIGN_WARNING_TRAFFIC_LIGHTS);
                        entries.add(SignBlocks.SIGN_WARNING_FALLING_ROCKS_LEFT);
                        entries.add(SignBlocks.SIGN_WARNING_FALLING_ROCKS_RIGHT);
                        entries.add(SignBlocks.SIGN_WARNING_CROSSWIND);
                        entries.add(SignBlocks.SIGN_WARNING_SLIPPERY);
                        entries.add(SignBlocks.SIGN_WARNING_PERILOUS_ROAD_ALONG_THE_MOUNTAIN_LEFT);
                        entries.add(SignBlocks.SIGN_WARNING_PERILOUS_ROAD_ALONG_THE_MOUNTAIN_RIGHT);
                        entries.add(SignBlocks.SIGN_WARNING_EMBANKMENT_ROAD_LEFT);
                        entries.add(SignBlocks.SIGN_WARNING_EMBANKMENT_ROAD_RIGHT);
                        entries.add(SignBlocks.SIGN_WARNING_VILLAGE);
                        entries.add(SignBlocks.SIGN_WARNING_TUNNEL);
                        entries.add(SignBlocks.SIGN_WARNING_CAMEL_BACK_BRIDGE);
                        entries.add(SignBlocks.SIGN_WARNING_ROAD_UNEVEN);
                        entries.add(SignBlocks.SIGN_WARNING_SPEED_BUMP);
                        entries.add(SignBlocks.SIGN_WARNING_ROAD_WET);
                        entries.add(SignBlocks.SIGN_WARNING_SOMEONE_GUARDING_THE_RAILWAY_CROSSING);
                        entries.add(SignBlocks.SIGN_WARNING_UNMANNED_GUARDING_THE_RAILWAY_CROSSING);
                        entries.add(SignBlocks.SIGN_WARNING_ACCIDENT_PRONE_ROAD);
                        entries.add(SignBlocks.SIGN_WARNING_DETOUR_DOUBLE);
                        entries.add(SignBlocks.SIGN_WARNING_DETOUR_LEFT);
                        entries.add(SignBlocks.SIGN_WARNING_DETOUR_RIGHT);
                        entries.add(SignBlocks.SIGN_WARNING_DANGEROUS);
                        entries.add(SignBlocks.SIGN_WARNING_CONSTRUCTION);
                        entries.add(SignBlocks.SIGN_WARNING_ACCIDENT);
                        entries.add(SignBlocks.SIGN_WARNING_TIDAL_LANE);
                        entries.add(SignBlocks.SIGN_WARNING_DISTANCE_BETWEEN_VEHICLES);
                        entries.add(SignBlocks.SIGN_WARNING_CONFLUENCE_LEFT);
                        entries.add(SignBlocks.SIGN_WARNING_CONFLUENCE_RIGHT);
                        entries.add(SignBlocks.SIGN_WARING_LESS_3_TO_2);
                        entries.add(SignBlocks.SIGN_WARING_LESS_4_TO_3);
                        entries.add(SignBlocks.SIGN_WARNING_EMERGENCY_LANE_1);
                        entries.add(SignBlocks.SIGN_WARNING_EMERGENCY_LANE_2);
                        entries.add(SignBlocks.SIGN_WARNING_ROAD_ICY);
                        entries.add(SignBlocks.SIGN_WARNING_WEATHER_RAINY_SNOWY);
                        entries.add(SignBlocks.SIGN_WARNING_WEATHER_FOGGY);
                        entries.add(SignBlocks.SIGN_WARNING_WEATHER_THUNDER);
                        entries.add(SignBlocks.SIGN_WARNING_VEHICLES_QUEUED_AHEAD);
                        entries.add(SignBlocks.ZONES_BOARD_BUS);
                        entries.add(SignBlocks.ZONES_BOARD_CHARGING);
                        entries.add(SignBlocks.ZONES_BOARD_COMPANY);
                        entries.add(SignBlocks.ZONES_BOARD_DISABLED);
                        entries.add(SignBlocks.ZONES_BOARD_NON_MOTOR_VEHICLES);
                        entries.add(SignBlocks.ZONES_BOARD_SCHOOL);
                        entries.add(SignBlocks.ZONES_BOARD_SCHOOL_BUS_1);
                        entries.add(SignBlocks.ZONES_BOARD_SCHOOL_BUS_2);
                        entries.add(SignBlocks.ZONES_BOARD_TAXI);
                    }).build());

    public static void registerGroups() {

    }
}