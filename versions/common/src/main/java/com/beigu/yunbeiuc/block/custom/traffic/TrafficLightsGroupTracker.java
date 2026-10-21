package com.beigu.yunbeiuc.block.custom.traffic;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// 暂存「即将被移除」的红绿灯方块实体：1.20.1 的 setBlockState 在 onRemove 之前就把方块实体移除了，
// 故在 playerWillDestroy 里先记下来供 onRemove 取回，否则拆组逻辑整段失效。仅服务端，读取即移除。
// 连拆除者一起记，供解散整组时给玩家一句明确提示。

public final class TrafficLightsGroupTracker {

    public static final class Pending {
        public final TrafficLightsBlockEntity entity;
        public final Player player;

        Pending(TrafficLightsBlockEntity entity, Player player) {
            this.entity = entity;
            this.player = player;
        }
    }

    private static final Map<BlockPos, Pending> PENDING = new ConcurrentHashMap<>();

    private TrafficLightsGroupTracker() {}

    public static void track(Level world, BlockPos pos, Player player) {
        if (world == null || world.isClientSide) return;
        if (!(world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl)) return;
        if (tl.getGroupId() == null) return;
        PENDING.put(pos.immutable(), new Pending(tl, player));
    }

    public static Pending take(BlockPos pos) {
        return PENDING.remove(pos);
    }
}
