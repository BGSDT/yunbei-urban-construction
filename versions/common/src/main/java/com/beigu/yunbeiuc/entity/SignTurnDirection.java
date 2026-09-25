package com.beigu.yunbeiuc.entity;

/**
 * 标志牌转向（原各实体重复定义的内部枚举 Direction 提取而来），
 * NBT 兼容：name 与原枚举一致（left/straight/right）。
 */
public enum SignTurnDirection {
    LEFT("left"),
    STRAIGHT("straight"),
    RIGHT("right");

    private final String name;

    SignTurnDirection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** 旧存档兼容解析，兜底值由调用方指定（各牌原枚举兜底不同） */
    public static SignTurnDirection fromName(String name, SignTurnDirection fallback) {
        for (SignTurnDirection dir : values()) {
            if (dir.name.equals(name)) {
                return dir;
            }
        }
        return fallback;
    }
}
