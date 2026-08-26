package com.beigu.yunbeiuc.autoassign;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 相位组自动分配入口。要支持新的布局组合方案，只需新写一个实现
 * {@link TrafficLightsLayoutPattern} 的类并加入下面的 PATTERNS 列表。
 */
public class TrafficLightsAutoAssigner {
    private static final List<TrafficLightsLayoutPattern> PATTERNS = List.of(
            new FourDirectionStraightLeftPattern()
    );

    public static boolean tryAutoAssign(World world, List<TrafficLightsBlockEntity> members, List<BlockPos> positions, int phaseCount, @Nullable PlayerEntity notifyPlayer) {
        for (TrafficLightsLayoutPattern pattern : PATTERNS) {
            if (pattern.tryApply(world, members, positions, phaseCount, notifyPlayer)) {
                return true;
            }
        }
        return false;
    }
}
