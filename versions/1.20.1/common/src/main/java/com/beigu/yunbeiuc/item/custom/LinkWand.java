package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LinkWand extends Item {
    private static final Map<UUID, List<BlockPos>> PLAYER_LINKING = new HashMap<>();

    public LinkWand(Settings settings) {
        super(settings);
    }

    public static List<BlockPos> getPlayerLinkedPositions(UUID playerId) {
        return PLAYER_LINKING.get(playerId);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.yunbeiuc.link_wand.tooltip"));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        if (player == null) return ActionResult.PASS;
        if (world.isClient()) return ActionResult.SUCCESS;

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof TrafficLightsBlockEntity)) {
            var state = world.getBlockState(pos);
            if (state.getBlock() instanceof com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPavementIntegrationBlock) {
                var part = state.get(com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPavementIntegrationBlock.PART);
                BlockPos bottomPos = switch (part) {
                    case BOTTOM -> pos;
                    case MIDDLE -> pos.down();
                    case TOP -> pos.down(2);
                };
                blockEntity = world.getBlockEntity(bottomPos);
                pos = bottomPos;
            }
        }

        if (!(blockEntity instanceof TrafficLightsBlockEntity)) {
            player.sendMessage(Text.literal("§c这不是一个红绿灯！"), true);
            return ActionResult.FAIL;
        }

        UUID playerId = player.getUuid();
        List<BlockPos> linkedLights = PLAYER_LINKING.computeIfAbsent(playerId, k -> new ArrayList<>());

        if (player.isSneaking()) {
            if (linkedLights.size() < 2) {
                player.sendMessage(Text.literal("§c你需要链接至少2个红绿灯！"), true);
                return ActionResult.FAIL;
            }

            for (BlockPos linkedPos : linkedLights) {
                if (!(world.getBlockEntity(linkedPos) instanceof TrafficLightsBlockEntity tl)) {
                    player.sendMessage(Text.literal("§c一些已链接的红绿灯不再有效！"), true);
                    PLAYER_LINKING.remove(playerId);
                    return ActionResult.FAIL;
                }
                if (tl.isInGroup()) {
                    player.sendMessage(Text.literal("§c红绿灯 §6" + linkedPos.toShortString() + " §c已有相位序列！"), true);
                    PLAYER_LINKING.remove(playerId);
                    return ActionResult.FAIL;
                }
            }

            String groupId = UUID.randomUUID().toString();
            List<BlockPos> finalPositions = new ArrayList<>(linkedLights);

            for (BlockPos linkedPos : finalPositions) {
                BlockEntity be = world.getBlockEntity(linkedPos);
                if (be instanceof TrafficLightsBlockEntity tl) {
                    tl.setGroup(groupId, finalPositions);
                }
            }

            player.sendMessage(Text.literal("§a已成功链接 §6§l" + finalPositions.size() + " §a个红绿灯"), true);
            player.sendMessage(Text.literal("§7请用 §e普通魔杖 §7右键任意已链接的红绿灯以设置时间"), true);

            PLAYER_LINKING.remove(playerId);
            return ActionResult.SUCCESS;
        }

        if (linkedLights.contains(pos)) {
            linkedLights.remove(pos);
            if (linkedLights.isEmpty()) {
                player.sendMessage(Text.literal("§c已清空链接组"), true);
            } else {
                player.sendMessage(Text.literal("§a已成功链接 §6§l" + linkedLights.size() + " §a个红绿灯 §7| §eshift+右键完成链接"), true);
            }
        } else {
            if (blockEntity instanceof TrafficLightsBlockEntity tl && tl.isInGroup()) {
                player.sendMessage(Text.literal("§c该红绿灯已有相位序列，无法链接！"), true);
                return ActionResult.FAIL;
            }
            linkedLights.add(pos);
            player.sendMessage(Text.literal("§a已成功链接 §6§l" + linkedLights.size() + " §a个红绿灯 §7| §eshift+右键完成链接"), true);
        }

        return ActionResult.SUCCESS;
    }

    public static void clearPlayerLinking(PlayerEntity player) {
        PLAYER_LINKING.remove(player.getUuid());
    }
}