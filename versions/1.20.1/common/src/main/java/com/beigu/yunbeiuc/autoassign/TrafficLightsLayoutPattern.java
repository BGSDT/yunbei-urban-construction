package com.beigu.yunbeiuc.autoassign;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 一种可识别的红绿灯组布局方案：判断成员是否符合特定几何/数量特征，
 * 若符合则自动为每个成员分配 directionType 与 phaseIndices。
 */
public interface TrafficLightsLayoutPattern {
    boolean tryApply(World world, List<TrafficLightsBlockEntity> members, List<BlockPos> positions, int phaseCount, @Nullable PlayerEntity notifyPlayer);
}
