package com.beigu.yunbeiuc.entity;

public enum SignTurnDirection {
    LEFT("left"),
    STRAIGHT("straight"),
    RIGHT("right");

    private final String name;

    SignTurnDirection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static SignTurnDirection fromName(String name, SignTurnDirection fallback) {
        for (SignTurnDirection dir : values()) {
            if (dir.name.equals(name)) {
                return dir;
            }
        }
        return fallback;
    }
}
