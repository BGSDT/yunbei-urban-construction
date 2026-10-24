package com.beigu.yunbeiuc.signal.mtr;

import com.beigu.yunbeiuc.signal.RailPhaseBinding;
import com.beigu.yunbeiuc.signal.RailSignalRegistry;

import net.minecraft.core.BlockPos;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

// MTR 列车钩子的实现体（纯反射）：MTR 3 挂 TrainServer#isRailBlocked，MTR 4 挂 Vehicle#checkAndBlockSignal。
// MTR 4 不能只挂 PathData#isSignalBlocked —— 那条路只在轨道配了 MTR 原生信号色时才会被调用。
// 只拦「还没进入这条轨道」的车：车头 railProgress >= 入口距离就放行，否则绿灯时已进道口的车会被硬刹在中间。

public final class RailSignalHook {

    private static boolean initialised;

    private static Field pathField;
    private static Method getRailProduct;

    private static Field extraDataField;
    private static Field immutablePathField;
    private static Method getOrderedPosition1;
    private static Method getOrderedPosition2;
    private static Method posGetX, posGetY, posGetZ;
    private static Field railProgressField;
    private static Method getStartDistance;

    private static final double ENTER_EPSILON = 0.001;

    private RailSignalHook() {
    }

    private static synchronized void init() {
        if (initialised) return;
        initialised = true;
        switch (MtrVersion.detect()) {
            case MTR4:
                initMtr4();
                break;
            case MTR3:
                initMtr3();
                break;
            default:
                break;
        }
    }

    private static void initMtr3() {
        try {
            final Class<?> trainClass = Class.forName("mtr.data.Train");
            pathField = findField(trainClass, "path");
            final Class<?> pathDataClass = Class.forName("mtr.path.PathData");
            getRailProduct = pathDataClass.getMethod("getRailProduct");
        } catch (Throwable t) {
            pathField = null;
            getRailProduct = null;
        }
    }

    private static void initMtr4() {
        try {
            final Class<?> vehicleClass = Class.forName("org.mtr.core.data.Vehicle");
            extraDataField = vehicleClass.getField("vehicleExtraData");
            final Class<?> extraDataClass = Class.forName("org.mtr.core.data.VehicleExtraData");
            immutablePathField = extraDataClass.getField("immutablePath");

            final Class<?> pathDataClass = Class.forName("org.mtr.core.data.PathData");
            getOrderedPosition1 = pathDataClass.getMethod("getOrderedPosition1");
            getOrderedPosition2 = pathDataClass.getMethod("getOrderedPosition2");
            getStartDistance = pathDataClass.getMethod("getStartDistance");

            railProgressField = findField(vehicleClass, "railProgress");

            final Class<?> positionClass = Class.forName("org.mtr.core.data.Position");
            posGetX = positionClass.getMethod("getX");
            posGetY = positionClass.getMethod("getY");
            posGetZ = positionClass.getMethod("getZ");
        } catch (Throwable t) {
            extraDataField = null;
            immutablePathField = null;
            getOrderedPosition1 = null;
            getOrderedPosition2 = null;
            posGetX = null;
            posGetY = null;
            posGetZ = null;
            railProgressField = null;
            getStartDistance = null;
        }
    }

    private static Field findField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null) {
            try {
                final Field field = current.getDeclaredField(name);
                try {
                    field.setAccessible(true);
                } catch (Throwable ignored) {
                }
                return field;
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }

    public static boolean isAvailable() {
        init();
        return (pathField != null && getRailProduct != null)
                || (extraDataField != null && immutablePathField != null);
    }

    public static boolean isBlocked(Object trainServer, int checkIndex) {
        if (RailSignalRegistry.size() == 0) return false;
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

    public static boolean isBlockedVehicle(Object vehicle, int index) {
        if (RailSignalRegistry.size() == 0) return false;
        init();
        if (extraDataField == null || immutablePathField == null || vehicle == null) {
            return false;
        }
        try {
            final Object extraData = extraDataField.get(vehicle);
            if (extraData == null) return false;
            final Object pathObject = immutablePathField.get(extraData);
            if (!(pathObject instanceof List<?> path)) return false;
            if (index < 0 || index >= path.size()) return false;

            final Object pathData = path.get(index);
            if (pathData == null) return false;

            final UUID rail = railOf(pathData);
            if (rail == null) return false;
            if (!RailSignalRegistry.isBlocked(rail)) return false;
            return !isAlreadyOnRail(path, index, rail, vehicle);
        } catch (Throwable t) {
            return false;
        }
    }

    private static boolean isAlreadyOnRail(List<?> path, int index, UUID rail, Object vehicle) {
        if (railProgressField == null) return false;
        final double progress;
        try {
            progress = railProgressField.getDouble(vehicle);
        } catch (Throwable t) {
            return false;
        }
        if (Double.isNaN(progress) || Double.isInfinite(progress)) return false;

        int first = index;
        while (first > 0) {
            final Object previous = path.get(first - 1);
            if (previous == null || !rail.equals(railOf(previous))) break;
            first--;
        }

        final double entry = startDistanceOf(path.get(first));
        if (Double.isNaN(entry)) return false;
        return progress >= entry - ENTER_EPSILON;
    }

    private static UUID railOf(Object pathData) {
        if (pathData == null || getOrderedPosition1 == null || getOrderedPosition2 == null) return null;
        try {
            final BlockPos start = toBlockPos(getOrderedPosition1.invoke(pathData));
            final BlockPos end = toBlockPos(getOrderedPosition2.invoke(pathData));
            if (start == null || end == null) return null;
            return RailPhaseBinding.railProductOf(start, end);
        } catch (Throwable t) {
            return null;
        }
    }

    private static double startDistanceOf(Object pathData) {
        if (pathData == null || getStartDistance == null) return Double.NaN;
        try {
            return (double) getStartDistance.invoke(pathData);
        } catch (Throwable t) {
            return Double.NaN;
        }
    }

    private static BlockPos toBlockPos(Object position) {
        if (position == null || posGetX == null) return null;
        try {
            final long x = (long) posGetX.invoke(position);
            final long y = (long) posGetY.invoke(position);
            final long z = (long) posGetZ.invoke(position);
            return new BlockPos((int) x, (int) y, (int) z);
        } catch (Throwable t) {
            return null;
        }
    }
}
