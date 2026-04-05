package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.lights.TrafficLightManager;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.util.WeakHashMap;

public class ModEvents {
    private static final WeakHashMap<ServerWorld, TrafficLightManager> MANAGER_CACHE = new WeakHashMap<>();

    public static void register() {
        ServerWorldEvents.LOAD.register((MinecraftServer server, ServerWorld world) -> {
            TrafficLightManager manager = TrafficLightManager.get(world);
            MANAGER_CACHE.put(world, manager);
            System.out.println("World loaded: " + world.getRegistryKey().getValue() + " - Traffic light manager initialized");
        });

        ServerWorldEvents.UNLOAD.register((MinecraftServer server, ServerWorld world) -> {
            MANAGER_CACHE.remove(world);
        });

        ServerTickEvents.END_WORLD_TICK.register(world -> {
            if (world instanceof ServerWorld serverWorld) {
                TrafficLightManager manager = MANAGER_CACHE.get(serverWorld);
                if (manager == null) {
                    manager = TrafficLightManager.get(serverWorld);
                    MANAGER_CACHE.put(serverWorld, manager);
                }
                manager.tick(serverWorld);
            }
        });
    }
}