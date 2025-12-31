package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum TFType implements StringIdentifiable {
    TRUE("true"),
    FALSE("false");

    private final String name;

    TFType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}