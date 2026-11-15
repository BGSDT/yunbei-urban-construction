package com.beigu.yunbeiuc.api.registry;

// 版本中立注册上下文：26.x 要求属性对象构造前就带上注册 id，靠 ThreadLocal 在 supplier 运行时透传；纯 Java 无 MC 依赖。
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
