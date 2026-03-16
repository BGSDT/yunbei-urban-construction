package com.beigu.yunbeiuc.block.custom.sign.abandoned.data;

import net.minecraft.util.StringIdentifiable;

public enum SignCancelSpeedLimit implements StringIdentifiable {
    CANCEL_SPEED_LIMIT_005("cancel_speed_limit_005"),
    CANCEL_SPEED_LIMIT_010("cancel_speed_limit_010"),
    CANCEL_SPEED_LIMIT_020("cancel_speed_limit_020"),
    CANCEL_SPEED_LIMIT_030("cancel_speed_limit_030"),
    CANCEL_SPEED_LIMIT_040("cancel_speed_limit_040"),
    CANCEL_SPEED_LIMIT_050("cancel_speed_limit_050"),
    CANCEL_SPEED_LIMIT_060("cancel_speed_limit_060"),
    CANCEL_SPEED_LIMIT_070("cancel_speed_limit_070"),
    CANCEL_SPEED_LIMIT_080("cancel_speed_limit_080"),
    CANCEL_SPEED_LIMIT_090("cancel_speed_limit_090"),
    CANCEL_SPEED_LIMIT_100("cancel_speed_limit_100"),
    CANCEL_SPEED_LIMIT_110("cancel_speed_limit_110"),
    CANCEL_SPEED_LIMIT_120("cancel_speed_limit_120");

    private final String name;
    SignCancelSpeedLimit(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}