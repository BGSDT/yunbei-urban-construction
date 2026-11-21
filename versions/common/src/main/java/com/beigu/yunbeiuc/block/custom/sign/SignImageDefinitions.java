package com.beigu.yunbeiuc.block.custom.sign;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SignImageDefinitions {
    public record Line(String kind, String value, float x, float y, float size, float rotZ, float rectW, float rectH, int color, float scaleX) {
        public boolean isRect() { return "rect".equals(kind); }
        public boolean isText() { return "text".equals(kind); }
    }

    public static final class Def {
        private final String base;
        private final Set<CustomSignTypeBlock.Type> types;
        private final List<Line> lines;

        private Def(String base, Set<CustomSignTypeBlock.Type> types, List<Line> lines) {
            this.base = base;
            this.types = Collections.unmodifiableSet(types);
            this.lines = lines;
        }

        public String base() { return base; }
        public Set<CustomSignTypeBlock.Type> types() { return types; }
        public List<Line> lines() { return lines; }
    }

    private static final Map<String, Def> DEFS = build();

    private SignImageDefinitions() {}

    public static Def get(String blockId) {
        return DEFS.get(blockId);
    }

    private static Map<String, Def> build() {
        Map<String, Def> map = new HashMap<>();
        d(map, "sign_alex_weight_limit_10", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_alex_weight_limit_10.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_alex_weight_limit_20", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_alex_weight_limit_20.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_alex_weight_limit_30", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_alex_weight_limit_30.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_overtake", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_overtake.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_005", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_005.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_010", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_010.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_020", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_020.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_030", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_030.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_040", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_040.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_050", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_050.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_060", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_060.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_070", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_070.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_080", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_080.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_090", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_090.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_100", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_100.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_110", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_110.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_cancel_speed_limit_120", "sign_custom_cancel_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_120.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_check", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_check.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_height_limit_20", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_20.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_height_limit_25", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_25.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_height_limit_30", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_30.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_height_limit_35", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_35.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_height_limit_40", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_40.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_height_limit_45", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_45.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_electric_vehicle", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_electric_vehicle.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_headlights", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_headlights.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_honk_horn", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_honk_horn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_lane_direction_electric_vehicle", "sign_custom_lane_direction_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_electric_vehicle.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_electric_vehicle_left_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_electric_vehicle.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_left_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_electric_vehicle_right_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_electric_vehicle.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_right_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_electric_vehicle_straight", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_electric_vehicle.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_straight_inverted.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_hov", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_hov.png", 0.0f, 0.0f, 1.0f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus", "sign_custom_lane_direction_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_brt", "sign_custom_lane_direction_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus_brt.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_brt_left_side_median_strip", "sign_custom_lane_direction_simple_4", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus_brt.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_left_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_brt_right_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus_brt.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_right_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_brt_straight", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus_brt.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_straight_inverted.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_left_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_left_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_right_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_right_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_straight", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_straight_inverted.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_text", "sign_custom_lane_direction_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus_text.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_text_left_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus_text.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_left_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_text_right_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus_text.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_right_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_large_bus_text_straight", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_large_bus_text.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_straight_inverted.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_left_turn", "sign_custom_lane_direction_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_left_turn.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_left_turn_around", "sign_custom_lane_direction_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_left_turn_around.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_motor_vehicles", "sign_custom_lane_direction_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_motor_vehicles.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_motor_vehicles_left_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_motor_vehicles.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_left_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_motor_vehicles_right_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_motor_vehicles.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_right_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_motor_vehicles_straight", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_motor_vehicles.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_straight_inverted.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_non_motor_vehicles", "sign_custom_lane_direction_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_non_motor_vehicles.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_non_motor_vehicles_left_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_non_motor_vehicles.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_left_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_non_motor_vehicles_right_side_median_strip", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_non_motor_vehicles.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_right_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_non_motor_vehicles_straight", "sign_custom_lane_direction_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_non_motor_vehicles.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_straight_inverted.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_right_turn", "sign_custom_lane_direction_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_right_turn.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_single_left_turn_around", "sign_custom_lane_direction_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_single_left_turn_around.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_small_passenger_car", "sign_custom_lane_direction_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_small_passenger_car.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_small_passenger_car_left_side_median_strip", "sign_custom_lane_direction_simple_4", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_small_passenger_car.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_left_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_small_passenger_car_right_side_median_strip", "sign_custom_lane_direction_simple_4", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_small_passenger_car.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_right_side_median_strip.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_small_passenger_car_straight", "sign_custom_lane_direction_simple_4", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_small_passenger_car.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_straight_inverted.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_lane_direction_straight", "sign_custom_lane_direction_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_straight.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_straight_left_turn", "sign_custom_lane_direction_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_straight_left_turn.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_straight_right_turn", "sign_custom_lane_direction_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_straight_right_turn.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_lane_direction_tram_straight", "sign_custom_lane_direction_simple_4", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_tram.png", 0.0f, 3.0f, 0.625f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_straight_inverted.png", 0.0f, -5.0f, 0.375f, 0.0f));
        d(map, "sign_indication_left_right_turn", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_left_right_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_left_side_median_strip", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_left_side_median_strip.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_left_turn", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_left_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_motor_vehicles", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_motor_vehicles.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_non_motor_vehicles", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_non_motor_vehicles.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_ok_truck", "sign_custom_indication_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_truck.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_parking_1", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_1.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_2", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_2.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_3", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_3.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_bus", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_bus.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_charging", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_charging.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_company", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_company.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_disabled", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_disabled.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_non_motor_vehicle", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_non_motor_vehicles.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_school_bus", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_school_bus.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_school_bus_fluorescence", "sign_custom_indication_simple_fluorescence", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_school_bus.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_parking_taxi", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_parking_taxi.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_paved_shoulder_1", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_paved_shoulder_1.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_paved_shoulder_2", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_paved_shoulder_2.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_paved_shoulder_3", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_paved_shoulder_1.png", 0.0f, 0.0f, 0.75f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_paved_shoulder_3.png", 0.0f, 0.0f, 0.9375f, 0.0f));
        d(map, "sign_indication_pedestrian", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_pedestrian.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_pedestrian_non_motor_vehicles_1", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_pedestrian.png", 3.625f, 0.0f, 0.4219f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_non_motor_vehicles.png", -3.625f, 0.0f, 0.4219f, 0.0f),
                r(0.5f, 14.0f, 0.0f, 0.0f, 0.0f, 16777215));
        d(map, "sign_indication_pedestrian_non_motor_vehicles_2", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_non_motor_vehicles.png", 3.625f, 0.0f, 0.4219f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_pedestrian.png", -3.625f, 0.0f, 0.4219f, 0.0f),
                r(0.5f, 14.0f, 0.0f, 0.0f, 0.0f, 16777215));
        d(map, "sign_indication_pedestrian_non_motor_vehicles_3", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_non_motor_vehicles.png", 0.0f, -3.5f, 0.4219f, 0.0f),
                l("yunbeiuc:textures/block/sign/sign_indication_pedestrian.png", 0.0f, 3.5f, 0.4219f, 0.0f));
        d(map, "sign_indication_promotion_drive_right", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_drive_right.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_promotion_non_motor_vehicles", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_promotion_non_motor_vehicles.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_right_side_median_strip", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_right_side_median_strip.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_right_turn", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_right_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_roundabout", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_roundabout.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_straight", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_straight.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_straight_left_turn", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_straight_left_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_straight_right_turn", "sign_custom_indication_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_straight_right_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_indication_turn_around", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_single_left_turn_around.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_yield_to_oncoming_traffic", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_yield_to_oncoming_traffic.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_zebra_crossing", "sign_custom_indication_simple_3", "nl",
                l("yunbeiuc:textures/block/sign/sign_indication_zebra_crossing.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_indication_zebra_crossing_fluorescence", "sign_custom_indication_simple_fluorescence", "nlh",
                l("yunbeiuc:textures/block/sign/sign_indication_zebra_crossing.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_min_speed_limit_005", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_005.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_010", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_010.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_020", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_020.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_030", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_030.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_040", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_040.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_050", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_050.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_060", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_060.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_070", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_070.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_080", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_080.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_090", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_090.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_min_speed_limit_100", "sign_custom_min_speed_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_min_speed_limit_100.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_all", "sign_custom_no_simple_3", "nlh");
        d(map, "sign_no_animal_drawn_cart", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_animal_drawn_cart.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_electric_vehicle", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_electric_vehicle.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_entry", "sign_custom_no_entry_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_entry.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_hazardous_materials_transport_vehicle", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_hazardous_materials_transport_vehicle.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_honk_horn", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_honk_horn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_human_powered_cargo_tricycle", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_human_powered_cargo_tricycle.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_human_powered_passenger_tricycle", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_human_powered_passenger_tricycle.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_large_bus", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_large_bus.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_left_right_turn", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_left_right_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_left_turn", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_left_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_motorcycle", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_motorcycle.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_non_motor_vehicles", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_non_motor_vehicles.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_overtake", "sign_custom_no_simple_1", "lh",
                l("yunbeiuc:textures/block/sign/sign_no_overtake.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_pedestrian", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_pedestrian.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_rickshaw", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_rickshaw.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_right_turn", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_right_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_single_left_turn_around", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_single_left_turn_around.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_small_passenger_car", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_small_passenger_car.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_straight", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_straight.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_straight_left_turn", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_straight_left_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_straight_right_turn", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_straight_right_turn.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_three_wheeled_vehicle", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_three_wheeled_vehicle.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_tractor", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_tractor.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_trailer", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_trailer.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_tricycle", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_tricycle.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_no_truck", "sign_custom_no_simple_1", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_truck.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_port_check", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_port_check.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_005", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_005.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_010", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_010.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_020", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_020.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_030", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_030.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_040", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_040.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_050", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_050.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_060", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_060.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_070", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_070.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_080", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_080.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_090", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_090.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_100", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_100.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_110", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_110.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_speed_limit_120", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_speed_limit_120.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_stop", "sign_custom_stop_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_stop.png", 0.0f, 0.0f, 0.75f, 0.0f));
        d(map, "sign_warning_accident", "sign_custom_warning_simple_3", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_accident.png", 0.0f, -2.91f, 0.7188f, 0.0f));
        d(map, "sign_warning_accident_prone_road", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_accident_prone_road.png", 0.0f, -0.91f, 0.5938f, 0.0f));
        d(map, "sign_warning_children", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_children.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_children_fluorescence", "sign_custom_warning_simple_fluorescence", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_children.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_confluence_left", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_confluence_left.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_confluence_right", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_confluence_right.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_construction", "sign_custom_warning_simple_4", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_construction.png", 0.0f, -1.66f, 0.75f, 0.0f));
        d(map, "sign_warning_continuous_downhill", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_continuous_downhill.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_continuous_winding_roads_1", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_continuous_winding_roads_1.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_continuous_winding_roads_2", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_continuous_winding_roads_2.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_1", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_1.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_10", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_10.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_11", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_11.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_2", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_2.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_3", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_3.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_4", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_4.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_5", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_5.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_6", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_6.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_7", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_7.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_8", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_8.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crossing_9", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crossing_9.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_crosswind", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_crosswind.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_dangerous", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_dangerous.png", 0.0f, -0.66f, 0.625f, 0.0f));
        d(map, "sign_warning_detour_double", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_detour_double.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_detour_left", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_detour_left.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_detour_right", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_detour_right.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_disabled", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_disabled.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_disabled_fluorescence", "sign_custom_warning_simple_fluorescence", "nh",
                l("yunbeiuc:textures/block/sign/sign_disabled.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_electric_vehicle", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_no_electric_vehicle.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_embankment_road_left", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_embankment_road_left.png", 0.0f, -1.66f, 0.75f, 0.0f));
        d(map, "sign_warning_embankment_road_right", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_embankment_road_right.png", 0.0f, -1.66f, 0.75f, 0.0f));
        d(map, "sign_warning_falling_rocks_left", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_falling_rocks_left.png", 0.0f, -1.66f, 0.75f, 0.0f));
        d(map, "sign_warning_falling_rocks_right", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_falling_rocks_right.png", 0.0f, -1.66f, 0.75f, 0.0f));
        d(map, "sign_warning_livestock", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_livestock.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_narrow_bridge", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_narrow_bridge.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_narrow_road_double", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_narrow_road_double.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_narrow_road_left", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_narrow_road_left.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_narrow_road_right", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_narrow_road_right.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_non_motor_vehicles", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_no_non_motor_vehicles.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_perilous_road_along_the_mountain_left", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_perilous_road_along_the_mountain_left.png", 0.0f, -1.66f, 0.75f, 0.0f));
        d(map, "sign_warning_perilous_road_along_the_mountain_right", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_perilous_road_along_the_mountain_right.png", 0.0f, -1.66f, 0.75f, 0.0f));
        d(map, "sign_warning_reverse_detour_1", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_reverse_detour_1.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_reverse_detour_2", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_reverse_detour_2.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_road_icy", "sign_custom_warning_simple_2", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_road_icy.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_sharp_turn_1", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_sharp_turn_1.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_sharp_turn_2", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_sharp_turn_2.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_slippery", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_slippery.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_someone_guarding_the_railway_crossing", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_someone_guarding_the_railway_crossing.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_tidal_lane", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_tidal_lane.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_traffic_lights", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_traffic_lights.png", 0.0f, -0.66f, 0.625f, 0.0f));
        d(map, "sign_warning_tunnel", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_tunnel.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_two_way_traffic", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_two_way_traffic.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_unmanned_guarding_the_railway_crossing", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_unmanned_guarding_the_railway_crossing.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_vehicles_queued_ahead", "sign_custom_warning_simple_2", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_vehicles_queued_ahead.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_village", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_village.png", 0.0f, -1.66f, 0.75f, 0.0f));
        d(map, "sign_warning_weather_foggy", "sign_custom_warning_simple_2", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_weather_foggy.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_weather_rainy_snowy", "sign_custom_warning_simple_2", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_weather_rainy_snowy.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_weather_thunder", "sign_custom_warning_simple_2", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_weather_thunder.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_wildlife", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_wildlife.png", 0.0f, -2.16f, 0.4375f, 0.0f));
        d(map, "sign_warning_zebra_crossing", "sign_custom_warning_simple_1", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_zebra_crossing.png", 0.0f, -0.16f, 0.8125f, 0.0f));
        d(map, "sign_warning_zebra_crossing_fluorescence", "sign_custom_warning_simple_fluorescence", "nh",
                l("yunbeiuc:textures/block/sign/sign_warning_zebra_crossing.png", 0.0f, -0.16f, 0.8125f, 0.0f));
        d(map, "sign_weight_limit_10", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_weight_limit_10.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_weight_limit_20", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_weight_limit_20.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_weight_limit_30", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_weight_limit_30.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_weight_limit_40", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_weight_limit_40.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "sign_width_limit_20", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_20.png", 0.0f, 0.0f, 0.5f, 0.0f));
        d(map, "sign_width_limit_25", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_25.png", 0.0f, 0.0f, 0.5f, 0.0f));
        d(map, "sign_width_limit_30", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_30.png", 0.0f, 0.0f, 0.5f, 0.0f));
        d(map, "sign_width_limit_35", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_35.png", 0.0f, 0.0f, 0.5f, 0.0f));
        d(map, "sign_width_limit_40", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_40.png", 0.0f, 0.0f, 0.5f, 0.0f));
        d(map, "sign_width_limit_45", "sign_custom_height_limit_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_height_limit_45.png", 0.0f, 0.0f, 0.5f, 0.0f));
        d(map, "sign_yield", "sign_custom_yield_simple", "n",
                l("yunbeiuc:textures/block/sign/sign_yield.png", 0.0f, 2.16f, 0.4375f, 0.0f));
        d(map, "sign_yield_to_oncoming_traffic", "sign_custom_no_simple_3", "nlh",
                l("yunbeiuc:textures/block/sign/sign_yield_to_oncoming_traffic.png", 0.0f, 0.0f, 0.625f, 0.0f));
        d(map, "zones_board_arrow_left", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/zones_board_arrow_left.png", 0.0f, 5.5f, 0.5f, 0.0f));
        d(map, "zones_board_arrow_left_right", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/zones_board_arrow_left_right_mirror.png", 0.0f, 5.5f, 0.5625f, 0.0f));
        d(map, "zones_board_arrow_left_turn", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/zones_board_arrow_left_turn.png", 0.0f, 5.5f, 0.25f, 0.0f));
        d(map, "zones_board_arrow_right", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/zones_board_arrow_left_mirror.png", 0.0f, 5.5f, 0.5f, 0.0f));
        d(map, "zones_board_arrow_right_turn", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/zones_board_arrow_left_turn_mirror.png", 0.0f, 5.5f, 0.25f, 0.0f));
        d(map, "zones_board_arrow_straight", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/zones_board_arrow_straight.png", 0.0f, 5.5f, 0.25f, 0.0f));
        d(map, "zones_board_bus", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_bus.png", 0.0f, 5.5f, 0.375f, 0.0f));
        d(map, "zones_board_central_axle_trailer", "sign_custom_zones_board_simple", "nlh",
                t("中置轴挂车", 0.023f, 5.477f, 0.0152f, 1.0f));
        d(map, "zones_board_charging", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_charging.png", 0.0f, 5.5f, 0.25f, 0.0f));
        d(map, "zones_board_company", "sign_custom_zones_board_simple", "nlh",
                t("单位专属", -0.027f, 5.527f, 0.0184f, 1.0f));
        d(map, "zones_board_disabled", "sign_custom_zones_board_simple_2", "nlh",
                l("yunbeiuc:textures/block/sign/sign_disabled.png", 0.0f, 4.5f, 0.375f, 0.0f));
        d(map, "zones_board_large_trucks_keep_right", "sign_custom_zones_board_large_truck_right", "nlh",
                t("大型货车", -0.027f, 5.496f, 0.0206f, 1.0f),
                t("靠右行驶", 0.027f, 1.668f, 0.0206f, 1.0f));
        d(map, "zones_board_medium_sized_bus", "sign_custom_zones_board_simple", "nlh",
                t("中型客车", -0.082f, 5.5f, 0.0209f, 1.0f));
        d(map, "zones_board_non_motor_vehicles", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_no_non_motor_vehicles.png", 0.0f, 5.5f, 0.25f, 0.0f));
        d(map, "zones_board_port", "sign_custom_zones_board_simple", "nlh",
                t("口岸", 0.0f, 5.485f, 0.018f, 1.0f));
        d(map, "zones_board_ramp", "sign_custom_zones_board_ramp", "nlh",
                t("匝道", 0.188f, 0.027f, 0.0269f, 1.0f));
        d(map, "zones_board_school", "sign_custom_zones_board_fluorescence", "nlh",
                t("学校", 0.059f, 4.598f, 0.0286f, 1.0f));
        d(map, "zones_board_school_bus_1", "sign_custom_zones_board_fluorescence", "nlh",
                l("yunbeiuc:textures/block/sign/sign_school_bus.png", 0.0f, 4.5f, 0.5f, 0.0f));
        d(map, "zones_board_school_bus_2", "sign_custom_zones_board_fluorescence_2", "nlh",
                t("校车专用", 0.0f, 5.527f, 0.0191f, 1.0f));
        d(map, "zones_board_semi_trailer", "sign_custom_zones_board_simple", "nlh",
                t("半挂车", -0.059f, 5.539f, 0.0206f, 1.0f));
        d(map, "zones_board_taxi", "sign_custom_zones_board_simple", "nlh",
                l("yunbeiuc:textures/block/sign/sign_taxi.png", 0.0f, 5.5f, 0.25f, 0.0f));
        d(map, "zones_board_tourist_bus", "sign_custom_zones_board_simple", "nlh",
                t("旅游大客车", -0.026f, 5.474f, 0.0164f, 1.0f));
        d(map, "zones_board_truck_road", "sign_custom_zones_board_simple", "nlh",
                t("货车道路", -0.055f, 5.5f, 0.0202f, 1.0f));
        d(map, "zones_board_trucks_keep_right", "sign_custom_zones_board_truck_right", "nlh",
                t("货车靠右行驶", -0.082f, 5.473f, 0.0148f, 1.0f));
        return Collections.unmodifiableMap(map);
    }

    private static Line l(String texture, float x, float y, float size, float rotZ) {
        return new Line("image", texture, x, y, size, rotZ, 0f, 0f, 0, 1f);
    }

    private static Line r(float w, float h, float x, float y, float rotZ, int color) {
        return new Line("rect", "", x, y, 1f, rotZ, w, h, color, 1f);
    }

    private static Line t(String text, float x, float y, float size, float scaleX) {
        return new Line("text", text, x, y, size, 0f, 0f, 0f, 0, scaleX);
    }

    private static void d(Map<String, Def> map, String id, String base, String types, Line... lines) {
        EnumSet<CustomSignTypeBlock.Type> set = EnumSet.noneOf(CustomSignTypeBlock.Type.class);
        if (types.contains("n")) set.add(CustomSignTypeBlock.Type.NORMAL);
        if (types.contains("l")) set.add(CustomSignTypeBlock.Type.POLE_L);
        if (types.contains("h")) set.add(CustomSignTypeBlock.Type.POLE_H);
        map.put(id, new Def(base, set, List.of(lines)));
    }
}
