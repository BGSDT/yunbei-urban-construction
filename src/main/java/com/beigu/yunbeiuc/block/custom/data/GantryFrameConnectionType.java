package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum GantryFrameConnectionType implements StringIdentifiable {
    GANTRY_FRAME_CONNECTION_1("gantry_frame_connection_1"),
    GANTRY_FRAME_CONNECTION_2("gantry_frame_connection_2"),
    GANTRY_FRAME_CONNECTION_3("gantry_frame_connection_3"),
    GANTRY_FRAME_CONNECTION_4("gantry_frame_connection_4");

    private final String name;

    GantryFrameConnectionType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}