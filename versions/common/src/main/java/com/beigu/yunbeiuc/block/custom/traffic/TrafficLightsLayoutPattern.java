package com.beigu.yunbeiuc.block.custom.traffic;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 一种可识别的红绿灯组布局方案：判断成员是否符合特定几何/数量特征，
 * 若符合则自动为每个成员分配 directionType 与 phaseIndices。
 */
public interface TrafficLightsLayoutPattern {
    boolean tryApply(Level world, List<TrafficLightsBlockEntity> members, List<BlockPos> positions, int phaseCount, @Nullable Player notifyPlayer);
}
