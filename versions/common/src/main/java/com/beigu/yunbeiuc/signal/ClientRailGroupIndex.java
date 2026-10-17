package com.beigu.yunbeiuc.signal;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 客户端「相位组锚点」索引。
 *
 * <h3>为什么需要它</h3>
 * 手持普通魔杖时要显示**周围所有已链接轨道**的高亮线与「相位 x」标签，但物品上
 * **不留任何「我在哪一组」的信息**（见 {@code PhaseWand}：魔杖不再带这种属性）。
 * 于是改成从世界里找：每个相位组在客户端同步到数据时把锚点登记进本表，
 * 渲染器按锚点去 {@code level.getBlockEntity(anchor)} 取那一组的轨道绑定。
 *
 * <h3>自愈，不需要区块加载/卸载事件</h3>
 * {@link #validAnchors} 在读取时顺手删掉失效项：方块实体没了、被拆了、退组了、
 * 或者所在区块卸载了（{@code level.getBlockEntity} 对未加载区块返回 {@code null}）
 * 都会清掉。区块重新加载时方块实体会再次同步数据、重新登记，所以索引会自动补回来。
 *
 * <p>纯客户端静态缓存，服务端不会往里写；未安装 MTR 时本类照常工作（只跟红绿灯有关）。
 */
public final class ClientRailGroupIndex {

    /** 已知的相位组锚点（组内第一个成员的坐标）。 */
    private static final Set<BlockPos> ANCHORS = new LinkedHashSet<>();

    /**
     * 上一次见到的是哪个世界。
     *
     * <p>换世界（退出存档 / 进服务器）时锚点必须全部作废，否则新世界里同坐标的相位组
     * 会被误当成旧的。只按引用比较，不长期持有——新世界一进来就把它顶掉了。
     */
    private static Level lastLevel;

    private ClientRailGroupIndex() {
    }

    /** 登记一个相位组锚点（客户端方块实体同步到数据时调用）。 */
    public static void register(Level level, BlockPos anchor) {
        if (level == null || anchor == null) return;
        if (level != lastLevel) {
            // 换了世界：先把上一个世界的锚点全部丢掉，再登记新的
            ANCHORS.clear();
            lastLevel = level;
        }
        ANCHORS.add(anchor.immutable());
    }

    /**
     * 当前仍然有效的组锚点；顺手清理失效项。
     *
     * <p>返回的是新列表，调用方不要长期持有（每帧都会重建）。
     */
    public static List<BlockPos> validAnchors(Level level) {
        if (level == null) return Collections.emptyList();
        if (level != lastLevel) {
            // 没有方块实体登记过（本世界还没有相位组）→ 顺手把上一个世界的清掉
            ANCHORS.clear();
            lastLevel = level;
        }
        if (ANCHORS.isEmpty()) return Collections.emptyList();

        final List<BlockPos> valid = new ArrayList<>(ANCHORS.size());
        for (final Iterator<BlockPos> it = ANCHORS.iterator(); it.hasNext(); ) {
            final BlockPos anchor = it.next();
            if (level.getBlockEntity(anchor) instanceof TrafficLightsBlockEntity group
                    && group.isInGroup()
                    && !group.getRailBindings().isEmpty()) {
                valid.add(anchor);
            } else {
                it.remove();
            }
        }
        return valid;
    }

    /** 退出世界 / 断线时清空，避免跨世界残留同坐标的旧锚点。 */
    public static void clear() {
        ANCHORS.clear();
        lastLevel = null;
    }
}
