package com.beigu.yunbeiuc.block.custom.sign.data;

import net.minecraft.util.StringIdentifiable;

public enum SignIndicationLaneDirection implements StringIdentifiable {
    SIGN_INDICATION_LANE_DIRECTION_LEFT_TURN("sign_indication_lane_direction_left_turn"),
    SIGN_INDICATION_LANE_DIRECTION_RIGHT_TURN("sign_indication_lane_direction_right_turn"),
    SIGN_INDICATION_LANE_DIRECTION_STRAIGHT("sign_indication_lane_direction_straight"),
    SIGN_INDICATION_LANE_DIRECTION_STRAIGHT_LEFT_TURN("sign_indication_lane_direction_straight_left_turn"),
    SIGN_INDICATION_LANE_DIRECTION_STRAIGHT_RIGHT_TURN("sign_indication_lane_direction_straight_right_turn"),
    SIGN_INDICATION_LANE_DIRECTION_SINGLE_LEFT_TURN_AROUND("sign_indication_lane_direction_single_left_turn_around"),
    SIGN_INDICATION_LANE_DIRECTION_LEFT_TURN_AROUND("sign_indication_lane_direction_left_turn_around");

    private final String name;

    SignIndicationLaneDirection(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}