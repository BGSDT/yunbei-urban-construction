package com.beigu.yunbeiuc.api.text;

import net.minecraft.network.chat.MutableComponent;

public interface TextPlatform {
    MutableComponent literal(String value);

    MutableComponent translatable(String key, Object... arguments);

    MutableComponent empty();

    net.minecraft.network.chat.Component fromLegacyJson(String json);
}
