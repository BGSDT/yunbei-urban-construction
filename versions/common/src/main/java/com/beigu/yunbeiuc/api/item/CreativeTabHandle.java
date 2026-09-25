package com.beigu.yunbeiuc.api.item;

/** Opaque handle for creative tabs whose platform type changed in 1.19.3. */
public final class CreativeTabHandle {
    private final Object value;

    public CreativeTabHandle(Object value) {
        this.value = value;
    }

    public Object value() {
        return value;
    }
}
