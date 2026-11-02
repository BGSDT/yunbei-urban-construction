package com.beigu.yunbeiuc.signal.mtr;

// 本 MC 版本是否允许联动 MTR：编译期常量，每个版本目录各一份，共享源码树里只读 ENABLED。
// 关掉时 MtrVersion.detect() 直接返回 NONE → 轨道后端不创建、拦车钩子不初始化、mixin 插件关闭所有 mtr.* / org.mtr.*。
// 1.16.5~1.20.1 = true；1.20.4 / 1.21.1 = false（按要求停用 MTR3/MTR4 联动与 mixin）。

public final class MtrSupport {

    public static final boolean ENABLED = false;

    private MtrSupport() {
    }
}
