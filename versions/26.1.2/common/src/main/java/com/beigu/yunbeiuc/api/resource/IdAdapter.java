package com.beigu.yunbeiuc.api.resource;

import net.minecraft.resources.Identifier;

/**
 * Bridges the version-neutral {@link Id} to this Minecraft version's identifier type
 * ({@code Identifier} before 26.x, {@code Identifier} from 26.x on).
 */
public final class IdAdapter {
    private IdAdapter() {
    }

    public static Identifier mc(Id id) {
        return Identifier.fromNamespaceAndPath(id.namespace(), id.path());
    }

    public static Identifier mc(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }

    public static Id of(Identifier id) {
        return Id.of(id.getNamespace(), id.getPath());
    }
}
