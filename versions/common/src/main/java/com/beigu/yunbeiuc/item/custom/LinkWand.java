package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LinkWand extends Item {
    private static final Map<UUID, List<BlockPos>> PLAYER_LINKING = new HashMap<>();

    public LinkWand(Properties settings) {
        super(settings);
    }

    public static List<BlockPos> getPlayerLinkedPositions(UUID playerId) {
        return PLAYER_LINKING.get(playerId);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.link_wand.tooltip"));
        super .appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player == null) return InteractionResult.PASS;
        if (world .isClientSide) return InteractionResult.SUCCESS;

        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (!(blockEntity instanceof TrafficLightsBlockEntity)) {
            var state = world.getBlockState(pos);
            if (state.getBlock() instanceof com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPavementIntegrationBlock) {
                var part = state.getValue(com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPavementIntegrationBlock.PART);
                BlockPos bottomPos = switch (part) {
                    case BOTTOM -> pos;
                    case MIDDLE -> pos.below();
                    case TOP -> pos.below(2);
                };
                blockEntity = world.getBlockEntity(bottomPos);
                pos = bottomPos;
            }
        }

        if (!(blockEntity instanceof TrafficLightsBlockEntity)) {
            player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c这不是一个红绿灯！"), true);
            return InteractionResult.FAIL;
        }

        UUID playerId = player.getUUID();
        List<BlockPos> linkedLights = PLAYER_LINKING.computeIfAbsent(playerId, k -> new ArrayList<>());

        if (player.isShiftKeyDown()) {
            if (linkedLights.size() < 2) {
                player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c你需要链接至少2个红绿灯！"), true);
                return InteractionResult.FAIL;
            }

            for (BlockPos linkedPos : linkedLights) {
                if (!(world.getBlockEntity(linkedPos) instanceof TrafficLightsBlockEntity tl)) {
                    player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c一些已链接的红绿灯不再有效！"), true);
                    PLAYER_LINKING.remove(playerId);
                    return InteractionResult.FAIL;
                }
                if (tl.isInGroup()) {
                    player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c红绿灯 §6" + linkedPos.toShortString() + " §c已有相位序列！"), true);
                    PLAYER_LINKING.remove(playerId);
                    return InteractionResult.FAIL;
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

            player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§a已成功链接 §6§l" + finalPositions.size() + " §a个红绿灯"), true);
            player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§7请用 §e普通魔杖 §7右键任意已链接的红绿灯以设置时间"), true);

            PLAYER_LINKING.remove(playerId);
            return InteractionResult.SUCCESS;
        }

        if (linkedLights.contains(pos)) {
            linkedLights.remove(pos);
            if (linkedLights.isEmpty()) {
                player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c已清空链接组"), true);
            } else {
                player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§a已成功链接 §6§l" + linkedLights.size() + " §a个红绿灯 §7| §eshift+右键完成链接"), true);
            }
        } else {
            if (blockEntity instanceof TrafficLightsBlockEntity tl && tl.isInGroup()) {
                player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§c该红绿灯已有相位序列，无法链接！"), true);
                return InteractionResult.FAIL;
            }
            linkedLights.add(pos);
            player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.literal("§a已成功链接 §6§l" + linkedLights.size() + " §a个红绿灯 §7| §eshift+右键完成链接"), true);
        }

        return InteractionResult.SUCCESS;
    }

    public static void clearPlayerLinking(Player player) {
        PLAYER_LINKING.remove(player.getUUID());
    }
}
