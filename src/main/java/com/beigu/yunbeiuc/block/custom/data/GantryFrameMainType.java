package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum GantryFrameMainType implements StringIdentifiable {
    GANTRY_FRAME_MAIN_1("gantry_frame_main_1"),
    GANTRY_FRAME_MAIN_2("gantry_frame_main_2");

    private final String name;

    GantryFrameMainType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}