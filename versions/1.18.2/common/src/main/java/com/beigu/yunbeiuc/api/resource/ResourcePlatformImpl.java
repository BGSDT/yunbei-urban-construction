package com.beigu.yunbeiuc.api.resource;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.io.InputStream;

public final class ResourcePlatformImpl implements ResourcePlatform {
    @Override public InputStream openIfPresent(ResourceManager manager, ResourceLocation id) throws IOException {
        return manager.hasResource(id) ? manager.getResource(id).getInputStream() : null;
    }
}
