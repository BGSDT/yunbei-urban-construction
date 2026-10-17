package com.beigu.yunbeiuc.signal;

import net.minecraft.core.BlockPos;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 列车信号的运行时索引（服务端专用）。
 *
 * <p>键是轨道标识 UUID（见 {@link RailPhaseBinding#railProduct()}），值是这条轨道
 * 当前的信号状态。红绿灯方块实体在每个 tick 把「本组所有已绑定轨道」的最新状态
 * 写进来，MTR 的列车钩子（{@code RailSignalHook}）在列车判定前方轨道是否被占用时
 * 直接查这张表，因此**不需要在钩子里拿到 Level / 游戏刻**。
 *
 * <p>这是纯运行期缓存：真正的持久化在红绿灯方块实体的 NBT 里（yunbeiuc 自己的存档），
 * 方块实体随区块加载/变化时重新写入本表；组被拆除或世界卸载时清空对应条目。
 *
 * <p>本表是**进程级**静态缓存，不随世界切换自动清空；但每条轨道每 tick 都会被其所属
 * 相位组覆盖，且解绑/拆组时会显式移除，因此跨世界残留只可能在「两端节点坐标完全相同的
 * 轨道 + 另一个世界里恰好有列车经过」这种几乎不可能的情况下产生。
 */
public final class RailSignalRegistry {

    /** 一条轨道的信号状态。{@code green == false} 即红灯，列车应在其前方停车。 */
    public static final class Entry {
        /** 所属相位组的锚点（组内第一个成员坐标），用于按组批量清除。 */
        public final BlockPos groupAnchor;
        /** 该轨道允许通行的相位掩码（第 i 位 = 相位 i 放行）。 */
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

    /** 写入/覆盖一条轨道的信号状态。 */
    public static void publish(UUID rail, BlockPos groupAnchor, long phaseMask, boolean green) {
        final Entry existing = ENTRIES.get(rail);
        if (existing != null && existing.groupAnchor.equals(groupAnchor)
                && existing.phaseMask == phaseMask) {
            existing.green = green;
            return;
        }
        ENTRIES.put(rail, new Entry(groupAnchor, phaseMask, green));
    }

    /** 移除一条轨道（解绑 / 轨道被拆）。 */
    public static void remove(UUID rail) {
        ENTRIES.remove(rail);
    }

    /** 移除某个相位组锚点下的全部轨道（组被拆 / 组重新链接）。 */
    public static void removeGroup(BlockPos groupAnchor) {
        if (groupAnchor == null) return;
        ENTRIES.values().removeIf(entry -> entry.groupAnchor.equals(groupAnchor));
    }

    public static Entry get(UUID rail) {
        return ENTRIES.get(rail);
    }

    /**
     * 该轨道当前是否为红灯（应拦截列车）。
     *
     * <p>没有登记的轨道一律视为「无信号」，返回 {@code false}，绝不拦车。
     */
    public static boolean isBlocked(UUID rail) {
        final Entry entry = ENTRIES.get(rail);
        return entry != null && !entry.green;
    }

    /** 世界卸载 / 服务端停止时清空。 */
    public static void clear() {
        ENTRIES.clear();
    }

    public static int size() {
        return ENTRIES.size();
    }
}
