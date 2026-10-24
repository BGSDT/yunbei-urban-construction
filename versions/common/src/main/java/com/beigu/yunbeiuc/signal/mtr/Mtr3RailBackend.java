package com.beigu.yunbeiuc.signal.mtr;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

// MTR 3 / YMTR 的轨道几何后端：客户端读 mtr.client.ClientData.RAILS，服务端读 RailwayData#getRailsMap()。
// 中心线用 Rail#getLength + Rail#getPosition，与 MTR 自己画轨道带的曲线同一套，所以高亮线在哪射线就打在哪。

final class Mtr3RailBackend implements MtrRailBackend {

    private static boolean railApiTried;
    private static boolean railApiReady;
    private static Method getInstance;
    private static Method getRailsMap;
    private static Method railGetLength;
    private static Method railGetPosition;

    private static boolean clientApiTried;
    private static Field clientRailsField;

    @Override
    public MtrVersion version() {
        return MtrVersion.MTR3;
    }

    private static synchronized boolean initRailApi() {
        if (railApiTried) return railApiReady;
        railApiTried = true;
        try {
            final Class<?> dataClass = Class.forName("mtr.data.RailwayData");
            final Class<?> railClass = Class.forName("mtr.data.Rail");
            getInstance = dataClass.getMethod("getInstance", Level.class);
            getRailsMap = dataClass.getMethod("getRailsMap");
            railGetLength = railClass.getMethod("getLength");
            railGetPosition = railClass.getMethod("getPosition", double.class);
            railApiReady = true;
        } catch (Throwable t) {
            getInstance = null;
            getRailsMap = null;
            railGetLength = null;
            railGetPosition = null;
            railApiReady = false;
        }
        return railApiReady;
    }

    private static synchronized Field initClientApi() {
        if (clientApiTried) return clientRailsField;
        clientApiTried = true;
        try {
            clientRailsField = Class.forName("mtr.client.ClientData").getField("RAILS");
        } catch (Throwable t) {
            clientRailsField = null;
        }
        return clientRailsField;
    }

    @SuppressWarnings("unchecked")
    private static Map<BlockPos, Map<BlockPos, Object>> clientRails() {
        final Field field = initClientApi();
        if (field == null) return null;
        try {
            final Object value = field.get(null);
            if (value instanceof Map<?, ?> map) {
                return (Map<BlockPos, Map<BlockPos, Object>>) map;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private static Map<BlockPos, Map<BlockPos, Object>> serverRails(Level level) {
        if (!initRailApi() || level == null) return null;
        try {
            final Object data = getInstance.invoke(null, level);
            if (data == null) return null;
            final Object mapObject = getRailsMap.invoke(data);
            if (mapObject instanceof Map<?, ?> map) {
                return (Map<BlockPos, Map<BlockPos, Object>>) map;
            }
        } catch (Throwable t) {
            // 读不到就当作没有轨道
        }
        return null;
    }

    @Override
    public void forEachClientRail(RailVisitor visitor) {
        forEach(clientRails(), visitor);
    }

    @Override
    public boolean forEachServerRail(Level level, RailVisitor visitor) {
        return forEach(serverRails(level), visitor);
    }

    private static boolean forEach(Map<BlockPos, Map<BlockPos, Object>> rails, RailVisitor visitor) {
        if (rails == null || rails.isEmpty()) return false;
        for (Map.Entry<BlockPos, Map<BlockPos, Object>> outer : rails.entrySet()) {
            final BlockPos start = outer.getKey();
            final Map<BlockPos, Object> inner = outer.getValue();
            if (start == null || inner == null) continue;
            for (Map.Entry<BlockPos, Object> entry : inner.entrySet()) {
                final BlockPos end = entry.getKey();
                final Object rail = entry.getValue();
                if (end == null || rail == null) continue;
                visitor.accept(start, end, rail);
            }
        }
        return true;
    }

    @Override
    public Object findClientRail(BlockPos start, BlockPos end) {
        if (start == null || end == null) return null;
        final Map<BlockPos, Map<BlockPos, Object>> rails = clientRails();
        if (rails == null) return null;
        final Object forward = lookup(rails, start, end);
        return forward != null ? forward : lookup(rails, end, start);
    }

    private static Object lookup(Map<BlockPos, Map<BlockPos, Object>> rails, BlockPos a, BlockPos b) {
        final Map<BlockPos, Object> inner = rails.get(a);
        return inner == null ? null : inner.get(b);
    }

    @Override
    public double length(Object rail) {
        if (rail == null || !initRailApi()) return 0.0;
        try {
            return (double) railGetLength.invoke(rail);
        } catch (Throwable t) {
            return 0.0;
        }
    }

    @Override
    public Vec3 positionAt(Object rail, double distance) {
        if (rail == null || !initRailApi()) return null;
        try {
            return (Vec3) railGetPosition.invoke(rail, distance);
        } catch (Throwable t) {
            return null;
        }
    }
}
