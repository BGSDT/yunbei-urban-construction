package com.beigu.yunbeiuc.block.custom.sign.data;

import net.minecraft.util.StringIdentifiable;

public enum SignWeightLimit implements StringIdentifiable {
    SIGN_WEIGHT_LIMIT_10("sign_weight_limit_10"),
    SIGN_WEIGHT_LIMIT_20("sign_weight_limit_20"),
    SIGN_WEIGHT_LIMIT_30("sign_weight_limit_30"),
    SIGN_WEIGHT_LIMIT_40("sign_weight_limit_40"),
    SIGN_ALEX_WEIGHT_LIMIT_10("sign_alex_weight_limit_10"),
    SIGN_ALEX_WEIGHT_LIMIT_20("sign_alex_weight_limit_20"),
    SIGN_ALEX_WEIGHT_LIMIT_30("sign_alex_weight_limit_30");

    private final String name;
    
    SignWeightLimit(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}