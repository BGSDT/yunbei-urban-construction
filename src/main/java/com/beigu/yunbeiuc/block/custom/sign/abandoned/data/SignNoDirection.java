package com.beigu.yunbeiuc.block.custom.sign.abandoned.data;

import net.minecraft.util.StringIdentifiable;

public enum SignNoDirection implements StringIdentifiable {
    SIGN_NO_LEFT_TURN("sign_no_left_turn"),
    SIGN_NO_RIGHT_TURN("sign_no_right_turn"),
    SIGN_NO_STRAIGHT("sign_no_straight"),
    SIGN_NO_LEFT_RIGHT_TURN("sign_no_left_right_turn"),
    SIGN_NO_STRAIGHT_LEFT_TURN("sign_no_straight_left_turn"),
    SIGN_NO_STRAIGHT_RIGHT_TURN("sign_no_straight_right_turn"),
    SIGN_NO_SINGLE_LEFT_TURN_AROUND("sign_no_single_left_turn_around"),
    SIGN_NO_OVERTAKE("sign_no_overtake"),
    SIGN_CANCEL_OVERTAKE("sign_cancel_overtake");

    private final String name;

    SignNoDirection(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}