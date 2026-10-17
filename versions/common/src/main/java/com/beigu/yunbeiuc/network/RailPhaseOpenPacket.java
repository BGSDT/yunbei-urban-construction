package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.screen.RailPhaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

/**
 * S2C：服务端确认「玩家正瞄准某条已绑定的轨道」后，把打开轨道相位界面所需的数据推给客户端。
 *
 * <p>数据由服务端给出（而不是客户端自己算），这样「瞄准的到底是哪条轨道、属于哪个组」由权威端裁定，
 * 客户端不用重复做一遍射线检测。
 *
 * <p><b>注意</b>：1.20.1 的客户端方块实体**是**拿得到 {@code railBindings} 的 ——
 * {@code BlockEntityMapper.createUpdatePacket()} 走 {@code ClientboundBlockEntityDataPacket.create(this)}
 * → {@code getUpdateTag()} = {@code saveWithoutMetadata()} → {@code saveAdditionalCompat}，
 * 里面就写了 {@code railBindings} / {@code phaseTimes} / {@code phaseCount} 等。
 * （本类早先的注释说「客户端拿不到」是**错的**，已更正。）
 * 真正需要这个包的原因是**服务端权威裁定**：瞄准的到底是哪条轨道、它属于哪个组，
 * 由服务端查 {@code RailSignalRegistry} 决定，客户端不重复做一遍射线检测。
 */
public class RailPhaseOpenPacket {

    private final BlockPos start;
    private final BlockPos end;
    private final BlockPos groupMember;
    private final int phaseCount;
    private final int[] phaseTimes;
    private final long mask;

    public RailPhaseOpenPacket(BlockPos start, BlockPos end, BlockPos groupMember,
                               int phaseCount, int[] phaseTimes, long mask) {
        this.start = start;
        this.end = end;
        this.groupMember = groupMember;
        this.phaseCount = phaseCount;
        this.phaseTimes = phaseTimes;
        this.mask = mask;
    }

    public RailPhaseOpenPacket(FriendlyByteBuf buf) {
        this.start = buf.readBlockPos();
        this.end = buf.readBlockPos();
        this.groupMember = buf.readBlockPos();
        this.phaseCount = buf.readVarInt();
        this.phaseTimes = buf.readVarIntArray();
        this.mask = buf.readLong();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(start);
        buf.writeBlockPos(end);
        buf.writeBlockPos(groupMember);
        buf.writeVarInt(phaseCount);
        buf.writeVarIntArray(phaseTimes == null ? new int[0] : phaseTimes);
        buf.writeLong(mask);
    }

    /** 客户端应用：打开轨道相位编辑界面。 */
    public void applyClient() {
        com.beigu.yunbeiuc.YunbeiUrbanConstruction.LOGGER.info(
                "[yunbeiuc] 客户端收到轨道相位界面数据：{} → {}，相位 {} 个，掩码 {}",
                start.toShortString(), end.toShortString(), phaseCount, Long.toBinaryString(mask));

        Minecraft.getInstance().setScreen(
                new RailPhaseScreen(start, end, groupMember, phaseCount, phaseTimes, mask));
    }
}
