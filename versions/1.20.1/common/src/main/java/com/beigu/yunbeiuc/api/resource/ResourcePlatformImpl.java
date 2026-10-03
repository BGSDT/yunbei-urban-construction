package com.beigu.yunbeiuc.api.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class ResourcePlatformImpl implements ResourcePlatform {
    @Override public InputStream openIfPresent(ResourceManager manager, ResourceLocation id) throws IOException {
        return manager.getResource(id).isPresent() ? manager.open(id) : null;
    }

    @Override public List<ResourceLocation> listResources(ResourceManager manager, String path, Predicate<ResourceLocation> filter) {
        List<ResourceLocation> result = new ArrayList<>();
        for (ResourceLocation id : manager.listResources(path, filter).keySet()) {
            result.add(id);
        }
        return result;
    }
    @Override public ResourceLocation create(String namespace, String path) { return new ResourceLocation(namespace, path); }
    @Override public ResourceLocation parse(String location) { return new ResourceLocation(location); }
}