package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum SignNoSpecial implements StringIdentifiable {
    SIGN_STOP("sign_stop"),
    SIGN_YIELD("sign_yield"),
    SIGN_YIELD_TO_ONCOMING_TRAFFIC("sign_yield_to_oncoming_traffic"),
    SIGN_NO_HONK_HORN("sign_no_honk_horn"),
    SIGN_CHECK("sign_check"),
    SIGN_PORT_CHECK("sign_port_check");

    private final String name;

    SignNoSpecial(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String asString() {
        return this.name;
    }
}