package com.beigu.yunbeiuc.api.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

public interface ResourcePlatform {
    ResourceLocation create(String namespace, String path);

    ResourceLocation parse(String location);

    InputStream openIfPresent(ResourceManager manager, ResourceLocation id) throws IOException;

    List<ResourceLocation> listResources(ResourceManager manager, String path, Predicate<ResourceLocation> filter);
}
