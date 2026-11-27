package com.beigu.yunbeiuc.util;

import com.beigu.yunbeiuc.api.resource.Id;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;

/**
 * 左转待行区信号牌动画：三种图案各自按“入场 → 循环 → 收尾”的时间轴播放。
 *
 * <p>帧图放在 {@code assets/yunbeiuc/textures/block/lights/waiting_area_turn_left/} 下，
 * 单帧停留 0.5s（{@link #FRAME_TICKS} tick）：</p>
 * <ul>
 *     <li>全红：red.png（一直显示这一张）</li>
 *     <li>可进入不可左转：enter_1..4（各 0.5s）→ (enter_to_green + enter_4) 循环 3 次，每次 1s</li>
 *     <li>全绿：green_1..4（各 0.5s）→ green_4 保持 3 次共 3s → green_to_red 显示 3s</li>
 * </ul>
 *
 * <p>时间轴完全跟随相位：相位多久就演多久，时间不够播完整段就直接截断，
 * 播完后停在最后一帧。相位延迟由调用方把可用时长缩短后传入，动画随之提前收尾。</p>
 */
public final class WaitingAreaTurnLeftAnimation {
    /** 未单独指定图案时，按方块灯色自动选择。 */
    public static final int PATTERN_AUTO = -1;
    public static final int PATTERN_RED = 0;
    public static final int PATTERN_ENTER = 1;
    public static final int PATTERN_GREEN = 2;

    /** 单帧停留时长：0.5s = 10 tick。 */
    public static final int FRAME_TICKS = 10;

    private static final String TEXTURE_DIR = "textures/block/lights/waiting_area_turn_left/";

    private static final Id RED_FRAME = Id.yunbeiuc(TEXTURE_DIR + "red.png");
    private static final Id ENTER_TO_GREEN_FRAME = Id.yunbeiuc(TEXTURE_DIR + "enter_to_green.png");
    private static final Id GREEN_TO_RED_FRAME = Id.yunbeiuc(TEXTURE_DIR + "green_to_red.png");
    private static final Id[] ENTER_FRAMES = frames("enter", 4);
    private static final Id[] GREEN_FRAMES = frames("green", 4);

    /** 入场 4 帧 × 0.5s。 */
    private static final int ENTER_INTRO_TICKS = ENTER_FRAMES.length * FRAME_TICKS;
    /** (enter_to_green + enter_4) 一次循环 1s。 */
    private static final int ENTER_LOOP_CYCLE_TICKS = 2 * FRAME_TICKS;
    private static final int ENTER_LOOP_CYCLES = 3;

    /** 入场 4 帧 × 0.5s。 */
    private static final int GREEN_INTRO_TICKS = GREEN_FRAMES.length * FRAME_TICKS;
    /** green_4 保持 3 次，每次 1s。 */
    private static final int GREEN_LOOP_TICKS = 3 * 2 * FRAME_TICKS;

    private WaitingAreaTurnLeftAnimation() {
    }

    public static String patternLabel(int pattern) {
        if (pattern == PATTERN_RED) {
            return "全红";
        }
        if (pattern == PATTERN_ENTER) {
            return "可进入不可左转";
        }
        if (pattern == PATTERN_GREEN) {
            return "全绿";
        }
        return "自动(跟随灯色)";
    }

    public static int nextPattern(int pattern) {
        if (pattern == PATTERN_AUTO) {
            return PATTERN_RED;
        }
        if (pattern == PATTERN_RED) {
            return PATTERN_ENTER;
        }
        if (pattern == PATTERN_ENTER) {
            return PATTERN_GREEN;
        }
        return PATTERN_AUTO;
    }

    /**
     * 取当前应显示的动画帧，纯客户端渲染使用。
     *
     * @param pattern      该相位配置的图案；{@link #PATTERN_AUTO} 时按灯色自动选择
     * @param lightState   方块灯色，仅在 pattern 为自动时使用
     * @param elapsedTicks 当前相位已经过的 tick
     * @param cycleActive  是否处于红绿灯组的相位循环中
     * @param activeTicks  当前相位可用的动画时长（相位时长 - 相位延迟），不足时截断
     */
    public static Id frame(int pattern, TrafficLightsBlock.LightState lightState,
                           int elapsedTicks, boolean cycleActive, int activeTicks) {
        int resolved = resolvePattern(pattern, lightState);
        if (resolved == PATTERN_RED) {
            return RED_FRAME;
        }

        int time;
        if (cycleActive) {
            time = Math.max(0, elapsedTicks);
            if (activeTicks > 0 && time >= activeTicks) {
                // 时间不够播完整段，直接截断并停在当前段的最后一帧。
                time = activeTicks - 1;
            }
        } else {
            // 未接入组时按真实时间自由循环，50ms 一 tick。
            time = (int) ((System.currentTimeMillis() / 50L) % 100000L);
        }

        return resolved == PATTERN_GREEN ? greenFrame(time) : enterFrame(time);
    }

    private static int resolvePattern(int pattern, TrafficLightsBlock.LightState lightState) {
        if (pattern == PATTERN_RED || pattern == PATTERN_ENTER || pattern == PATTERN_GREEN) {
            return pattern;
        }
        if (lightState == TrafficLightsBlock.LightState.YELLOW) {
            return PATTERN_ENTER;
        }
        if (lightState == TrafficLightsBlock.LightState.GREEN) {
            return PATTERN_GREEN;
        }
        return PATTERN_RED;
    }

    private static Id enterFrame(int time) {
        if (time < ENTER_INTRO_TICKS) {
            return ENTER_FRAMES[time / FRAME_TICKS];
        }
        int loop = time - ENTER_INTRO_TICKS;
        int loopLength = ENTER_LOOP_CYCLE_TICKS * ENTER_LOOP_CYCLES;
        if (loop < loopLength) {
            int inCycle = loop % ENTER_LOOP_CYCLE_TICKS;
            return inCycle < FRAME_TICKS ? ENTER_TO_GREEN_FRAME : ENTER_FRAMES[ENTER_FRAMES.length - 1];
        }
        return ENTER_FRAMES[ENTER_FRAMES.length - 1];
    }

    private static Id greenFrame(int time) {
        if (time < GREEN_INTRO_TICKS) {
            return GREEN_FRAMES[time / FRAME_TICKS];
        }
        int loop = time - GREEN_INTRO_TICKS;
        if (loop < GREEN_LOOP_TICKS) {
            return GREEN_FRAMES[GREEN_FRAMES.length - 1];
        }
        // green_to_red 显示 3s，之后保持该帧直到相位结束。
        return GREEN_TO_RED_FRAME;
    }

    private static Id[] frames(String prefix, int count) {
        Id[] result = new Id[count];
        for (int i = 0; i < count; i++) {
            result[i] = Id.yunbeiuc(TEXTURE_DIR + prefix + "_" + (i + 1) + ".png");
        }
        return result;
    }
}
