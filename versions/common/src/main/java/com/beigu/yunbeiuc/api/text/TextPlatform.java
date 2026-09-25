package com.beigu.yunbeiuc.api.text;

import net.minecraft.network.chat.MutableComponent;

/** Version-sensitive text component construction. */
public interface TextPlatform {
    MutableComponent literal(String value);

    MutableComponent translatable(String key, Object... arguments);
}
