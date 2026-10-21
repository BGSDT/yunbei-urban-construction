package com.beigu.yunbeiuc.signal;

import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// 列车信号的运行时索引（服务端专用）：轨道标识 UUID -> 当前是否放行，由红绿灯方块实体每 tick 写入。
// 未登记的轨道一律视为无信号、绝不拦车。纯运行期缓存，持久化在方块实体的 NBT 里。

public final class RailSignalRegistry {

    public static final class Entry {
        public final BlockPos groupAnchor;
        public final long phaseMask;
        private volatile boolean green;

        private Entry(BlockPos groupAnchor, long phaseMask, boolean green) {
            this.groupAnchor = groupAnchor;
            this.phaseMask = phaseMask;
            this.green = green;
        }

        public boolean isGreen() {
            return green;
        }
    }

    private static final Map<UUID, Entry> ENTRIES = new ConcurrentHashMap<>();

    private RailSignalRegistry() {
    }

    public static void publish(UUID rail, BlockPos groupAnchor, long phaseMask, boolean green) {
        final Entry existing = ENTRIES.get(rail);
        if (existing != null && existing.groupAnchor.equals(groupAnchor)
                && existing.phaseMask == phaseMask) {
            existing.green = green;
            return;
        }
        ENTRIES.put(rail, new Entry(groupAnchor, phaseMask, green));
    }

    public static void remove(UUID rail) {
        ENTRIES.remove(rail);
    }

    public static void removeGroup(BlockPos groupAnchor) {
        if (groupAnchor == null) return;
        ENTRIES.values().removeIf(entry -> entry.groupAnchor.equals(groupAnchor));
    }

    public static Entry get(UUID rail) {
        return ENTRIES.get(rail);
    }

    public static boolean isBlocked(UUID rail) {
        final Entry entry = ENTRIES.get(rail);
        return entry != null && !entry.green;
    }

    public static void clear() {
        ENTRIES.clear();
    }

    public static int size() {
        return ENTRIES.size();
    }
}
