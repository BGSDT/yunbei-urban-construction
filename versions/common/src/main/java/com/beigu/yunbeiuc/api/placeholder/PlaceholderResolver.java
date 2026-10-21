package com.beigu.yunbeiuc.api.placeholder;

@FunctionalInterface
public interface PlaceholderResolver {
    String resolve(String key);

    default String resolveOrDefault(String key, String fallback) {
        String value = resolve(key);
        return value == null ? fallback : value;
    }
}
