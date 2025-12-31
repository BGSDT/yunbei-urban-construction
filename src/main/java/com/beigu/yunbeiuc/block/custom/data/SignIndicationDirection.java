package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum SignIndicationDirection implements StringIdentifiable {
    SIGN_INDICATION_STRAIGHT("sign_indication_straight"),
    SIGN_INDICATION_LEFT_TURN("sign_indication_left_turn"),
    SIGN_INDICATION_RIGHT_TURN("sign_indication_right_turn"),
    SIGN_INDICATION_STRAIGHT_LEFT_TURN("sign_indication_straight_left_turn"),
    SIGN_INDICATION_STRAIGHT_RIGHT_TURN("sign_indication_straight_right_turn"),
    SIGN_INDICATION_LEFT_RIGHT_TURN("sign_indication_left_right_turn"),
    SIGN_INDICATION_LEFT_SIDE_MEDIAN_STRIP("sign_indication_left_side_median_strip"),
    SIGN_INDICATION_RIGHT_SIDE_MEDIAN_STRIP("sign_indication_right_side_median_strip"),
    SIGN_INDICATION_ROUNDABOUT("sign_indication_roundabout"),
    SIGN_INDICATION_ONE_WAY_STREET_STRAIGHT("sign_indication_one_way_street_straight"),
    SIGN_INDICATION_ONE_WAY_STREET_LEFT_RIGHT("sign_indication_one_way_street_left_right");

    private final String name;

    SignIndicationDirection(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}