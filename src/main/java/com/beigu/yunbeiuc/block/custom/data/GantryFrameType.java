package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum GantryFrameType implements StringIdentifiable {
    GANTRY_FRAME_SIDE_1("gantry_frame_side_1"),
    GANTRY_FRAME_SIDE_2("gantry_frame_side_2");

    private final String name;

    GantryFrameType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}