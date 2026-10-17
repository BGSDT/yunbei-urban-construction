package com.beigu.yunbeiuc.signal;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

import java.util.UUID;

/**
 * 一条 MTR 轨道与红绿灯相位组之间的绑定。
 *
 * <p>MTR 的轨道由两端「节点方块」的坐标唯一确定，其轨道标识
 * {@code PathData.getRailProduct(posStart, posEnd)} 就是
 * {@code new UUID(min(asLong), max(asLong))}。这里只保存两端坐标，
 * 需要时再算出同一个 UUID，因此**不需要依赖 MTR 的类**。
 *
 * <p>相位用位掩码表示：第 {@code i} 位为 1 表示「组的活动相位为 i 时本轨道放行」。
 * 允许一条轨道同时被多个相位放行（例如直行与左转共用同一段轨道），
 * 掩码为 0 时该轨道永不放行（恒为红灯）。相位的勾选在专用界面里完成，
 * 因此绑定本身不再携带「单一相位」的概念。
 */
public final class RailPhaseBinding {

    /** 位掩码能表示的相位数量上限。 */
    public static final int MAX_PHASES = Long.SIZE;

    private final BlockPos start;
    private final BlockPos end;
    private long phaseMask;

    public RailPhaseBinding(BlockPos start, BlockPos end, long phaseMask) {
        this.start = start.immutable();
        this.end = end.immutable();
        this.phaseMask = phaseMask;
    }

    /** 单相位便捷构造（等价于只勾选 {@code phaseIndex} 一个相位）。 */
    public static RailPhaseBinding of(BlockPos start, BlockPos end, int phaseIndex) {
        return new RailPhaseBinding(start, end, bit(phaseIndex));
    }

    /** 第 {@code index} 个相位对应的掩码位；越界返回 0。 */
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

    /** 相位 {@code index} 是否放行本轨道。 */
    public boolean isPhaseEnabled(int index) {
        return index >= 0 && index < MAX_PHASES && (phaseMask & bit(index)) != 0L;
    }

    /**
     * 勾选 / 取消勾选某个相位。
     *
     * @return 掩码是否真的发生了变化
     */
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

    /** 被勾选的相位个数。 */
    public int enabledPhaseCount() {
        return Long.bitCount(phaseMask);
    }

    /** 编号最小的被勾选相位（0 起）；一个都没勾选时返回 -1。仅用于展示。 */
    public int firstEnabledPhase() {
        return phaseMask == 0L ? -1 : Long.numberOfTrailingZeros(phaseMask);
    }

    /** 清掉掩码里超出 {@code phaseCount} 的相位（相位表被缩短后调用）。 */
    public void clampToPhaseCount(int phaseCount) {
        if (phaseCount <= 0) {
            phaseMask = 0L;
            return;
        }
        if (phaseCount >= MAX_PHASES) return;
        phaseMask &= (1L << phaseCount) - 1L;
    }

    /**
     * 轨道标识 UUID，与 MTR 的 {@code PathData.getRailProduct} 完全一致
     * （两端坐标 {@code asLong()} 取小者为 most，大者为 least）。
     */
    public UUID railProduct() {
        return railProductOf(start, end);
    }

    /** 静态版：按两端坐标直接算轨道标识 UUID，省去为查表而临时 new 一个实例。 */
    public static UUID railProductOf(BlockPos start, BlockPos end) {
        final long a = start.asLong();
        final long b = end.asLong();
        return new UUID(Math.min(a, b), Math.max(a, b));
    }

    /** 忽略方向地比较两端坐标是否与本绑定相同。 */
    public boolean matches(BlockPos p1, BlockPos p2) {
        return (start.equals(p1) && end.equals(p2)) || (start.equals(p2) && end.equals(p1));
    }

    public CompoundTag toNbt() {
        final CompoundTag tag = new CompoundTag();
        tag.putLong("start", start.asLong());
        tag.putLong("end", end.asLong());
        tag.putLong("mask", phaseMask);
        // 兼容降级：旧版本只认单个 "phase" 字段，写入最低位相位以免其读到 0 号相位
        tag.putInt("phase", Math.max(firstEnabledPhase(), 0));
        return tag;
    }

    public static RailPhaseBinding fromNbt(CompoundTag tag) {
        final long mask;
        if (tag.contains("mask")) {
            mask = tag.getLong("mask");
        } else {
            // 旧存档只有单相位字段
            mask = bit(tag.getInt("phase"));
        }
        return new RailPhaseBinding(
                BlockPos.of(tag.getLong("start")),
                BlockPos.of(tag.getLong("end")),
                mask);
    }
}
