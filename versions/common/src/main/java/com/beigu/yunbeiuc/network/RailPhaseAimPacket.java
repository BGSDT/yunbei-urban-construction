package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.item.custom.PhaseWand;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

/**
 * C2S：客户端瞄准到一条轨道后，把「玩家想编辑这条轨道」的意图告诉服务端。
 *
 * <p>为什么由客户端算瞄准：MTR 的轨道表在客户端是现成的
 * （{@code mtr.client.ClientData.RAILS}），客户端算出来的轨道和玩家眼里看到的高亮线
 * 必然是同一份几何；服务端再自己去算一遍反而容易出现「看得见却点不开」的偏差。
 * 服务端只负责校验这条轨道确实绑定在本组上，然后回 {@link RailPhaseOpenPacket} 打开界面。
 *
 * <p>两个坐标都是 MTR 的**节点方块**位置（轨道表的两端 key），不是任意方块坐标。
 */
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

    /** 服务端应用：校验绑定并让该玩家打开轨道相位界面。 */
    public void apply(ServerPlayer player) {
        PhaseWand.openRailPhaseScreen(player, start, end);
    }
}
