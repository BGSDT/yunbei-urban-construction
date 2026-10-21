package com.beigu.yunbeiuc.signal.mtr;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// 用玩家视线瞄准一条 MTR 轨道：取中心线做射线检测 / 采样折线，与 MTR 版本无关（差异全收在 MtrRailBackend）。

public final class RailRaycast {

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

    private static final double SAMPLE_STEP = 0.5;
    private static final double CENTERLINE_STEP = 0.25;

    private static final org.apache.logging.log4j.Logger LOGGER =
            com.beigu.yunbeiuc.YunbeiUrbanConstruction.LOGGER;

    private static MtrRailBackend backend;
    private static boolean backendTried;

    private static Object lastLoggedRail;
    private static boolean lastLoggedNull = true;

    private RailRaycast() {
    }

    private static MtrRailBackend backend() {
        if (!backendTried) {
            backendTried = true;
            backend = MtrRailBackend.create();
            if (backend == null) {
                LOGGER.info("[yunbeiuc] 未检测到 MTR（3 或 4），轨道瞄准 / 高亮功能不可用");
            }
        }
        return backend;
    }

    public static MtrVersion linkedVersion() {
        return MtrVersion.detect();
    }

    public static Hit raycastClient(Vec3 eye, Vec3 look, double maxDistance, double threshold) {
        final MtrRailBackend backend = backend();
        if (backend == null) return null;
        return raycast(backend, null, eye, look, maxDistance, threshold);
    }

    public static Object railBetween(BlockPos start, BlockPos end) {
        final MtrRailBackend backend = backend();
        return backend == null ? null : backend.findClientRail(start, end);
    }

    public static Hit raycastServer(Level level, Vec3 eye, Vec3 look, double maxDistance, double threshold) {
        final MtrRailBackend backend = backend();
        if (backend == null || level == null) return null;
        return raycast(backend, level, eye, look, maxDistance, threshold);
    }

    public static List<Vec3> centerline(Object rail) {
        final MtrRailBackend backend = backend();
        if (backend == null || rail == null) return Collections.emptyList();
        final double length = backend.length(rail);
        if (!(length > 0.0)) return Collections.emptyList();

        final int steps = Math.max(2, (int) Math.ceil(length / CENTERLINE_STEP));
        final List<Vec3> points = new ArrayList<>(steps + 1);
        for (int i = 0; i <= steps; i++) {
            final Vec3 point = backend.positionAt(rail, length * i / steps);
            if (point != null) points.add(point);
        }
        return points;
    }

    private static Hit raycast(MtrRailBackend backend, Level level,
                               Vec3 eye, Vec3 look, double maxDistance, double threshold) {
        if (eye == null || look == null) return null;
        if (look.lengthSqr() < 1.0E-8) return null;

        final Vec3 dir = look.normalize();
        final Hit[] best = new Hit[1];
        try {
            final MtrRailBackend.RailVisitor visitor = (start, end, rail) -> {
                if (distanceToSegment(eye, start, end) > maxDistance + threshold + 4.0) return;

                final double length = backend.length(rail);
                if (!(length > 0.0)) return;

                final int steps = Math.max(2, (int) Math.ceil(length / SAMPLE_STEP));
                double bestAlong = Double.MAX_VALUE;
                for (int i = 0; i <= steps; i++) {
                    final Vec3 point = backend.positionAt(rail, length * i / steps);
                    if (point == null) continue;
                    final Vec3 rel = point.subtract(eye);
                    final double along = rel.dot(dir);
                    if (along < 0.0 || along > maxDistance) continue;
                    if (rel.subtract(dir.scale(along)).length() > threshold) continue;
                    if (along < bestAlong) bestAlong = along;
                }
                if (bestAlong == Double.MAX_VALUE) return;

                if (best[0] == null || bestAlong < best[0].distance) {
                    best[0] = new Hit(start, end, rail, bestAlong);
                }
            };

            final boolean iterated = level == null
                    ? forEachClient(backend, visitor)
                    : backend.forEachServerRail(level, visitor);
            if (!iterated) return null;
        } catch (Throwable t) {
            LOGGER.warn("[yunbeiuc] 轨道射线检测异常", t);
            return null;
        }
        logAimChange(best[0]);
        return best[0];
    }

    private static boolean forEachClient(MtrRailBackend backend, MtrRailBackend.RailVisitor visitor) {
        final boolean[] any = new boolean[1];
        backend.forEachClientRail((start, end, rail) -> {
            any[0] = true;
            visitor.accept(start, end, rail);
        });
        return any[0];
    }

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

    private static Vec3 center(BlockPos pos) {
        return new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
    }
}
