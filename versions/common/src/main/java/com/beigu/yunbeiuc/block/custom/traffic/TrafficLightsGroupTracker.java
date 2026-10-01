package com.beigu.yunbeiuc.block.custom.traffic;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 暂存"即将被移除"的红绿灯方块实体。
 * <p>
 * Minecraft 1.20.1 的 {@code LevelChunk#setBlockState} 在调用 {@code Block#onRemove} 之前
 * 就已经把方块实体从区块中移除并标记为 removed，因此 {@code Level#getBlockEntity(pos)}
 * 在 onRemove 内部对本方块恒为 null。破坏方块时无法再取到方块实体，
 * 链接组的拆除逻辑（{@code TrafficLightsBlockEntity#unloadGroup()}）就会整段失效，
 * 导致组内其它存活成员仍然保留 groupId / phaseTimes，客户端也继续显示旧的图案界面。
 * <p>
 * 这里在方块被破坏前（{@code playerWillDestroy}）记录方块实体，供 onRemove 取回。
 * 仅服务端使用；读取即移除，避免长期持有已失效的方块实体引用。
 */
public final class TrafficLightsGroupTracker {

    private static final Map<BlockPos, TrafficLightsBlockEntity> PENDING = new ConcurrentHashMap<>();

    private TrafficLightsGroupTracker() {}

    /** 方块被破坏前记录其方块实体。仅服务端调用。 */
    public static void track(Level world, BlockPos pos) {
        if (world == null || world.isClientSide) return;
        if (!(world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl)) return;
        if (tl.getGroupId() == null) return;
        PENDING.put(pos.immutable(), tl);
    }

    /** 取回并移除记录；无记录时返回 null。 */
    public static TrafficLightsBlockEntity take(BlockPos pos) {
        return PENDING.remove(pos);
    }
}
