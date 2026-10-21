package com.beigu.yunbeiuc.signal.mtr;

// 探测运行环境是 MTR 3/YMTR 还是 MTR 4：只查类路径资源，绝不 Class.forName（会提前定义目标类，mixin 永不生效）。
// 标记类：MTR 3 = mtr/data/TrainServer.class，MTR 4 = org/mtr/core/data/Rail.class；不能用 mtr.mixins.json 判断（两版同名）。
// hasClass() 供 mixin 插件筛 targets：MTR 4.0.x 模组层在 org.mtr.mod.*，4.1 起在 org.mtr.*，两种全名都列上再筛。

public enum MtrVersion {

    NONE("未安装"),
    MTR3("MTR 3 / YMTR"),
    MTR4("MTR 4");

    private static final String MTR3_MARKER = "mtr/data/TrainServer.class";
    private static final String MTR4_MARKER = "org/mtr/core/data/Rail.class";

    private static volatile MtrVersion detected;

    private final String displayName;

    MtrVersion(String displayName) {
        this.displayName = displayName;
    }

    public String displayName() {
        return displayName;
    }

    public boolean isPresent() {
        return this != NONE;
    }

    public static MtrVersion detect() {
        final MtrVersion current = detected;
        if (current != null) return current;
        synchronized (MtrVersion.class) {
            if (detected == null) {
                final MtrVersion probed = probe();
                if (probed != NONE) {
                    detected = probed;
                }
                return probed;
            }
            return detected;
        }
    }

    private static MtrVersion probe() {
        if (hasResource(MTR4_MARKER)) return MTR4;
        if (hasResource(MTR3_MARKER)) return MTR3;
        return NONE;
    }

    private static boolean hasResource(String path) {
        try {
            final ClassLoader loader = MtrVersion.class.getClassLoader();
            return loader != null && loader.getResource(path) != null;
        } catch (Throwable t) {
            return false;
        }
    }

    public static boolean hasClass(String className) {
        if (className == null || className.isEmpty()) return false;
        return hasResource(className.replace('.', '/') + ".class");
    }
}
