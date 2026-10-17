package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * C2S：轨道相位界面点「保存」时提交新的相位勾选掩码。
 *
 * <p>服务端不信任客户端的掩码：{@code TrafficLightsBlockEntity#setRailBindingMask}
 * 会先把掩码裁剪到本组相位数量范围内，再写回绑定。
 */
public class RailPhaseUpdatePacket {

    private final BlockPos start;
    private final BlockPos end;
    private final BlockPos groupMember;
    private final long mask;

    public RailPhaseUpdatePacket(BlockPos start, BlockPos end, BlockPos groupMember, long mask) {
        this.start = start;
        this.end = end;
        this.groupMember = groupMember;
        this.mask = mask;
    }

    public RailPhaseUpdatePacket(FriendlyByteBuf buf) {
        this.start = buf.readBlockPos();
        this.end = buf.readBlockPos();
        this.groupMember = buf.readBlockPos();
        this.mask = buf.readLong();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(start);
        buf.writeBlockPos(end);
        buf.writeBlockPos(groupMember);
        buf.writeLong(mask);
    }

    public void apply(ServerPlayer player) {
        if (player == null) return;

        final Level level = player.getCommandSenderWorld();
        if (!(level.getBlockEntity(groupMember) instanceof TrafficLightsBlockEntity lights)) {
            message(player, "§c相位组已失效，请重新绑定后再编辑！");
            return;
        }
        if (!lights.isInGroup()) {
            message(player, "§c该红绿灯已不在任何相位组中！");
            return;
        }
        lights.setRailBindingMask(start, end, mask, player);
    }

    private static void message(ServerPlayer player, String text) {
        player.displayClientMessage(Text.literal(text), false);
    }
}
