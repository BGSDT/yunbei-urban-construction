package com.beigu.yunbeiuc.signal.mtr;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

// MTR 4 的轨道几何后端：轨道表是 Data#positionsToRail，几何在 Rail#railMath，坐标是 org.mtr.core.data.Position。
// 客户端数据类两种全名都试：4.0.x 在 org.mtr.mod.client，4.1 起在 org.mtr.client。全反射，编译期不依赖 org.mtr.core。

final class Mtr4RailBackend implements MtrRailBackend {

    private static final org.apache.logging.log4j.Logger LOGGER =
            com.beigu.yunbeiuc.YunbeiUrbanConstruction.LOGGER;

    private static boolean initTried;
    private static boolean ready;

    private static final String[] CLIENT_DATA_CLASSES = {
            "org.mtr.client.MinecraftClientData",
            "org.mtr.mod.client.MinecraftClientData"
    };

    private static Method getInstance;
    private static Field positionsToRail;
    private static Constructor<?> newPosition;
    private static Method posGetX, posGetY, posGetZ;
    private static Field railMath;
    private static Method mathGetLength;
    private static Method mathGetPosition;
    private static Method vecX, vecY, vecZ;

    @Override
    public MtrVersion version() {
        return MtrVersion.MTR4;
    }

    private static synchronized boolean init() {
        if (initTried) return ready;
        initTried = true;
        try {
            final Class<?> clientDataClass = findClientDataClass();
            getInstance = clientDataClass.getMethod("getInstance");
            positionsToRail = clientDataClass.getField("positionsToRail");

            final Class<?> positionClass = Class.forName("org.mtr.core.data.Position");
            newPosition = positionClass.getConstructor(long.class, long.class, long.class);
            posGetX = positionClass.getMethod("getX");
            posGetY = positionClass.getMethod("getY");
            posGetZ = positionClass.getMethod("getZ");

            final Class<?> railClass = Class.forName("org.mtr.core.data.Rail");
            railMath = railClass.getField("railMath");

            final Class<?> railMathClass = Class.forName("org.mtr.core.data.RailMath");
            mathGetLength = railMathClass.getMethod("getLength");
            mathGetPosition = railMathClass.getMethod("getPosition", double.class, boolean.class);

            final Class<?> vectorClass = Class.forName("org.mtr.core.tool.Vector");
            vecX = vectorClass.getMethod("x");
            vecY = vectorClass.getMethod("y");
            vecZ = vectorClass.getMethod("z");

            ready = true;
            LOGGER.info("[yunbeiuc] 已连接 MTR 4 轨道 API（{}，Rail#railMath.getLength / getPosition）",
                    clientDataClass.getName());
        } catch (Throwable t) {
            ready = false;
            LOGGER.info("[yunbeiuc] MTR 4 客户端轨道 API 不可用（{}）：轨道瞄准/高亮功能关闭",
                    t.toString());
        }
        return ready;
    }

    private static Class<?> findClientDataClass() throws ClassNotFoundException {
        Throwable last = null;
        for (String name : CLIENT_DATA_CLASSES) {
            try {
                return Class.forName(name);
            } catch (Throwable t) {
                last = t;
            }
        }
        throw new ClassNotFoundException("未找到 MTR 4 的 MinecraftClientData", last);
    }

    private static Map<?, ?> rails() {
        if (!init()) return null;
        try {
            final Object data = getInstance.invoke(null);
            if (data == null) return null;
            final Object value = positionsToRail.get(data);
            return value instanceof Map<?, ?> map ? map : null;
        } catch (Throwable t) {
            return null;
        }
    }

    private static BlockPos toBlockPos(Object position) {
        if (position == null) return null;
        try {
            final long x = (long) posGetX.invoke(position);
            final long y = (long) posGetY.invoke(position);
            final long z = (long) posGetZ.invoke(position);
            return new BlockPos((int) x, (int) y, (int) z);
        } catch (Throwable t) {
            return null;
        }
    }

    private static Object toPosition(BlockPos pos) {
        try {
            return newPosition.newInstance((long) pos.getX(), (long) pos.getY(), (long) pos.getZ());
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public void forEachClientRail(RailVisitor visitor) {
        final Map<?, ?> rails = rails();
        if (rails == null || rails.isEmpty()) return;
        for (Map.Entry<?, ?> outer : rails.entrySet()) {
            final BlockPos start = toBlockPos(outer.getKey());
            if (start == null || !(outer.getValue() instanceof Map<?, ?> inner)) continue;
            for (Map.Entry<?, ?> entry : inner.entrySet()) {
                final BlockPos end = toBlockPos(entry.getKey());
                final Object rail = entry.getValue();
                if (end == null || rail == null) continue;
                visitor.accept(start, end, rail);
            }
        }
    }

    @Override
    public Object findClientRail(BlockPos start, BlockPos end) {
        if (start == null || end == null) return null;
        final Map<?, ?> rails = rails();
        if (rails == null) return null;
        final Object forward = lookup(rails, toPosition(start), toPosition(end));
        return forward != null ? forward : lookup(rails, toPosition(end), toPosition(start));
    }

    private static Object lookup(Map<?, ?> rails, Object a, Object b) {
        if (a == null || b == null) return null;
        final Object inner = rails.get(a);
        return inner instanceof Map<?, ?> map ? map.get(b) : null;
    }

    @Override
    public double length(Object rail) {
        if (rail == null || !init()) return 0.0;
        try {
            final Object math = railMath.get(rail);
            return math == null ? 0.0 : (double) mathGetLength.invoke(math);
        } catch (Throwable t) {
            return 0.0;
        }
    }

    @Override
    public Vec3 positionAt(Object rail, double distance) {
        if (rail == null || !init()) return null;
        try {
            final Object math = railMath.get(rail);
            if (math == null) return null;
            final Object vector = mathGetPosition.invoke(math, distance, false);
            if (vector == null) return null;
            return new Vec3((double) vecX.invoke(vector),
                    (double) vecY.invoke(vector),
                    (double) vecZ.invoke(vector));
        } catch (Throwable t) {
            return null;
        }
    }
}
