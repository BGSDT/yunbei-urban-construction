package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.screen.RailPhaseScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;

// S2C：服务端确认瞄准的轨道后，把打开轨道相位界面所需的数据推给客户端（服务端权威裁定）。
// 客户端方块实体本来就能从 getUpdateTag 拿到 railBindings，这个包不是为了补数据。

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

    public void applyClient() {
        com.beigu.yunbeiuc.YunbeiUrbanConstruction.LOGGER.info(
                "[yunbeiuc] 客户端收到轨道相位界面数据：{} → {}，相位 {} 个，掩码 {}",
                start.toShortString(), end.toShortString(), phaseCount, Long.toBinaryString(mask));

        Minecraft.getInstance().setScreen(
                new RailPhaseScreen(start, end, groupMember, phaseCount, phaseTimes, mask));
    }
}
