package com.beigu.yunbeiuc.api.text;

import net.minecraft.network.chat.MutableComponent;

/** Version-sensitive text component construction. */
public interface TextPlatform {
    MutableComponent literal(String value);

    MutableComponent translatable(String key, Object... arguments);

    /** An empty component; 1.16.5-1.18.2 lack {@code Component.empty()} and use {@code TextComponent.EMPTY}. */
    MutableComponent empty();

    /** Parses a lenient JSON component; 1.21 additionally requires a registry provider. */
    net.minecraft.network.chat.Component fromLegacyJson(String json);
}
