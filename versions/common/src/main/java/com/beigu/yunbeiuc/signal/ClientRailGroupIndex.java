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

// 客户端相位组锚点索引：魔杖上不存「我在哪一组」，改为从世界里现查。
// 读取时顺手剔除失效项（方块实体没了 / 退组 / 区块卸载）；换世界按 lastLevel 引用比较清空，不需要区块事件。

public final class ClientRailGroupIndex {

    private static final Set<BlockPos> ANCHORS = new LinkedHashSet<>();

    private static Level lastLevel;

    private ClientRailGroupIndex() {
    }

    public static void register(Level level, BlockPos anchor) {
        if (level == null || anchor == null) return;
        if (level != lastLevel) {
            ANCHORS.clear();
            lastLevel = level;
        }
        ANCHORS.add(anchor.immutable());
    }

    public static List<BlockPos> validAnchors(Level level) {
        if (level == null) return Collections.emptyList();
        if (level != lastLevel) {
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

    public static void clear() {
        ANCHORS.clear();
        lastLevel = null;
    }
}
