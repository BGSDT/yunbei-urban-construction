package com.beigu.yunbeiuc.api.registry;

/**
 * Version neutral registration context.
 *
 * <p>Minecraft 26.x requires every {@code BlockBehaviour.Properties} and
 * {@code Item.Properties} instance to carry its registry id before the object is constructed
 * ({@code "Block id not set"} / {@code "Item id not set"}). The id can only be stamped while the
 * deferred supplier runs, because that is the moment the properties object is created and the
 * registry name is known. The shared registration helpers therefore publish the current name here
 * and the 26.x compatibility layer reads it back.</p>
 *
 * <p>The holder is pure Java and has no Minecraft dependency, so it is harmless on older
 * versions where nothing reads it.</p>
 */
public final class RegistrationContext {
    private static final ThreadLocal<String> ITEM_NAME = new ThreadLocal<>();

    private RegistrationContext() {
    }

    public static void beginItem(String name) {
        ITEM_NAME.set(name);
    }

    public static void endItem() {
        ITEM_NAME.remove();
    }

    public static String currentItem() {
        return ITEM_NAME.get();
    }
}
