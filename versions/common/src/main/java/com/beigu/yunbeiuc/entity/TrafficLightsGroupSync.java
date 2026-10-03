package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

/**
 * 判断当前版本是否需要显式推送链接组状态。
 *
 * <p>1.16.5 / 1.17.1 的 {@code ClientboundBlockEntityDataPacket} 用 int 表示类型，
 * 客户端 {@code ClientPacketListener#handleBlockEntityData} 只对原版白名单类型
 * （刷怪笼 / 告示牌 / 床 …）调用 {@code BlockEntity#load}；模组方块实体只能填
 * {@code -1}，于是更新标签被整包丢弃，客户端方块实体拿不到 {@code groupId}。
 *
 * <p>1.18.2 起更新包改带 {@code BlockEntityType}，客户端按键取值并 {@code load}，
 * 不再有白名单丢弃问题，因此无需这条额外通道。
 */
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
        // 仅 1.16.5 与 1.17.1 使用 int 型更新包
        return version.startsWith("1.16") || version.startsWith("1.17");
    }
}
