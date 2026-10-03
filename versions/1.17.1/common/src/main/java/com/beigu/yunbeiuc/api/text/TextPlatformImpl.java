package com.beigu.yunbeiuc.api.text;

import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public final class TextPlatformImpl implements TextPlatform {
    @Override
    public MutableComponent literal(String value) {
        return new TextComponent(value);
    }

    @Override
    public MutableComponent translatable(String key, Object... arguments) {
        return new TranslatableComponent(key, arguments);
    }

    @Override
    public MutableComponent empty() {
        return new TextComponent("");
    }
    @Override
    public net.minecraft.network.chat.Component fromLegacyJson(String json) {
        return net.minecraft.network.chat.Component.Serializer.fromJsonLenient(json);
    }
}