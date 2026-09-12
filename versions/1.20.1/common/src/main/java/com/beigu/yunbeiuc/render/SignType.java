package com.beigu.yunbeiuc.render;

/**
 * 标志牌安装类型（原 BaseSignRenderer 内部枚举，BaseSignRenderer 移除后提升为顶层类），
 * 由 SignTypeConverter 把各方块的 TYPE 枚举按名称转换为此类型。
 */
public enum SignType {
    POLE_L,
    POLE_H,
    NORMAL
}
