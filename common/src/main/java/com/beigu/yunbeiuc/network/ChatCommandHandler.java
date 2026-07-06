package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.custom.LinkWand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;

import java.util.*;

public class ChatCommandHandler {
    private static final Map<UUID, GroupInfo> PLAYER_LAST_GROUP = new HashMap<>();

    private static class GroupInfo {
        final String groupId;
        final List<BlockPos> positions;

        GroupInfo(String groupId, List<BlockPos> positions) {
            this.groupId = groupId;
            this.positions = new ArrayList<>(positions);
        }
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("yunbeiuc")
                .then(CommandManager.literal("lights")
                        .then(CommandManager.argument("phaseCount", IntegerArgumentType.integer(2, 16))
                                .then(CommandManager.argument("timings", StringArgumentType.greedyString())
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            int phaseCount = IntegerArgumentType.getInteger(context, "phaseCount");
                                            String timingsStr = StringArgumentType.getString(context, "timings");
                                            return executeTiming(source, phaseCount, timingsStr);
                                        })
                                )
                        )
                )
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    PlayerEntity player = source.getPlayer();
                    if (player != null) {
                        LinkWand.clearPlayerLinking(player);
                        PLAYER_LAST_GROUP.remove(player.getUuid());
                    }
                    source.sendFeedback(() -> Text.literal("§e已取消链接操作"), false);
                    return 1;
                })
        );
    }

    public static void setPlayerLastGroup(PlayerEntity player, String groupId, List<BlockPos> positions) {
        PLAYER_LAST_GROUP.put(player.getUuid(), new GroupInfo(groupId, positions));
    }

    private static int executeTiming(ServerCommandSource source, int phaseCount, String timingsStr) {
        PlayerEntity player = source.getPlayer();
        if (player == null) return 0;

        String[] parts = timingsStr.trim().split("\\s+");

        if (parts.length != phaseCount) {
            final int count = phaseCount;
            source.sendFeedback(() -> Text.literal("§c时间数量不匹配！需要 " + count + " 个时间值"), false);
            StringBuilder example = new StringBuilder();
            for (int i = 0; i < count; i++) {
                example.append("40 ");
            }
            final String exampleStr = example.toString().trim();
            source.sendFeedback(() -> Text.literal("§7示例：/yunbeiuc lights " + count + " " + exampleStr), false);
            return 0;
        }

        int[] timings = new int[phaseCount];
        try {
            for (int i = 0; i < phaseCount; i++) {
                timings[i] = Integer.parseInt(parts[i]);
                if (timings[i] < 7) {
                    final int pos = i + 1;
                    source.sendFeedback(() -> Text.literal("§c时间至少需要7秒！(第" + pos + "个时间)"), false);
                    return 0;
                }
                if (timings[i] > 300) {
                    final int pos = i + 1;
                    source.sendFeedback(() -> Text.literal("§c时间不能超过300秒！(第" + pos + "个时间)"), false);
                    return 0;
                }
            }
        } catch (NumberFormatException e) {
            source.sendFeedback(() -> Text.literal("§c格式无效！请输入数字。"), false);
            return 0;
        }

        GroupInfo groupInfo = PLAYER_LAST_GROUP.get(player.getUuid());
        if (groupInfo == null) {
            source.sendFeedback(() -> Text.literal("§c未找到已链接的红绿灯！请先用链接魔杖链接红绿灯。"), false);
            return 0;
        }

        World world = player.getWorld();
        List<TrafficLightsBlockEntity> linkedLights = new ArrayList<>();
        boolean allValid = true;

        for (BlockPos pos : groupInfo.positions) {
            if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity tl) {
                if (groupInfo.groupId.equals(tl.getGroupId())) {
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
            source.sendFeedback(() -> Text.literal("§c一些已链接的红绿灯已被破坏，链接组已失效！"), false);
            source.sendFeedback(() -> Text.literal("§7请重新使用链接魔杖创建链接组。"), false);
            PLAYER_LAST_GROUP.remove(player.getUuid());
            return 0;
        }

        for (TrafficLightsBlockEntity tl : linkedLights) {
            tl.setTimings(phaseCount, timings);
        }

        final int finalPhaseCount = phaseCount;
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("§a§l========== 时间设置成功 =========="), false);
        source.sendFeedback(() -> Text.literal("§e相位数量：§6§l" + finalPhaseCount), false);
        source.sendFeedback(() -> Text.literal(""), false);
        for (int i = 0; i < finalPhaseCount; i++) {
            final int index = i;
            source.sendFeedback(() -> Text.literal("§e相位" + (index + 1) + " §7: §6§l" + timings[index] + " §7秒"), false);
        }
        source.sendFeedback(() -> Text.literal(""), false);
        source.sendFeedback(() -> Text.literal("§e使用§6魔杖§e右键红绿灯设置各红绿灯的相位"), false);
        source.sendFeedback(() -> Text.literal("§7运行规则："), false);
        source.sendFeedback(() -> Text.literal("§7  • 绿灯时间 = 设置时间 - 3秒"), false);
        source.sendFeedback(() -> Text.literal("§7  • 倒数6-3秒闪烁"), false);
        source.sendFeedback(() -> Text.literal("§7  • 最后3秒黄灯"), false);
        source.sendFeedback(() -> Text.literal("§a========================================"), false);

        return 1;
    }
}