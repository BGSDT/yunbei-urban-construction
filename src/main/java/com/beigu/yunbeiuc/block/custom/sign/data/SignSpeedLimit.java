package com.beigu.yunbeiuc.block.custom.sign.data;

import net.minecraft.util.StringIdentifiable;

public enum SignSpeedLimit implements StringIdentifiable {
    SPEED_LIMIT_005("speed_limit_005"),
    SPEED_LIMIT_010("speed_limit_010"),
    SPEED_LIMIT_020("speed_limit_020"),
    SPEED_LIMIT_030("speed_limit_030"),
    SPEED_LIMIT_040("speed_limit_040"),
    SPEED_LIMIT_050("speed_limit_050"),
    SPEED_LIMIT_060("speed_limit_060"),
    SPEED_LIMIT_070("speed_limit_070"),
    SPEED_LIMIT_080("speed_limit_080"),
    SPEED_LIMIT_090("speed_limit_090"),
    SPEED_LIMIT_100("speed_limit_100"),
    SPEED_LIMIT_110("speed_limit_110"),
    SPEED_LIMIT_120("speed_limit_120");


    private final String name;
    SignSpeedLimit(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

}
