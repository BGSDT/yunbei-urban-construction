package com.beigu.yunbeiuc.signal.mtr;

import com.beigu.yunbeiuc.signal.RailSignalRegistry;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

/**
 * MTR 列车钩子的实现体（纯反射，编译期不依赖 MTR）。
 *
 * <p>MTR 的列车在推进时会对「前方轨道索引」调用 {@code TrainServer#isRailBlocked(int)}：
 * 返回 {@code true} 就表示前方受阻，列车会据此把停车点设到该段之前并制动。
 * 本模组通过一个可选 mixin 在该方法头部插入本类的判定，从而让红绿灯相位
 * 能真正拦停列车。
 *
 * <p>为了让 mixin 本体不需要引用 MTR 的类型，这里用反射读取
 * {@code mtr.data.Train#path}（public final）与 {@code mtr.path.PathData#getRailProduct()}
 * 拿到当前轨道的标识 UUID，再查 {@link RailSignalRegistry}。
 *
 * <p>MTR 不存在时 {@link #init()} 会失败并令所有判定返回 {@code false}，
 * 绝不拦截、也绝不抛异常。
 */
public final class RailSignalHook {

    private static boolean initialised;
    private static Field pathField;
    private static Method getRailProduct;

    private RailSignalHook() {
    }

    private static synchronized void init() {
        if (initialised) return;
        initialised = true;
        try {
            final Class<?> trainClass = Class.forName("mtr.data.Train");
            pathField = findField(trainClass, "path");
            final Class<?> pathDataClass = Class.forName("mtr.path.PathData");
            getRailProduct = pathDataClass.getMethod("getRailProduct");
        } catch (Throwable t) {
            pathField = null;
            getRailProduct = null;
        }
        // 只在首次判定时输出一次：便于从启动日志直接确认 MTR 联动是否真的挂上了。
        // 本方法是在列车运行时被调用（不在任何类的转换过程中），此处 Class.forName 是安全的。
        if (pathField != null && getRailProduct != null) {
            com.beigu.yunbeiuc.YunbeiUrbanConstruction.LOGGER.info(
                    "[yunbeiuc] MTR 轨道信号钩子已启用：红绿灯相位可拦停列车");
        } else {
            com.beigu.yunbeiuc.YunbeiUrbanConstruction.LOGGER.warn(
                    "[yunbeiuc] 未找到 MTR 的 Train/PathData，轨道信号联动不会生效");
        }
    }

    /** 沿继承链查找字段（MTR 各版本对 {@code path} 的可见性可能不同）。 */
    private static Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null) {
            try {
                final Field field = current.getDeclaredField(name);
                try {
                    field.setAccessible(true);
                } catch (Throwable ignored) {
                    // 无法开放访问时仍尝试使用（public 字段本就可读）
                }
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    /** MTR 是否可用（供调试/日志）。 */
    public static boolean isAvailable() {
        init();
        return pathField != null && getRailProduct != null;
    }

    /**
     * 判断列车前方第 {@code checkIndex} 段轨道当前是否为红灯。
     *
     * @param trainServer MTR 的 {@code TrainServer} 实例（mixin 里的 {@code this}）
     * @param checkIndex  前方轨道在路径中的索引
     * @return {@code true} = 红灯，应拦停；任何异常或未知轨道一律 {@code false}
     */
    public static boolean isBlocked(Object trainServer, int checkIndex) {
        init();
        if (pathField == null || getRailProduct == null || trainServer == null) {
            return false;
        }
        try {
            final Object pathObject = pathField.get(trainServer);
            if (!(pathObject instanceof List<?> path)) {
                return false;
            }
            if (checkIndex < 0 || checkIndex >= path.size()) {
                return false;
            }
            final Object pathData = path.get(checkIndex);
            if (pathData == null) {
                return false;
            }
            final Object railProduct = getRailProduct.invoke(pathData);
            if (!(railProduct instanceof UUID uuid)) {
                return false;
            }
            return RailSignalRegistry.isBlocked(uuid);
        } catch (Throwable t) {
            return false;
        }
    }
}
