package com.beigu.yunbeiuc.api.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

/** Opens a resource, returning null when it does not exist. */
public interface ResourcePlatform {
    InputStream openIfPresent(ResourceManager manager, ResourceLocation id) throws IOException;
}
