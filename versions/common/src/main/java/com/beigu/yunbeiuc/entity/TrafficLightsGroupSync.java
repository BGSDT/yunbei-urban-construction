package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

// 1.16.5 / 1.17.1 需要显式推送链接组状态：这两个版本的方块实体更新包只对原版白名单类型调用 load，模组方块实体会被整包丢弃。
// 1.18.2 起更新包带 BlockEntityType，无需这条通道。

public final class TrafficLightsGroupSync {
    private static final boolean REQUIRED = computeRequired();

    private TrafficLightsGroupSync() {
    }

    public static boolean isRequired() {
        return REQUIRED;
    }

    private static boolean computeRequired() {
        String version = VersionServices.minecraftVersion();
        if (version == null) return false;
        return version.startsWith("1.16") || version.startsWith("1.17");
    }
}
