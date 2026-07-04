package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.lights.TrafficLightsManager;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.server.world.ServerWorld;

import java.util.WeakHashMap;

public class ModEvents {
    private static final WeakHashMap<ServerWorld, TrafficLightsManager> MANAGER_CACHE = new WeakHashMap<>();

    public static void register() {
        LifecycleEvent.SERVER_LEVEL_LOAD.register(world -> {
            TrafficLightsManager manager = TrafficLightsManager.get(world);
            MANAGER_CACHE.put(world, manager);
            System.out.println("World loaded: " + world.getRegistryKey().getValue() + " - Traffic light manager initialized");
        });

        LifecycleEvent.SERVER_LEVEL_UNLOAD.register(world -> {
            MANAGER_CACHE.remove(world);
        });

        TickEvent.SERVER_LEVEL_POST.register(world -> {
            TrafficLightsManager manager = MANAGER_CACHE.get(world);
            if (manager == null) {
                manager = TrafficLightsManager.get(world);
                MANAGER_CACHE.put(world, manager);
            }
            manager.tick(world);
        });
    }
}
