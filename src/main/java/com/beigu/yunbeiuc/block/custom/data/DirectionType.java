package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum DirectionType implements StringIdentifiable {
    NORTH("north"),
    EAST("east"),
    SOUTH("south"),
    WEST("west");

    private final String name;

    DirectionType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public DirectionType next() {
        return switch (this) {
            case NORTH -> EAST;
            case EAST -> SOUTH;
            case SOUTH -> WEST;
            case WEST -> NORTH;
        };
    }

}