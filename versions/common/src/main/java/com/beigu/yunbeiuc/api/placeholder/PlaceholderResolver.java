package com.beigu.yunbeiuc.api.placeholder;

/** Resolves a logical placeholder key without exposing Minecraft text APIs. */
@FunctionalInterface
public interface PlaceholderResolver {
    String resolve(String key);

    default String resolveOrDefault(String key, String fallback) {
        String value = resolve(key);
        return value == null ? fallback : value;
    }
}
