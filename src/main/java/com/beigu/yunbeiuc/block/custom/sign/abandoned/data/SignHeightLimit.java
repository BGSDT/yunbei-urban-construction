package com.beigu.yunbeiuc.block.custom.sign.abandoned.data;

import net.minecraft.util.StringIdentifiable;

public enum SignHeightLimit implements StringIdentifiable {
    HEIGHT_LIMIT_20("height_limit_20"),  // 对应2.0米
    HEIGHT_LIMIT_25("height_limit_25"),  // 对应2.5米
    HEIGHT_LIMIT_30("height_limit_30"),  // 对应3.0米
    HEIGHT_LIMIT_35("height_limit_35"),  // 对应3.5米
    HEIGHT_LIMIT_40("height_limit_40"),  // 对应4.0米
    HEIGHT_LIMIT_45("height_limit_45");  // 对应4.5米

    private final String name;

    SignHeightLimit(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}