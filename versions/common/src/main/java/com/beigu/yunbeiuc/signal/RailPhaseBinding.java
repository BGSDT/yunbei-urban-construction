package com.beigu.yunbeiuc.signal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

// 一条 MTR 轨道与相位组的绑定：只存两端节点坐标，轨道标识按同一套规则现算，故不依赖 MTR 的类、也不分 MTR 3/4。
// 相位用位掩码表示（第 i 位 = 相位 i 放行）；掩码为 0 表示这条轨道恒为红灯。

public final class RailPhaseBinding {

    public static final int MAX_PHASES = Long.SIZE;

    private final BlockPos start;
    private final BlockPos end;
    private long phaseMask;

    public RailPhaseBinding(BlockPos start, BlockPos end, long phaseMask) {
        this.start = start.immutable();
        this.end = end.immutable();
        this.phaseMask = phaseMask;
    }

    public static RailPhaseBinding of(BlockPos start, BlockPos end, int phaseIndex) {
        return new RailPhaseBinding(start, end, bit(phaseIndex));
    }

    public static long bit(int index) {
        if (index < 0 || index >= MAX_PHASES) return 0L;
        return 1L << index;
    }

    public BlockPos getStart() {
        return start;
    }

    public BlockPos getEnd() {
        return end;
    }

    public long getPhaseMask() {
        return phaseMask;
    }

    public void setPhaseMask(long phaseMask) {
        this.phaseMask = phaseMask;
    }

    public boolean isPhaseEnabled(int index) {
        return index >= 0 && index < MAX_PHASES && (phaseMask & bit(index)) != 0L;
    }

    public boolean setPhaseEnabled(int index, boolean enabled) {
        if (index < 0 || index >= MAX_PHASES) return false;
        final long before = phaseMask;
        if (enabled) {
            phaseMask |= bit(index);
        } else {
            phaseMask &= ~bit(index);
        }
        return before != phaseMask;
    }

    public void togglePhase(int index) {
        setPhaseEnabled(index, !isPhaseEnabled(index));
    }

    public int enabledPhaseCount() {
        return Long.bitCount(phaseMask);
    }

    public int firstEnabledPhase() {
        return phaseMask == 0L ? -1 : Long.numberOfTrailingZeros(phaseMask);
    }

    public void clampToPhaseCount(int phaseCount) {
        if (phaseCount <= 0) {
            phaseMask = 0L;
            return;
        }
        if (phaseCount >= MAX_PHASES) return;
        phaseMask &= (1L << phaseCount) - 1L;
    }

    public UUID railProduct() {
        return railProductOf(start, end);
    }

    public static UUID railProductOf(BlockPos start, BlockPos end) {
        final long a = start.asLong();
        final long b = end.asLong();
        return new UUID(Math.min(a, b), Math.max(a, b));
    }

    public boolean matches(BlockPos p1, BlockPos p2) {
        return (start.equals(p1) && end.equals(p2)) || (start.equals(p2) && end.equals(p1));
    }

    public CompoundTag toNbt() {
        final CompoundTag tag = new CompoundTag();
        tag.putLong("start", start.asLong());
        tag.putLong("end", end.asLong());
        tag.putLong("mask", phaseMask);
        tag.putInt("phase", Math.max(firstEnabledPhase(), 0));
        return tag;
    }

    public static RailPhaseBinding fromNbt(CompoundTag tag) {
        final long mask;
        if (tag.contains("mask")) {
            mask = tag.getLong("mask");
        } else {
            mask = bit(tag.getInt("phase"));
        }
        return new RailPhaseBinding(
                BlockPos.of(tag.getLong("start")),
                BlockPos.of(tag.getLong("end")),
                mask);
    }
}
