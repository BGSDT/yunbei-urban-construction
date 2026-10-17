package com.beigu.yunbeiuc.signal;

/**
 * 轨道「红绿灯状态」的颜色。
 *
 * <p>这里**不是**给每个相位分配一个专属颜色，而是按「这条轨道此刻放不放行」上色，
 * 也就是跟红绿灯一样：
 * <ul>
 *   <li><b>绿</b> {@link #RELEASED}：当前相位轮到这条轨道，放行；</li>
 *   <li><b>黄</b> {@link #WARNING}：当前相位放行它，但已经进黄灯段（最后 3 秒），马上要变红；</li>
 *   <li><b>红</b> {@link #HELD}：当前相位没轮到这条轨道（或一个相位都没勾），不放行；</li>
 *   <li><b>灰白</b> {@link #UNKNOWN}：判断不了——还没选中相位组，或这条轨道没链接到本组。</li>
 * </ul>
 *
 * <p>状态本身由 {@code TrafficLightsBlockEntity#getRailLightState} 按灯同一套逻辑算出来
 * （见 {@code TrafficLightsBlockEntity#updateLightState}），所以轨道上的颜色与那盏灯的
 * 灯色是同步变化的。
 *
 * <p>界面（{@code RailPhaseScreen}）里也复用这几个颜色：某一行勾上了就是「该相位放行这条轨道」，
 * 显示绿色；没勾就是红色。于是「界面里勾了哪几个相位」与「世界里这条轨道什么颜色」用的是同一套语义。
 */
public final class PhaseColors {

    /** 放行（绿灯）。 */
    public static final int RELEASED = 0xFF3CDE3C;
    /** 放行但已进黄灯段，即将变红（黄灯）。 */
    public static final int WARNING = 0xFFFFCC22;
    /** 不放行（红灯）。 */
    public static final int HELD = 0xFFF03535;
    /** 判断不了（没选中相位组 / 这条轨道没链接到本组）：中性灰白。 */
    public static final int UNKNOWN = 0xFFE0E0E0;

    private PhaseColors() {
    }

    /** 把 ARGB 拆成 0..1 的红色分量，供顶点着色使用。 */
    public static float red(int argb) {
        return ((argb >> 16) & 0xFF) / 255.0f;
    }

    /** 把 ARGB 拆成 0..1 的绿色分量，供顶点着色使用。 */
    public static float green(int argb) {
        return ((argb >> 8) & 0xFF) / 255.0f;
    }

    /** 把 ARGB 拆成 0..1 的蓝色分量，供顶点着色使用。 */
    public static float blue(int argb) {
        return (argb & 0xFF) / 255.0f;
    }
}
