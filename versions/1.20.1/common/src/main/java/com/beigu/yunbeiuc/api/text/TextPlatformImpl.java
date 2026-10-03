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
        return net.minecraft.network.chat.Component.Serializer.fromJsonLenient(json);
    }
}