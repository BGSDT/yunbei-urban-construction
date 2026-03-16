package com.beigu.yunbeiuc.block.custom.sign.abandoned.data;

import net.minecraft.util.StringIdentifiable;

public enum SignWidthLimit implements StringIdentifiable {
    SIGN_WIDTH_LIMIT_20("sign_width_limit_20"),
    SIGN_WIDTH_LIMIT_25("sign_width_limit_25"),
    SIGN_WIDTH_LIMIT_30("sign_width_limit_30"),
    SIGN_WIDTH_LIMIT_35("sign_width_limit_35"),
    SIGN_WIDTH_LIMIT_40("sign_width_limit_40"),
    SIGN_WIDTH_LIMIT_45("sign_width_limit_45");

    private final String name;
    
    SignWidthLimit(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}