package com.beigu.yunbeiuc.block.custom.lights;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrafficLightManager extends PersistentState {
    private static final String DATA_NAME = "yunbeiuc_traffic_lights";
    private final Map<UUID, TrafficLightGroup> groups = new HashMap<>();
    private ServerWorld world; // 添加世界引用

    public void setWorld(ServerWorld world) {
        this.world = world;
        // 设置世界后，确保所有组都获得世界引用
        for (TrafficLightGroup group : groups.values()) {
            group.setWorld(world);
        }
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList groupsList = new NbtList();
        for (TrafficLightGroup group : groups.values()) {
            groupsList.add(group.toNbt());
        }
        nbt.put("groups", groupsList);
        return nbt;
    }

    public static TrafficLightManager createFromNbt(NbtCompound nbt) {
        TrafficLightManager manager = new TrafficLightManager();
        NbtList groupsList = nbt.getList("groups", 10);
        for (int i = 0; i < groupsList.size(); i++) {
            TrafficLightGroup group = TrafficLightGroup.fromNbt(groupsList.getCompound(i));
            manager.groups.put(group.getGroupId(), group);
        }
        return manager;
    }

    public static TrafficLightManager get(ServerWorld world) {
        PersistentStateManager manager = world.getPersistentStateManager();
        TrafficLightManager trafficLightManager = manager.getOrCreate(
                TrafficLightManager::createFromNbt,
                TrafficLightManager::new,
                DATA_NAME
        );
        // 设置世界引用
        trafficLightManager.setWorld(world);
        return trafficLightManager;
    }

    public void addGroup(TrafficLightGroup group) {
        group.setWorld(world); // 设置世界引用
        groups.put(group.getGroupId(), group);
        markDirty();
        System.out.println("Added group: " + group.getGroupId() + " Total groups: " + groups.size());
    }

    public void removeGroup(UUID groupId) {
        groups.remove(groupId);
        markDirty();
    }

    public TrafficLightGroup getGroup(UUID groupId) {
        return groups.get(groupId);
    }

    public void tick(ServerWorld world) {
        if (groups.isEmpty()) return;

        // System.out.println("Ticking " + groups.size() + " traffic light groups");
        for (TrafficLightGroup group : groups.values()) {
            group.tick(world);
        }
    }

    public int getGroupCount() {
        return groups.size();
    }

    public void clearAll() {
        groups.clear();
        markDirty();
    }
}