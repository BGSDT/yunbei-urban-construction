package com.beigu.yunbeiuc.api.input;

/**
 * Version-neutral key constants.
 *
 * <p>26.3 dropped LWJGL GLFW in favour of SDL, so {@code org.lwjgl.glfw.GLFW} is no longer on the
 * classpath. The key codes below are the GLFW values Minecraft has always used for its own input
 * handling, which keeps the shared screens compiling everywhere.</p>
 */
public final class Keys {
    private Keys() {
    }

    public static final int A = 65;
    public static final int C = 67;
    public static final int D = 68;
    public static final int E = 69;
    public static final int F = 70;
    public static final int P = 80;
    public static final int R = 82;
    public static final int S = 83;
    public static final int V = 86;
    public static final int W = 87;

    public static final int ESCAPE = 256;
    public static final int ENTER = 257;
    public static final int DELETE = 261;
    public static final int RIGHT = 262;
    public static final int LEFT = 263;
    public static final int DOWN = 264;
    public static final int UP = 265;
    public static final int PAGE_UP = 266;
    public static final int PAGE_DOWN = 267;

    public static final int LEFT_SHIFT = 340;
    public static final int LEFT_CONTROL = 341;
    public static final int LEFT_ALT = 342;
    public static final int RIGHT_SHIFT = 344;
    public static final int RIGHT_CONTROL = 345;
    public static final int RIGHT_ALT = 346;

    public static final int MOD_CONTROL = 0x0002;
}
