package com.beigu.yunbeiuc.api.resource;

import net.minecraft.resources.ResourceLocation;

// Bridges the version-neutral {@link Id} to this Minecraft version's identifier type.
public final class IdAdapter {
    private IdAdapter() {
    }

    public static ResourceLocation mc(Id id) {
        return new ResourceLocation(id.namespace(), id.path());
    }

    public static ResourceLocation mc(String namespace, String path) {
        return new ResourceLocation(namespace, path);
    }

    public static Id of(ResourceLocation id) {
        return Id.of(id.getNamespace(), id.getPath());
    }
}
