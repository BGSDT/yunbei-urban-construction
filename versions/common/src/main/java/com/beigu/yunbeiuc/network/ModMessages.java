package com.beigu.yunbeiuc.network;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import dev.architectury.networking.NetworkManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

public class  ModMessages {

    public static final ResourceLocation UPDATE_FLAG = id("update_flag");
    public static final ResourceLocation UPDATE_ROAD_NAME_SIGN = id("update_road_name_sign");
    public static final ResourceLocation UPDATE_TRAFFIC_LIGHTS = id("update_traffic_lights");
    public static final ResourceLocation UPDATE_TRAFFIC_LIGHTS_TIMING = id("update_traffic_lights_timing");
    public static final ResourceLocation UPDATE_TRAFFIC_LIGHTS_STATIC_STATE = id("update_traffic_lights_static_state");
    public static final ResourceLocation UPDATE_TRAFFIC_LIGHTS_MOUNT_TYPE = id("update_traffic_lights_mount_type");
    public static final ResourceLocation APPLY_TRAFFIC_LIGHTS_PATTERN = id("apply_traffic_lights_pattern");
    public static final ResourceLocation UPDATE_CUSTOM_SIGN = id("update_custom_sign");
    public static final ResourceLocation UPDATE_CUSTOM_SIGN_FIELD = id("update_custom_sign_field");
    public static final ResourceLocation SYNC_TRAFFIC_LIGHTS_GROUP = id("sync_traffic_lights_group");
    private static ResourceLocation id(String path) {
        return VersionServices.resources().create(YunbeiUrbanConstruction.MOD_ID, path);
    }

    /**
     * S2C 接收器。客户端与服务端都会调用 {@code YunbeiUrbanConstruction.init()}，
     * 因此在这里统一注册即可；S2C 包只在 1.16.5 / 1.17.1 会被真正发出。
     */
    @SuppressWarnings("removal")
    public static void registerS2CPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, SYNC_TRAFFIC_LIGHTS_GROUP, (buf, context) -> {
            TrafficLightsGroupSyncPacket packet = new TrafficLightsGroupSyncPacket(buf);
            context.queue(packet::applyClient);
        });
    }

    @SuppressWarnings("removal")
    public static void registerC2SPackets() {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_FLAG, (buf, context) -> {
            FlagUpdatePacket packet = new FlagUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayer) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_ROAD_NAME_SIGN, (buf, context) -> {
            RoadNameSignBlockUpdatePacket packet = new RoadNameSignBlockUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayer) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_TRAFFIC_LIGHTS, (buf, context) -> {
            TrafficLightsUpdatePacket packet = new TrafficLightsUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayer) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_TRAFFIC_LIGHTS_TIMING, (buf, context) -> {
            TrafficLightsTimingUpdatePacket packet = new TrafficLightsTimingUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayer) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_TRAFFIC_LIGHTS_STATIC_STATE, (buf, context) -> {
            TrafficLightsStaticStateUpdatePacket packet = new TrafficLightsStaticStateUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayer) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_TRAFFIC_LIGHTS_MOUNT_TYPE, (buf, context) -> {
            TrafficLightsMountTypeUpdatePacket packet = new TrafficLightsMountTypeUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayer) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, APPLY_TRAFFIC_LIGHTS_PATTERN, (buf, context) -> {
            TrafficLightsPatternApplyPacket packet = new TrafficLightsPatternApplyPacket(buf);
            context.queue(() -> packet.apply((ServerPlayer) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_CUSTOM_SIGN, (buf, context) -> {
            CustomSignUpdatePacket packet = new CustomSignUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayer) context.getPlayer()));
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, UPDATE_CUSTOM_SIGN_FIELD, (buf, context) -> {
            CustomSignFieldUpdatePacket packet = new CustomSignFieldUpdatePacket(buf);
            context.queue(() -> packet.apply((ServerPlayer) context.getPlayer()));
        });
    }
}
