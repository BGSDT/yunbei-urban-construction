package com.beigu.yunbeiuc.api.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.function.Predicate;

/** Opens a resource, returning null when it does not exist. */
public interface ResourcePlatform {
    /** Builds a namespaced resource id: 1.21 made the ResourceLocation constructor private. */
    ResourceLocation create(String namespace, String path);

    /** Parses "{@code namespace:path}" (1.21 made the single-argument constructor private). */
    ResourceLocation parse(String location);

    InputStream openIfPresent(ResourceManager manager, ResourceLocation id) throws IOException;

    /**
     * Lists the ids of every resource under the given path that passes the filter.
     *
     * <p>1.16.5-1.18.2 return a {@code Collection&lt;ResourceLocation&gt;} filtered by a
     * {@code Predicate&lt;String&gt;} of the path, while 1.19.2+ return a
     * {@code Map&lt;ResourceLocation, Resource&gt;} filtered by a
     * {@code Predicate&lt;ResourceLocation&gt;}; this method normalizes both to a plain id list.
     */
    List<ResourceLocation> listResources(ResourceManager manager, String path, Predicate<ResourceLocation> filter);
}
