package com.beigu.yunbeiuc.signal.mtr;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 用玩家视线「瞄准」一条 MTR 轨道（纯反射，不依赖 MTR 的类）。
 *
 * <p>MTR 的轨道不是方块，而是由一张 {@code Map<BlockPos, Map<BlockPos, Rail>>}
 * 描述的、两端节点之间的**曲线**。玩家能看到的「轨道中间那条线」就是
 * {@code Rail#render} 沿这条曲线画出来的带子，本类用同一套几何：
 * {@code Rail#getLength()} + {@code Rail#getPosition(double)} 得到的就是那条中心线，
 * 因此玩家看到的高亮线在哪，射线就打在哪。
 *
 * <p>轨道表有两个来源：
 * <ul>
 *   <li><b>客户端</b>：{@code mtr.client.ClientData.RAILS}（静态字段，MTR 同步给客户端的全量轨道）。
 *       瞄准与高亮都在客户端做，所见即所得，且不占用服务端 tick。</li>
 *   <li><b>服务端</b>：{@code RailwayData.getInstance(Level)#getRailsMap()}（存档里的轨道表），
 *       作为备用；正常情况下界面由客户端发来的瞄准结果驱动，不走这条路。</li>
 * </ul>
 *
 * <p>{@code Rail#getLength()} 与 {@code Rail#getPosition(double)} 都是 {@code mtr.data.Rail}
 * 上的 public 方法，返回原版 {@link Vec3}（不是 MTR 的类），所以只有
 * {@code RailwayData} / {@code Rail} / {@code ClientData} 三个**类型名**需要反射，
 * 未安装 MTR 时一切返回 {@code null} / 空表。
 */
public final class RailRaycast {

    /** 命中的一条轨道。{@code rail} 是 MTR 的 {@code Rail} 实例，供客户端画高亮线采样用。 */
    public static final class Hit {
        public final BlockPos start;
        public final BlockPos end;
        public final Object rail;
        public final double distance;

        Hit(BlockPos start, BlockPos end, Object rail, double distance) {
            this.start = start;
            this.end = end;
            this.rail = rail;
            this.distance = distance;
        }
    }

    /** 射线检测的采样步长（方块）。越小越精确，0.5 已足够。 */
    private static final double SAMPLE_STEP = 0.5;
    /** 画高亮线时的采样步长（方块）。比射线检测更细，曲线才平滑。 */
    private static final double CENTERLINE_STEP = 0.25;

    private static final org.apache.logging.log4j.Logger LOGGER =
            com.beigu.yunbeiuc.YunbeiUrbanConstruction.LOGGER;

    // ---- mtr.data.Rail / mtr.data.RailwayData ----
    private static boolean railApiTried;
    private static boolean railApiReady;
    private static Method getInstance;    // RailwayData.getInstance(Level)
    private static Method getRailsMap;    // RailwayData#getRailsMap()
    private static Method railGetLength;  // Rail#getLength()
    private static Method railGetPosition; // Rail#getPosition(double)

    // ---- mtr.client.ClientData ----
    private static boolean clientApiTried;
    private static Field clientRailsField; // ClientData.RAILS

    /** 上一次打日志的轨道，用来把「每帧都在瞄准」压成「瞄准目标变化时才打一条」。 */
    private static Object lastLoggedRail;
    private static boolean lastLoggedNull = true;

    private RailRaycast() {
    }

    // ------------------------------------------------------------------ 反射准备

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
            LOGGER.info("[yunbeiuc] 已连接 MTR 轨道 API（Rail#getLength / Rail#getPosition）");
        } catch (Throwable t) {
            getInstance = null;
            getRailsMap = null;
            railGetLength = null;
            railGetPosition = null;
            railApiReady = false;
            LOGGER.info("[yunbeiuc] 未找到 MTR 轨道 API，瞄准轨道功能不可用（未安装 MTR？）：{}", t.toString());
        }
        return railApiReady;
    }

    private static synchronized Field initClientApi() {
        if (clientApiTried) return clientRailsField;
        clientApiTried = true;
        try {
            // 刻意用 forName 的默认形式：ClientData 只在客户端存在，专用服务端会抛异常，被下面吞掉。
            clientRailsField = Class.forName("mtr.client.ClientData").getField("RAILS");
        } catch (Throwable t) {
            clientRailsField = null;
        }
        return clientRailsField;
    }

    // ------------------------------------------------------------------ 轨道表

    /** 客户端已知的全量轨道表：{@code mtr.client.ClientData.RAILS}。 */
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
            // 忽略：拿不到就当作没有轨道
        }
        return null;
    }

    /** 存档里的轨道表：{@code RailwayData.getInstance(level)#getRailsMap()}。 */
    @SuppressWarnings("unchecked")
    private static Map<BlockPos, Map<BlockPos, Object>> serverRails(Level level) {
        if (!initRailApi() || level == null) return null;
        try {
            final Object data = getInstance.invoke(null, level);
            if (data == null) {
                LOGGER.warn("[yunbeiuc] 拿不到 MTR 的 RailwayData（level={}）",
                        level.getClass().getSimpleName());
                return null;
            }
            final Object mapObject = getRailsMap.invoke(data);
            if (mapObject instanceof Map<?, ?> map) {
                return (Map<BlockPos, Map<BlockPos, Object>>) map;
            }
        } catch (Throwable t) {
            LOGGER.warn("[yunbeiuc] 读取 MTR 轨道表失败", t);
        }
        return null;
    }

    // ------------------------------------------------------------------ 对外 API

    /** 客户端瞄准：用客户端同步到的轨道表做射线检测（正常路径）。 */
    public static Hit raycastClient(Vec3 eye, Vec3 look, double maxDistance, double threshold) {
        return raycast(clientRails(), eye, look, maxDistance, threshold);
    }

    /**
     * 按两端节点直接取回一条 MTR 轨道（读客户端轨道表，不做射线检测）。
     *
     * <p>「手持魔杖就显示本组所有已链接轨道」用的就是它：相位组里存的是两端节点坐标
     * （{@link com.beigu.yunbeiuc.signal.RailPhaseBinding}），而 {@code ClientData.RAILS}
     * 正好是 {@code Map<起点, Map<终点, Rail>>}，所以两个坐标就能直接定位到几何对象，
     * 不需要玩家的准心参与。
     *
     * <p>MTR 的轨道表对同一段轨道**两个方向都可能存**（{@code RenderTrains} 自己要用
     * {@code PathData.getRailProduct} 去重就是这个原因），所以正反两个方向都试一次。
     *
     * @return MTR 的 {@code Rail} 实例；未装 MTR、节点不在轨道表里时返回 {@code null}
     */
    public static Object railBetween(BlockPos start, BlockPos end) {
        if (start == null || end == null) return null;
        final Map<BlockPos, Map<BlockPos, Object>> rails = clientRails();
        if (rails == null) return null;
        final Object forward = lookupRail(rails, start, end);
        return forward != null ? forward : lookupRail(rails, end, start);
    }

    private static Object lookupRail(Map<BlockPos, Map<BlockPos, Object>> rails, BlockPos a, BlockPos b) {
        final Map<BlockPos, Object> inner = rails.get(a);
        return inner == null ? null : inner.get(b);
    }

    /** 服务端瞄准：用存档里的轨道表做射线检测（备用路径）。 */
    public static Hit raycastServer(Level level, Vec3 eye, Vec3 look, double maxDistance, double threshold) {
        return raycast(serverRails(level), eye, look, maxDistance, threshold);
    }

    /**
     * 把一条轨道离散成中心线折线，供客户端画高亮线使用。
     *
     * @param rail {@link Hit#rail}，即 MTR 的 {@code Rail} 实例
     * @return 采样点列表（至少 2 个点才算有效）；未装 MTR 或参数不对时返回空表
     */
    public static List<Vec3> centerline(Object rail) {
        if (rail == null || !initRailApi()) return Collections.emptyList();
        try {
            final double length = (double) railGetLength.invoke(rail);
            if (!(length > 0.0)) return Collections.emptyList();
            final int steps = Math.max(2, (int) Math.ceil(length / CENTERLINE_STEP));
            final List<Vec3> points = new ArrayList<>(steps + 1);
            for (int i = 0; i <= steps; i++) {
                final Vec3 point = (Vec3) railGetPosition.invoke(rail, length * i / steps);
                if (point != null) points.add(point);
            }
            return points;
        } catch (Throwable t) {
            return Collections.emptyList();
        }
    }

    // ------------------------------------------------------------------ 核心

    /**
     * 返回视线命中的最近一条轨道。
     *
     * @param maxDistance 最远距离（方块）
     * @param threshold   允许的横向偏差（方块）。MTR 画出的轨道带宽是 ±1，取 1 左右与所见一致
     * @return 命中轨道；未装 MTR、轨道表为空或视线没打到轨道时返回 {@code null}
     */
    private static Hit raycast(Map<BlockPos, Map<BlockPos, Object>> rails,
                               Vec3 eye, Vec3 look, double maxDistance, double threshold) {
        if (rails == null || rails.isEmpty() || eye == null || look == null) return null;
        if (look.lengthSqr() < 1.0E-8) return null;
        if (!initRailApi()) return null;

        final Vec3 dir = look.normalize();
        Hit best = null;
        try {
            for (Map.Entry<BlockPos, Map<BlockPos, Object>> outer : rails.entrySet()) {
                final BlockPos start = outer.getKey();
                final Map<BlockPos, Object> inner = outer.getValue();
                if (start == null || inner == null) continue;

                for (Map.Entry<BlockPos, Object> entry : inner.entrySet()) {
                    final BlockPos end = entry.getKey();
                    final Object rail = entry.getValue();
                    if (end == null || rail == null) continue;

                    // 粗筛：轨道离视线太远就跳过，避免对整张轨道表逐点采样
                    if (distanceToSegment(eye, start, end) > maxDistance + threshold + 4.0) continue;

                    final double length = (double) railGetLength.invoke(rail);
                    if (!(length > 0.0)) continue;

                    final int steps = Math.max(2, (int) Math.ceil(length / SAMPLE_STEP));
                    double bestAlong = Double.MAX_VALUE;
                    for (int i = 0; i <= steps; i++) {
                        final Vec3 point = (Vec3) railGetPosition.invoke(rail, length * i / steps);
                        if (point == null) continue;
                        final Vec3 rel = point.subtract(eye);
                        final double along = rel.dot(dir);
                        if (along < 0.0 || along > maxDistance) continue;
                        if (rel.subtract(dir.scale(along)).length() > threshold) continue;
                        if (along < bestAlong) bestAlong = along;
                    }
                    if (bestAlong == Double.MAX_VALUE) continue;

                    if (best == null || bestAlong < best.distance) {
                        best = new Hit(start, end, rail, bestAlong);
                    }
                }
            }
        } catch (Throwable t) {
            LOGGER.warn("[yunbeiuc] 轨道射线检测异常", t);
            return null;
        }
        logAimChange(best);
        return best;
    }

    /** 只在「瞄准目标变化」时打一条日志，避免每帧刷屏。 */
    private static void logAimChange(Hit hit) {
        final Object rail = hit == null ? null : hit.rail;
        if (rail == null) {
            if (!lastLoggedNull) {
                lastLoggedNull = true;
                lastLoggedRail = null;
                LOGGER.info("[yunbeiuc] 准心已离开轨道");
            }
            return;
        }
        if (rail == lastLoggedRail) return;
        lastLoggedRail = rail;
        lastLoggedNull = false;
        LOGGER.info("[yunbeiuc] 准心瞄准轨道：{} → {}（视线距离 {}）",
                hit.start.toShortString(), hit.end.toShortString(),
                Math.round(hit.distance * 100) / 100.0);
    }

    /** 点到线段的最短距离（用两端点方块中心近似，只用于粗筛）。 */
    private static double distanceToSegment(Vec3 point, BlockPos a, BlockPos b) {
        final Vec3 p1 = center(a);
        final Vec3 p2 = center(b);
        final Vec3 ab = p2.subtract(p1);
        final double lengthSqr = ab.lengthSqr();
        if (lengthSqr < 1.0E-8) return point.distanceTo(p1);
        double t = point.subtract(p1).dot(ab) / lengthSqr;
        if (t < 0.0) t = 0.0;
        else if (t > 1.0) t = 1.0;
        return point.distanceTo(p1.add(ab.scale(t)));
    }

    /** 方块中心。刻意手写而不用 {@code Vec3.atCenterOf}，避免各版本 API 差异。 */
    private static Vec3 center(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
}
