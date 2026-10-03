package com.beigu.yunbeiuc.api.text;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public final class TextPlatformImpl implements TextPlatform {
    @Override
    public MutableComponent literal(String value) {
        return Component.literal(value);
    }

    @Override
    public MutableComponent translatable(String key, Object... arguments) {
        return Component.translatable(key, arguments);
    }

    @Override
    public MutableComponent empty() {
        return Component.empty();
    }
    @Override
    public net.minecraft.network.chat.Component fromLegacyJson(String json) {
        net.minecraft.client.Minecraft client = net.minecraft.client.Minecraft.getInstance();
        net.minecraft.core.HolderLookup.Provider provider =
                client.level != null ? client.level.registryAccess() : net.minecraft.core.RegistryAccess.EMPTY;
        return Component.Serializer.fromJsonLenient(json, provider);
    }
}