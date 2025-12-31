package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum RoadRailingsIronType implements StringIdentifiable {
    ROAD_RAILINGS_IRON("road_railings_iron"),
    ROAD_RAILINGS_IRON_POLE("road_railings_iron_pole"),
    ROAD_RAILINGS_IRON_OBLIQUE("road_railings_iron_oblique");

    private final String name;

    RoadRailingsIronType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public RoadRailingsIronType next() {
        return switch (this) {
            case ROAD_RAILINGS_IRON -> ROAD_RAILINGS_IRON_POLE;
            case ROAD_RAILINGS_IRON_POLE -> ROAD_RAILINGS_IRON_OBLIQUE;
            case ROAD_RAILINGS_IRON_OBLIQUE -> ROAD_RAILINGS_IRON;
        };
    }

}