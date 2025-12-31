package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum AntiGlareNetPoleType implements StringIdentifiable {
    ANTI_GLARE_NET_POLE("anti_glare_net_pole"),
    ANTI_GLARE_NET_POLE_LEFT("anti_glare_net_pole_left"),
    ANTI_GLARE_NET_POLE_RIGHT("anti_glare_net_pole_right");

    private final String name;

    AntiGlareNetPoleType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }
}