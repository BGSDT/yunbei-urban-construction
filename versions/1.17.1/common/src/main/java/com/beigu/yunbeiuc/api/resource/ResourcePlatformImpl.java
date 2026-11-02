package com.beigu.yunbeiuc.api.resource;

import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class ResourcePlatformImpl implements ResourcePlatform {
    @Override public InputStream openIfPresent(ResourceManager manager, Id id) throws IOException {
        return manager.hasResource(IdAdapter.mc(id))
                ? manager.getResource(IdAdapter.mc(id)).getInputStream()
                : null;
    }

    @Override public List<Id> listResources(ResourceManager manager, String path, Predicate<Id> filter) {
        List<Id> result = new ArrayList<>();
        for (var mcId : manager.listResources(path, name -> true)) {
            Id id = IdAdapter.of(mcId);
            if (filter.test(id)) {
                result.add(id);
            }
        }
        return result;
    }

    @Override public Id create(String namespace, String path) { return Id.of(namespace, path); }

    @Override public Id parse(String location) { return Id.parse(location); }
}
