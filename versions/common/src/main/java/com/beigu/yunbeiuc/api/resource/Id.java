package com.beigu.yunbeiuc.api.resource;

import java.util.Objects;

// 版本中立 id：26.x 把 ResourceLocation 改名 Identifier 且两者都 final，共享代码无法引用，统一用 Id，转换在 IdAdapter 完成。
public final class Id {
    public static final String DEFAULT_NAMESPACE = "minecraft";

    private final String namespace;
    private final String path;

    private Id(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public static Id of(String namespace, String path) {
        return new Id(namespace, path);
    }

    public static Id yunbeiuc(String path) {
        return new Id("yunbeiuc", path);
    }

    public static Id parse(String value) {
        int split = value.indexOf(':');
        if (split < 0) {
            return new Id(DEFAULT_NAMESPACE, value);
        }
        return new Id(value.substring(0, split), value.substring(split + 1));
    }

    public String namespace() {
        return namespace;
    }

    public String path() {
        return path;
    }

    public Id withPath(String newPath) {
        return new Id(namespace, newPath);
    }

    public Id withPrefix(String prefix) {
        return new Id(namespace, prefix + path);
    }

    public Id withSuffix(String suffix) {
        return new Id(namespace, path + suffix);
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Id id)) {
            return false;
        }
        return namespace.equals(id.namespace) && path.equals(id.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(namespace, path);
    }
}
