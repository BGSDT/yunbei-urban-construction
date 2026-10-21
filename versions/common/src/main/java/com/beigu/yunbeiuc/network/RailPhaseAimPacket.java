package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.item.custom.PhaseWand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

// C2S：客户端瞄准到一条轨道后告诉服务端，由服务端校验它确实绑定在本组上。
// 两个坐标是 MTR 轨道表的节点方块坐标，不是任意方块坐标。

public class RailPhaseAimPacket {

    private final BlockPos start;
    private final BlockPos end;

    public RailPhaseAimPacket(BlockPos start, BlockPos end) {
        this.start = start;
        this.end = end;
    }

    public RailPhaseAimPacket(FriendlyByteBuf buf) {
        this.start = buf.readBlockPos();
        this.end = buf.readBlockPos();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(start);
        buf.writeBlockPos(end);
    }

    public void apply(ServerPlayer player) {
        PhaseWand.openRailPhaseScreen(player, start, end);
    }
}
