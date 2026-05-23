package com.beigu.yunbeiuc.block;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class SignBlocks {
    public static final Block SIGN_STOP = register("sign_stop", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_YIELD = register("sign_yield", new SignRed1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_YIELD_TO_ONCOMING_TRAFFIC = register("sign_yield_to_oncoming_traffic", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_ALL = register("sign_no_all", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_ENTRY = register("sign_no_entry", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_NO_MOTOR_VEHICLES = register("sign_no_motor_vehicles", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_LARGE_BUS = register("sign_no_large_bus", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_SMALL_PASSENGER_CAR = register("sign_no_small_passenger_car", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_TRUCK = register("sign_no_truck", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_TRAILER = register("sign_no_trailer", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_TRACTOR = register("sign_no_tractor", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_THREE_WHEELED_VEHICLE = register("sign_no_three_wheeled_vehicle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_MOTORCYCLE = register("sign_no_motorcycle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_NON_MOTOR_VEHICLES = register("sign_no_non_motor_vehicles", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_ELECTRIC_VEHICLE = register("sign_no_electric_vehicle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_ANIMAL_DRAWN_CART = register("sign_no_animal_drawn_cart", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_TRICYCLE = register("sign_no_tricycle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_HUMAN_POWERED_PASSENGER_TRICYCLE = register("sign_no_human_powered_passenger_tricycle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_HUMAN_POWERED_CARGO_TRICYCLE = register("sign_no_human_powered_cargo_tricycle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_RICKSHAW = register("sign_no_rickshaw", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_PEDESTRIAN = register("sign_no_pedestrian", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_NO_LEFT_TURN = register("sign_no_left_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_STRAIGHT = register("sign_no_straight", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_RIGHT_TURN = register("sign_no_right_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_LEFT_RIGHT_TURN = register("sign_no_left_right_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_STRAIGHT_LEFT_TURN = register("sign_no_straight_left_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_STRAIGHT_RIGHT_TURN = register("sign_no_straight_right_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_SINGLE_LEFT_TURN_AROUND = register("sign_no_single_left_turn_around", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_NO_OVERTAKE = register("sign_no_overtake", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_OVERTAKE = register("sign_cancel_overtake", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_PARKING = register("sign_no_parking", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_PARKING_LONG_TIME = register("sign_no_parking_long_time", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_HONK_HORN = register("sign_no_honk_horn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WIDTH_LIMIT_20 = register("sign_width_limit_20", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WIDTH_LIMIT_25 = register("sign_width_limit_25", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WIDTH_LIMIT_30 = register("sign_width_limit_30", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WIDTH_LIMIT_35 = register("sign_width_limit_35", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WIDTH_LIMIT_40 = register("sign_width_limit_40", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WIDTH_LIMIT_45 = register("sign_width_limit_45", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_HEIGHT_LIMIT_20 = register("sign_height_limit_20", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_HEIGHT_LIMIT_25 = register("sign_height_limit_25", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_HEIGHT_LIMIT_30 = register("sign_height_limit_30", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_HEIGHT_LIMIT_35 = register("sign_height_limit_35", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_HEIGHT_LIMIT_40 = register("sign_height_limit_40", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_HEIGHT_LIMIT_45 = register("sign_height_limit_45", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WEIGHT_LIMIT_10 = register("sign_weight_limit_10", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WEIGHT_LIMIT_20 = register("sign_weight_limit_20", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WEIGHT_LIMIT_30 = register("sign_weight_limit_30", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WEIGHT_LIMIT_40 = register("sign_weight_limit_40", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_ALEX_WEIGHT_LIMIT_10 = register("sign_alex_weight_limit_10", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_ALEX_WEIGHT_LIMIT_20 = register("sign_alex_weight_limit_20", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_ALEX_WEIGHT_LIMIT_30 = register("sign_alex_weight_limit_30", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_SPEED_LIMIT_005 = register("sign_speed_limit_005", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_010 = register("sign_speed_limit_010", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_020 = register("sign_speed_limit_020", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_030 = register("sign_speed_limit_030", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_040 = register("sign_speed_limit_040", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_050 = register("sign_speed_limit_050", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_060 = register("sign_speed_limit_060", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_070 = register("sign_speed_limit_070", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_080 = register("sign_speed_limit_080", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_090 = register("sign_speed_limit_090", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_100 = register("sign_speed_limit_100", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_110 = register("sign_speed_limit_110", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_SPEED_LIMIT_120 = register("sign_speed_limit_120", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_CANCEL_SPEED_LIMIT_005 = register("sign_cancel_speed_limit_005", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_010 = register("sign_cancel_speed_limit_010", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_020 = register("sign_cancel_speed_limit_020", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_030 = register("sign_cancel_speed_limit_030", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_040 = register("sign_cancel_speed_limit_040", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_050 = register("sign_cancel_speed_limit_050", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_060 = register("sign_cancel_speed_limit_060", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_070 = register("sign_cancel_speed_limit_070", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_080 = register("sign_cancel_speed_limit_080", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_090 = register("sign_cancel_speed_limit_090", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_100 = register("sign_cancel_speed_limit_100", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_110 = register("sign_cancel_speed_limit_110", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_CANCEL_SPEED_LIMIT_120 = register("sign_cancel_speed_limit_120", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_CHECK = register("sign_check", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_PORT_CHECK = register("sign_port_check", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_NO_HAZARDOUS_MATERIALS_TRANSPORT_VEHICLE = register("sign_no_hazardous_materials_transport_vehicle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_STRAIGHT = register("sign_indication_straight", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LEFT_TURN = register("sign_indication_left_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_RIGHT_TURN = register("sign_indication_right_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_STRAIGHT_LEFT_TURN = register("sign_indication_straight_left_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_STRAIGHT_RIGHT_TURN = register("sign_indication_straight_right_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LEFT_RIGHT_TURN = register("sign_indication_left_right_turn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_RIGHT_SIDE_MEDIAN_STRIP = register("sign_indication_right_side_median_strip", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LEFT_SIDE_MEDIAN_STRIP = register("sign_indication_left_side_median_strip", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_ROUNDABOUT = register("sign_indication_roundabout", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_ONE_WAY_STREET_LEFT_RIGHT = register("sign_indication_one_way_street_left_right", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_ONE_WAY_STREET_STRAIGHT = register("sign_indication_one_way_street_straight", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_HONK_HORN = register("sign_indication_honk_horn", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_HEADLIGHTS = register("sign_indication_headlights", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_MIN_SPEED_LIMIT_005 = register("sign_min_speed_limit_005", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_010 = register("sign_min_speed_limit_010", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_020 = register("sign_min_speed_limit_020", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_030 = register("sign_min_speed_limit_030", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_040 = register("sign_min_speed_limit_040", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_050 = register("sign_min_speed_limit_050", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_060 = register("sign_min_speed_limit_060", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_070 = register("sign_min_speed_limit_070", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_080 = register("sign_min_speed_limit_080", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_090 = register("sign_min_speed_limit_090", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_MIN_SPEED_LIMIT_100 = register("sign_min_speed_limit_100", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_YIELD_TO_ONCOMING_TRAFFIC = register("sign_indication_yield_to_oncoming_traffic", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_ZEBRA_CROSSING = register("sign_indication_zebra_crossing", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_ZEBRA_CROSSING_FLUORESCENCE = register("sign_indication_zebra_crossing_fluorescence", new SignBlue4Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_LANE_DIRECTION_RIGHT_TURN = register("sign_indication_lane_direction_right_turn", new SignBlue1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LEFT_TURN = register("sign_indication_lane_direction_left_turn", new SignBlue1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_STRAIGHT = register("sign_indication_lane_direction_straight", new SignBlue1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_STRAIGHT_RIGHT_TURN = register("sign_indication_lane_direction_straight_right_turn", new SignBlue1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_STRAIGHT_LEFT_TURN = register("sign_indication_lane_direction_straight_left_turn", new SignBlue1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_SINGLE_LEFT_TURN_AROUND = register("sign_indication_lane_direction_single_left_turn_around", new SignBlue1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LEFT_TURN_AROUND = register("sign_indication_lane_direction_left_turn_around", new SignBlue1Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_MOTOR_VEHICLES = register("sign_indication_motor_vehicles", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES = register("sign_indication_lane_direction_motor_vehicles", new SignBlue2Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES_STRAIGHT = register("sign_indication_lane_direction_motor_vehicles_straight", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES_LEFT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_motor_vehicles_left_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_MOTOR_VEHICLES_RIGHT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_motor_vehicles_right_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR = register("sign_indication_lane_direction_small_passenger_car", new SignBlue2Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR_STRAIGHT = register("sign_indication_lane_direction_small_passenger_car_straight", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR_LEFT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_small_passenger_car_left_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_SMALL_PASSENGER_CAR_RIGHT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_small_passenger_car_right_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS = register("sign_indication_lane_direction_large_bus", new SignBlue2Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_STRAIGHT = register("sign_indication_lane_direction_large_bus_straight", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_LEFT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_large_bus_left_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_RIGHT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_large_bus_right_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT = register("sign_indication_lane_direction_large_bus_text", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT_STRAIGHT = register("sign_indication_lane_direction_large_bus_text_straight", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT_LEFT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_large_bus_text_left_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_TEXT_RIGHT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_large_bus_text_right_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT = register("sign_indication_lane_direction_large_bus_brt", new SignBlue2Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT_STRAIGHT = register("sign_indication_lane_direction_large_bus_brt_straight", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT_LEFT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_large_bus_brt_left_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_LARGE_BUS_BRT_RIGHT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_large_bus_brt_right_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_LANE_DIRECTION_TRAM_STRAIGHT = register("sign_indication_lane_direction_tram_straight", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_LANE_DIRECTION_HOV = register("sign_indication_lane_direction_hov", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_NON_MOTOR_VEHICLES = register("sign_indication_non_motor_vehicles", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES = register("sign_indication_lane_direction_non_motor_vehicles", new SignBlue2Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES_STRAIGHT = register("sign_indication_lane_direction_non_motor_vehicles_straight", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES_LEFT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_non_motor_vehicles_left_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_NON_MOTOR_VEHICLES_RIGHT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_non_motor_vehicles_right_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_ELECTRIC_VEHICLE = register("sign_indication_electric_vehicle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE = register("sign_indication_lane_direction_electric_vehicle", new SignBlue2Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE_STRAIGHT = register("sign_indication_lane_direction_electric_vehicle_straight", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE_LEFT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_electric_vehicle_left_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_LANE_DIRECTION_ELECTRIC_VEHICLE_RIGHT_SIDE_MEDIAN_STRIP = register("sign_indication_lane_direction_electric_vehicle_right_side_median_strip", new SignBlue3Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_PEDESTRIAN = register("sign_indication_pedestrian", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PEDESTRIAN_NON_MOTOR_VEHICLES_1 = register("sign_indication_pedestrian_non_motor_vehicles_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PEDESTRIAN_NON_MOTOR_VEHICLES_2 = register("sign_indication_pedestrian_non_motor_vehicles_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PEDESTRIAN_NON_MOTOR_VEHICLES_3 = register("sign_indication_pedestrian_non_motor_vehicles_3", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_PROMOTION_NON_MOTOR_VEHICLES = register("sign_indication_promotion_non_motor_vehicles", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PROMOTION_DRIVE_RIGHT = register("sign_indication_promotion_drive_right", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_PARKING_1 = register("sign_indication_parking_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_2 = register("sign_indication_parking_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_3 = register("sign_indication_parking_3", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_4 = register("sign_indication_parking_4", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_DISABLED = register("sign_indication_parking_disabled", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_SCHOOL_BUS = register("sign_indication_parking_school_bus", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_SCHOOL_BUS_WUXI = register("sign_indication_school_bus_wuxi", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_SCHOOL_BUS_FLUORESCENCE = register("sign_indication_parking_school_bus_fluorescence", new SignBlue4Block(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_TAXI = register("sign_indication_parking_taxi", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_NON_MOTOR_VEHICLE = register("sign_indication_parking_non_motor_vehicle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_BUS = register("sign_indication_parking_bus", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_CHARGING = register("sign_indication_parking_charging", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PARKING_COMPANY = register("sign_indication_parking_company", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_TURN_AROUND = register("sign_indication_turn_around", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_PAVED_SHOULDER_1 = register("sign_indication_paved_shoulder_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PAVED_SHOULDER_2 = register("sign_indication_paved_shoulder_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_INDICATION_PAVED_SHOULDER_3 = register("sign_indication_paved_shoulder_3", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_INDICATION_OK_TRUCK = register("sign_indication_ok_truck", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_CROSSING_1 = register("sign_warning_crossing_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_CROSSING_2 = register("sign_warning_crossing_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CROSSING_3 = register("sign_warning_crossing_3", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CROSSING_4 = register("sign_warning_crossing_4", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CROSSING_5 = register("sign_warning_crossing_5", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CROSSING_6 = register("sign_warning_crossing_6", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CROSSING_7 = register("sign_warning_crossing_7", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CROSSING_8 = register("sign_warning_crossing_8", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CROSSING_9 = register("sign_warning_crossing_9", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CROSSING_10 = register("sign_warning_crossing_10", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CROSSING_11 = register("sign_warning_crossing_11", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_SHARP_TURN_1 = register("sign_warning_sharp_turn_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_SHARP_TURN_2 = register("sign_warning_sharp_turn_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_REVERSE_DETOUR_1 = register("sign_warning_reverse_detour_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_REVERSE_DETOUR_2 = register("sign_warning_reverse_detour_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_CONTINUOUS_WINDING_ROADS_1 = register("sign_warning_continuous_winding_roads_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CONTINUOUS_WINDING_ROADS_2 = register("sign_warning_continuous_winding_roads_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_STEEP_SLOPE_UP = register("sign_warning_steep_slope_up", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_STEEP_SLOPE_DOWN = register("sign_warning_steep_slope_down", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_CONTINUOUS_DOWNHILL = register("sign_warning_continuous_downhill", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_NARROW_ROAD_DOUBLE = register("sign_warning_narrow_road_double", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_NARROW_ROAD_LEFT = register("sign_warning_narrow_road_left", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_NARROW_ROAD_RIGHT = register("sign_warning_narrow_road_right", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_NARROW_BRIDGE = register("sign_warning_narrow_bridge", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_TWO_WAY_TRAFFIC = register("sign_warning_two_way_traffic", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_ZEBRA_CROSSING = register("sign_warning_zebra_crossing", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_ZEBRA_CROSSING_FLUORESCENCE = register("sign_warning_zebra_crossing_fluorescence", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_CHILDREN = register("sign_warning_children", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CHILDREN_FLUORESCENCE = register("sign_warning_children_fluorescence", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_DISABLED = register("sign_warning_disabled", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_DISABLED_FLUORESCENCE = register("sign_warning_disabled_fluorescence", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_NON_MOTOR_VEHICLES = register("sign_warning_non_motor_vehicles", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_ELECTRIC_VEHICLE = register("sign_warning_electric_vehicle", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_LIVESTOCK = register("sign_warning_livestock", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_WILDLIFE = register("sign_warning_wildlife", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_TRAFFIC_LIGHTS = register("sign_warning_traffic_lights", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_FALLING_ROCKS_LEFT = register("sign_warning_falling_rocks_left", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_FALLING_ROCKS_RIGHT = register("sign_warning_falling_rocks_right", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_CROSSWIND = register("sign_warning_crosswind", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_SLIPPERY = register("sign_warning_slippery", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_PERILOUS_ROAD_ALONG_THE_MOUNTAIN_LEFT = register("sign_warning_perilous_road_along_the_mountain_left", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_PERILOUS_ROAD_ALONG_THE_MOUNTAIN_RIGHT = register("sign_warning_perilous_road_along_the_mountain_right", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_EMBANKMENT_ROAD_LEFT = register("sign_warning_embankment_road_left", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_EMBANKMENT_ROAD_RIGHT = register("sign_warning_embankment_road_right", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_VILLAGE = register("sign_warning_village", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_TUNNEL = register("sign_warning_tunnel", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CAMEL_BACK_BRIDGE = register("sign_warning_camel_back_bridge", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_ROAD_UNEVEN = register("sign_warning_road_uneven", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_SPEED_BUMP = register("sign_warning_speed_bump", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_ROAD_WET = register("sign_warning_road_wet", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_SOMEONE_GUARDING_THE_RAILWAY_CROSSING = register("sign_warning_someone_guarding_the_railway_crossing", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_UNMANNED_GUARDING_THE_RAILWAY_CROSSING = register("sign_warning_unmanned_guarding_the_railway_crossing", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_ACCIDENT_PRONE_ROAD = register("sign_warning_accident_prone_road", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_DETOUR_DOUBLE = register("sign_warning_detour_double", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_DETOUR_LEFT = register("sign_warning_detour_left", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_DETOUR_RIGHT = register("sign_warning_detour_right", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_DANGEROUS = register("sign_warning_dangerous", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CONSTRUCTION = register("sign_warning_construction", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_ACCIDENT = register("sign_warning_accident", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_TIDAL_LANE = register("sign_warning_tidal_lane", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_DISTANCE_BETWEEN_VEHICLES = register("sign_warning_distance_between_vehicles", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_CONFLUENCE_LEFT = register("sign_warning_confluence_left", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_CONFLUENCE_RIGHT = register("sign_warning_confluence_right", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARING_LESS_3_TO_2 = register("sign_waring_less_3_to_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARING_LESS_4_TO_3 = register("sign_waring_less_4_to_3", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_EMERGENCY_LANE_1 = register("sign_warning_emergency_lane_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_EMERGENCY_LANE_2 = register("sign_warning_emergency_lane_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_WARNING_ROAD_ICY = register("sign_warning_road_icy", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_WEATHER_RAINY_SNOWY = register("sign_warning_weather_rainy_snowy", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_WEATHER_FOGGY = register("sign_warning_weather_foggy", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_WEATHER_THUNDER = register("sign_warning_weather_thunder", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_WARNING_VEHICLES_QUEUED_AHEAD = register("sign_warning_vehicles_queued_ahead", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block ZONES_BOARD_BUS = register("zones_board_bus", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ZONES_BOARD_CHARGING = register("zones_board_charging", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ZONES_BOARD_COMPANY = register("zones_board_company", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ZONES_BOARD_DISABLED = register("zones_board_disabled", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ZONES_BOARD_NON_MOTOR_VEHICLES = register("zones_board_non_motor_vehicles", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ZONES_BOARD_SCHOOL = register("zones_board_school", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ZONES_BOARD_SCHOOL_BUS_1 = register("zones_board_school_bus_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ZONES_BOARD_SCHOOL_BUS_2 = register("zones_board_school_bus_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block ZONES_BOARD_TAXI = register("zones_board_taxi", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1 = register("sign_guide_intersection_advance_warning_1", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2 = register("sign_guide_intersection_advance_warning_2", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_EAST = register("sign_guide_intersection_advance_warning_3_east", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_NORTH = register("sign_guide_intersection_advance_warning_3_north", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_SOUTH = register("sign_guide_intersection_advance_warning_3_south", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3_WEST = register("sign_guide_intersection_advance_warning_3_west", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4_EAST = register("sign_guide_intersection_advance_warning_4_east", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4_NORTH = register("sign_guide_intersection_advance_warning_4_north", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4_SOUTH = register("sign_guide_intersection_advance_warning_4_south", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4_WEST = register("sign_guide_intersection_advance_warning_4_west", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5 = register("sign_guide_intersection_advance_warning_5", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_6 = register("sign_guide_intersection_advance_warning_6", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_7 = register("sign_guide_intersection_advance_warning_7", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
//    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_8 = register("sign_guide_intersection_advance_warning_8", new SignSimpleBlock(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));

    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_LEFT = register("sign_guide_intersection_advance_warning_1_wuhan_left", new SignGuideIntersectionAdvanceWarning1Wuhan(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_STRAIGHT = register("sign_guide_intersection_advance_warning_1_wuhan_straight", new SignGuideIntersectionAdvanceWarning1Wuhan(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_RIGHT = register("sign_guide_intersection_advance_warning_1_wuhan_right", new SignGuideIntersectionAdvanceWarning1Wuhan(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1 = register("sign_guide_intersection_advance_warning_1", new SignGuideIntersectionAdvanceWarning1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2 = register("sign_guide_intersection_advance_warning_2", new SignGuideIntersectionAdvanceWarning1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3 = register("sign_guide_intersection_advance_warning_3", new SignGuideIntersectionAdvanceWarning3(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_4 = register("sign_guide_intersection_advance_warning_4", new SignGuideIntersectionAdvanceWarning3(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_5 = register("sign_guide_intersection_advance_warning_5", new SignGuideIntersectionAdvanceWarning5(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));
    public static final Block SIGN_GUIDE_LANE_INDICATOR_1 = register("sign_guide_lane_indicator_1", new SignGuideLaneIndicator1(AbstractBlock.Settings.copy(Blocks.CYAN_TERRACOTTA).nonOpaque().requiresTool()));


    public static Block register(String id, Block block) {
        registerBlockItems(id, block);
        return Registry.register(Registries.BLOCK, new Identifier(YunbeiUrbanConstruction.MOD_ID, id), block);
    }

    public static void registerBlockItems(String id, Block block) {
        Registry.register(Registries.ITEM, new Identifier(YunbeiUrbanConstruction.MOD_ID, id),
                new BlockItem(block, new Item.Settings()));
    }

    public static Block registerWithoutItem(String id, Block block) {
        return Registry.register(Registries.BLOCK, new Identifier(YunbeiUrbanConstruction.MOD_ID, id), block);
    }

    public static void registerSignBlocks() {

    }
}
