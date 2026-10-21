package com.beigu.yunbeiuc.signal;

// 轨道颜色 = 这条轨道此刻的红绿灯状态：绿放行 / 黄即将变红 / 红不放行 / 灰白无法判定。
// 不是「每个相位一种颜色」；状态由 TrafficLightsBlockEntity#getRailLightState 算，与那盏灯同步变化。

public final class PhaseColors {

    public static final int RELEASED = 0xFF3CDE3C;
    public static final int WARNING = 0xFFFFCC22;
    public static final int HELD = 0xFFF03535;
    public static final int UNKNOWN = 0xFFE0E0E0;

    private PhaseColors() {
    }

    public static float red(int argb) {
        return ((argb >> 16) & 0xFF) / 255.0f;
    }

    public static float green(int argb) {
        return ((argb >> 8) & 0xFF) / 255.0f;
    }

    public static float blue(int argb) {
        return (argb & 0xFF) / 255.0f;
    }
}
