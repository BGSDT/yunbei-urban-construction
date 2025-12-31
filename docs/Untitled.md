```java
package com.beigu.yunbeiuc.block.custom.data;

import net.minecraft.util.StringIdentifiable;

public enum CrashBarrierConcreteType implements StringIdentifiable {
    CRASH_BARRIER_CONCRETE("crash_barrier_concrete"),
    CRASH_BARRIER_CONCRETE_LEFT("crash_barrier_concrete_left"),
    CRASH_BARRIER_CONCRETE_RIGHT("crash_barrier_concrete_right");

    private final String name;

    CrashBarrierConcreteType(String name) {
        this.name = name;
    }

    @Override
    public String asString() {
        return this.name;
    }

    public CrashBarrierConcreteType next() {
        return switch (this) {
            case CRASH_BARRIER_CONCRETE -> CRASH_BARRIER_CONCRETE_LEFT;
            case CRASH_BARRIER_CONCRETE_LEFT -> CRASH_BARRIER_CONCRETE_RIGHT;
            case CRASH_BARRIER_CONCRETE_RIGHT -> CRASH_BARRIER_CONCRETE;
        };
    }

}
```

