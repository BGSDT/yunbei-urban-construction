package com.beigu.yunbeiuc.api.resource;

import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public final class ResourcePlatformImpl implements ResourcePlatform {
    @Override public InputStream openIfPresent(ResourceManager manager, Id id) throws IOException {
        return manager.getResource(IdAdapter.mc(id)).isPresent() ? manager.open(IdAdapter.mc(id)) : null;
    }

    @Override public List<Id> listResources(ResourceManager manager, String path, Predicate<Id> filter) {
        List<Id> result = new ArrayList<>();
        String base = path == null ? "" : path;
        while (base.endsWith("/")) base = base.substring(0, base.length() - 1);
        String[] candidates = {base, base + "/"};
        for (String candidate : candidates) {
            try {
                for (var mcId : manager.listResources(candidate, name -> true).keySet()) {
                    Id id = IdAdapter.of(mcId);
                    if (filter.test(id) && !result.contains(id)) {
                        result.add(id);
                    }
                }
            } catch (Throwable ignored) {
            }
            if (!result.isEmpty()) break;
        }
        return result;
    }

    @Override public List<String> listNamespaces(ResourceManager manager) {
        List<String> result = new ArrayList<>(manager.getNamespaces());
        result.sort(String::compareTo);
        return result;
    }
    @Override public Id create(String namespace, String path) { return Id.of(namespace, path); }

    @Override public Id parse(String location) { return Id.parse(location); }
}
