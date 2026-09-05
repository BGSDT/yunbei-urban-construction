package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPatternPreset;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

/**
 * C2S：把一个相位分配预设应用到点击的红绿灯所在的链接组。
 * 服务端做"完美嵌入"校验，全部匹配才对组内所有红绿灯写入图案与相位。
 */
public class TrafficLightsPatternApplyPacket {
    private final BlockPos pos;
    private final TrafficLightsPatternPreset preset;

    public TrafficLightsPatternApplyPacket(BlockPos pos, TrafficLightsPatternPreset preset) {
        this.pos = pos;
        this.preset = preset;
    }

    public TrafficLightsPatternApplyPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.preset = readPreset(buf);
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        writePreset(buf, preset);
    }

    public static void writePreset(PacketByteBuf buf, TrafficLightsPatternPreset preset) {
        buf.writeString(preset.getName());
        buf.writeVarInt(preset.getPhaseCount());
        List<TrafficLightsPatternPreset.Slot> slots = preset.getSlots();
        buf.writeVarInt(slots.size());
        for (TrafficLightsPatternPreset.Slot slot : slots) {
            buf.writeVarInt(slot.getDirection().ordinal());
            buf.writeVarInt(slot.getKind().ordinal());
            buf.writeString(slot.getDirectionType().getName());
            buf.writeVarInt(slot.getPhaseIndices().size());
            for (int index : slot.getPhaseIndices()) {
                buf.writeVarInt(index);
            }
            buf.writeVarInt(slot.getOrder());
        }
    }

    public static TrafficLightsPatternPreset readPreset(PacketByteBuf buf) {
        TrafficLightsPatternPreset preset = new TrafficLightsPatternPreset();
        preset.setName(buf.readString());
        preset.setPhaseCount(buf.readVarInt());
        int slotCount = buf.readVarInt();
        List<TrafficLightsPatternPreset.Slot> slots = new ArrayList<>();
        TrafficLightsPatternPreset.Direction8[] directions = TrafficLightsPatternPreset.Direction8.values();
        TrafficLightsPatternPreset.MemberKind[] kinds = TrafficLightsPatternPreset.MemberKind.values();
        for (int i = 0; i < slotCount; i++) {
            TrafficLightsPatternPreset.Slot slot = new TrafficLightsPatternPreset.Slot();
            int dirOrdinal = buf.readVarInt();
            slot.setDirection(directions[dirOrdinal >= 0 && dirOrdinal < directions.length ? dirOrdinal : 0]);
            int kindOrdinal = buf.readVarInt();
            slot.setKind(kinds[kindOrdinal >= 0 && kindOrdinal < kinds.length ? kindOrdinal : 0]);
            slot.setDirectionType(TrafficLightsBlockEntity.DirectionType.fromName(buf.readString()));
            int phaseCount = buf.readVarInt();
            List<Integer> phaseIndices = new ArrayList<>();
            for (int j = 0; j < phaseCount; j++) {
                phaseIndices.add(buf.readVarInt());
            }
            if (phaseIndices.isEmpty()) phaseIndices.add(0);
            slot.setPhaseIndices(phaseIndices);
            slot.setOrder(buf.readVarInt());
            slots.add(slot);
        }
        preset.setSlots(slots);
        return preset;
    }

    public void apply(ServerPlayerEntity player) {
        World world = player.getWorld();
        if (!(world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity clicked)) {
            player.sendMessage(Text.literal("§c该方块不是红绿灯！"), false);
            return;
        }
        if (!clicked.isInGroup()) {
            player.sendMessage(Text.literal("§c该红绿灯不在任何链接组中！"), false);
            return;
        }
        if (!clicked.hasTimings()) {
            player.sendMessage(Text.literal("§c该红绿灯所在的链接组还没有设置时间表，请先设置时间表！"), false);
            return;
        }

        String groupId = clicked.getGroupId();
        List<BlockPos> positions = clicked.getGroupPositions();
        List<TrafficLightsBlockEntity> members = new ArrayList<>();
        for (BlockPos memberPos : positions) {
            if (world.getBlockEntity(memberPos) instanceof TrafficLightsBlockEntity tl
                    && groupId.equals(tl.getGroupId())) {
                members.add(tl);
            } else {
                player.sendMessage(Text.literal("§c一些已链接的红绿灯已被破坏，链接组已失效！"), false);
                return;
            }
        }

        if (members.isEmpty()) {
            player.sendMessage(Text.literal("§c链接组为空！"), false);
            return;
        }

        preset.tryApplyToGroup(world, members, positions, player);
    }
}
