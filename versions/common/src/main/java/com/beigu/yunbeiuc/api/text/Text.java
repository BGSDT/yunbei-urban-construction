package com.beigu.yunbeiuc.api.text;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import net.minecraft.network.chat.MutableComponent;

/** Stable text factories for shared source. */
public final class Text {
    private Text() {}

    public static MutableComponent literal(String value) {
        return VersionServices.text().literal(value);
    }

    public static MutableComponent translatable(String key, Object... arguments) {
        return VersionServices.text().translatable(key, arguments);
    }
}
