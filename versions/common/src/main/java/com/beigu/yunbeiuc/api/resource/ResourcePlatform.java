package com.beigu.yunbeiuc.api.resource;

import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

public interface ResourcePlatform {
    Id create(String namespace, String path);

    Id parse(String location);

    InputStream openIfPresent(ResourceManager manager, Id id) throws IOException;

    List<Id> listResources(ResourceManager manager, String path, Predicate<Id> filter);
}
