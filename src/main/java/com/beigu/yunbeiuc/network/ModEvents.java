package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.lights.TrafficLightManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class ModEvents {
    public static void register() {
        // 世界加载完成事件
        ServerWorldEvents.LOAD.register((MinecraftServer server, ServerWorld world) -> {
            // 确保管理器被初始化并设置世界引用
            TrafficLightManager.get(world);
            System.out.println("World loaded: " + world.getRegistryKey().getValue() + " - Traffic light manager initialized");
        });

        // 每 tick 更新红绿灯
        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world instanceof ServerWorld) {
                ServerWorld serverWorld = (ServerWorld) world;
                TrafficLightManager manager = TrafficLightManager.get(serverWorld);
                manager.tick(serverWorld);
            }
        });
    }
}