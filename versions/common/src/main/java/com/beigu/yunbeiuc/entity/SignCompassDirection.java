package com.beigu.yunbeiuc.entity;

public enum SignCompassDirection {
    NORTH("north"),
    SOUTH("south"),
    WEST("west"),
    EAST("east");

    private final String name;

    SignCompassDirection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SignCompassDirection fromName(String name, SignCompassDirection fallback) {
        for (SignCompassDirection dir : values()) {
            if (dir.name.equals(name)) {
                return dir;
            }
        }
        return fallback;
    }
}
