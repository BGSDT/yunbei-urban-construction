package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.screen.TrafficLightsTimingScreen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class OpenTrafficLightsTimingScreenS2CPacket {
    private final String groupId;
    private final List<BlockPos> positions;

    public OpenTrafficLightsTimingScreenS2CPacket(String groupId, List<BlockPos> positions) {
        this.groupId = groupId;
        this.positions = positions;
    }

    public OpenTrafficLightsTimingScreenS2CPacket(PacketByteBuf buf) {
        this.groupId = buf.readString();
        int size = buf.readVarInt();
        this.positions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.positions.add(buf.readBlockPos());
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeString(groupId);
        buf.writeVarInt(positions.size());
        for (BlockPos pos : positions) {
            buf.writeBlockPos(pos);
        }
    }

    public void apply() {
        MinecraftClient.getInstance().setScreen(new TrafficLightsTimingScreen(groupId, positions));
    }
}
