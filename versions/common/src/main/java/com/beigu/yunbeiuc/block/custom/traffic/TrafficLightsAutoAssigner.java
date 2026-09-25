package com.beigu.yunbeiuc.block.custom.traffic;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 相位组自动分配入口（已禁用）。
 * 自动分配功能已被移除，现在使用预设分配功能。
 */
public class TrafficLightsAutoAssigner {
    private static final List<TrafficLightsLayoutPattern> PATTERNS = List.of();

    public static boolean tryAutoAssign(Level world, List<TrafficLightsBlockEntity> members, List<BlockPos> positions, int phaseCount, @Nullable Player notifyPlayer) {
        // 自动分配已禁用，始终返回 false
        return false;
    }
}
