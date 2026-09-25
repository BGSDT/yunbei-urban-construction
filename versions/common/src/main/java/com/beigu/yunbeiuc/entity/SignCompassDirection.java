package com.beigu.yunbeiuc.entity;

/**
 * 标志牌罗盘方向（原各实体重复定义的内部枚举 Direction 提取而来），
 * NBT 兼容：name 与原枚举一致（north/south/west/east）。
 */
public enum SignCompassDirection {
    NORTH("north"),
    SOUTH("south"),
    WEST("west"),
    EAST("east");

    private final String name;

    SignCompassDirection(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    /** 旧存档兼容解析，兜底值由调用方指定（各牌原枚举兜底不同） */
    public static SignCompassDirection fromName(String name, SignCompassDirection fallback) {
        for (SignCompassDirection dir : values()) {
            if (dir.name.equals(name)) {
                return dir;
            }
        }
        return fallback;
    }
}
