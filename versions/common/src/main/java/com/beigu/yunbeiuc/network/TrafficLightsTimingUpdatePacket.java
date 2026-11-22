package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.api.ApiCompat;

import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsAutoAssigner;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class TrafficLightsTimingUpdatePacket {
    private final String groupId;
    private final List<BlockPos> positions;
    private final int[] timings;
    private final int[] delays;

    public TrafficLightsTimingUpdatePacket(String groupId, List<BlockPos> positions, int[] timings, int[] delays) {
        this.groupId = groupId;
        this.positions = positions;
        this.timings = timings;
        this.delays = delays;
    }

    public TrafficLightsTimingUpdatePacket(FriendlyByteBuf buf) {
        this.groupId = buf.readUtf();
        int size = buf.readVarInt();
        this.positions = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.positions.add(buf.readBlockPos());
        }
        this.timings = buf.readVarIntArray();
        this.delays = buf.readVarIntArray();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(groupId);
        buf.writeVarInt(positions.size());
        for (BlockPos pos : positions) {
            buf.writeBlockPos(pos);
        }
        buf.writeVarIntArray(timings);
        buf.writeVarIntArray(delays);
    }

    public void apply(ServerPlayer player) {
        int phaseCount = timings.length;

        if (phaseCount < 2 || phaseCount > 16) {
            ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c相位数量必须在 2-16 之间！"), false);
            return;
        }

        for (int i = 0; i < phaseCount; i++) {
            if (timings[i] < 7) {
                ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c时间至少需要7秒！(第" + (i + 1) + "个时间)"), false);
                return;
            }
            if (timings[i] > 300) {
                ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c时间不能超过300秒！(第" + (i + 1) + "个时间)"), false);
                return;
            }
        }

        int[] normalizedDelays = new int[phaseCount];
        if (delays != null) {
            for (int i = 0; i < phaseCount && i < delays.length; i++) {
                normalizedDelays[i] = delays[i];
            }
        }
        for (int i = 0; i < phaseCount; i++) {
            if (normalizedDelays[i] < 0) {
                ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c相位延迟不能为负数！(第" + (i + 1) + "个相位)"), false);
                return;
            }
            if (normalizedDelays[i] > timings[i] - 6) {
                ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c相位延迟最多 " + (timings[i] - 6) + " 秒！(第" + (i + 1) + "个相位)"), false);
                return;
            }
        }

        Level world = ApiCompat.level(player);
        List<TrafficLightsBlockEntity> linkedLights = new ArrayList<>();
        boolean allValid = true;

        for (BlockPos pos : positions) {
            if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl) {
                if (groupId.equals(tl.getGroupId())) {
                    linkedLights.add(tl);
                } else {
                    allValid = false;
                    break;
                }
            } else {
                allValid = false;
                break;
            }
        }

        if (!allValid || linkedLights.isEmpty()) {
            ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c一些已链接的红绿灯已被破坏，链接组已失效！"), false);
            ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§7请重新使用链接魔杖创建链接组。"), false);
            return;
        }

        for (TrafficLightsBlockEntity tl : linkedLights) {
            tl.setTimings(phaseCount, timings, normalizedDelays);
        }

        ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal(""), false);
        ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§c⚪§e⚪§a⚪§a§l云北城建红绿灯控制面板 ===== 操作提示"), false);
        ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§e相位数量：§6§l" + phaseCount), false);
        for (int i = 0; i < phaseCount; i++) {
            ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§e相位" + (i + 1) + " §7: §6§l" + timings[i] + " §7秒"
                    + (normalizedDelays[i] > 0 ? " §7(延迟 §6" + normalizedDelays[i] + " §7秒)" : "")), false);
        }
        ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal(""), false);

        boolean autoAssigned = TrafficLightsAutoAssigner.tryAutoAssign(world, linkedLights, positions, phaseCount, player);
        if (!autoAssigned) {
            ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§e使用§6魔杖§e右键红绿灯设置各红绿灯的相位"), false);
        }
        ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§7运行规则：§a绿灯时间（相位时间 - 6s）+ §9闪烁时间（倒数 6s - 3s）+ §e黄灯时间（相位倒数 3s）"), false);
        ApiCompat.message(player, com.beigu.yunbeiuc.api.text.Text.literal("§a========================================"), false);
    }
}
