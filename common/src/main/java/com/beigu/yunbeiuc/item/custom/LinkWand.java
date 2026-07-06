package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.network.ChatCommandHandler;
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
            player.sendMessage(Text.literal("§c这不是一个红绿灯！"), true);
            return ActionResult.FAIL;
        }

        UUID playerId = player.getUuid();
        List<BlockPos> linkedLights = PLAYER_LINKING.computeIfAbsent(playerId, k -> new ArrayList<>());

        // Shift+右键完成链接
        if (player.isSneaking()) {
            if (linkedLights.size() < 2) {
                player.sendMessage(Text.literal("§c你需要链接至少2个红绿灯！"), true);
                return ActionResult.FAIL;
            }

            for (BlockPos linkedPos : linkedLights) {
                if (!(world.getBlockEntity(linkedPos) instanceof TrafficLightsBlockEntity)) {
                    player.sendMessage(Text.literal("§c一些已链接的红绿灯不再有效！"), true);
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

            ChatCommandHandler.setPlayerLastGroup(player, groupId, finalPositions);

            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("§a§l========== 红绿灯链接完成 =========="), false);
            player.sendMessage(Text.literal("§e已成功链接 §6§l" + finalPositions.size() + " §e个红绿灯"), false);
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("§e§l使用以下命令设置时间和相位数量："), false);
            player.sendMessage(Text.literal("§6  /yunbeiuc lights <相位数量> <时间1> <时间2> ..."), false);
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("§e示例（4个相位）："), false);
            player.sendMessage(Text.literal("§7  /yunbeiuc lights 4 40 40 40 40"), false);
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("§e示例（5个相位）："), false);
            player.sendMessage(Text.literal("§7  /yunbeiuc lights 5 30 35 40 35 30"), false);
            player.sendMessage(Text.literal(""), false);
            player.sendMessage(Text.literal("§e设置后使用§6魔杖§e右键红绿灯设置相位"), false);
            player.sendMessage(Text.literal("§a========================================"), false);

            PLAYER_LINKING.remove(playerId);
            return ActionResult.SUCCESS;
        }

        // 普通右键
        if (linkedLights.contains(pos)) {
            linkedLights.remove(pos);
            player.sendMessage(Text.literal("§c已从链接组移除 §7(剩余 §6" + linkedLights.size() + " §c个)"), true);
            if (linkedLights.isEmpty()) {
                player.sendMessage(Text.literal("§7提示：右键红绿灯继续添加，§eShift+右键 §7完成链接"), true);
            }
        } else {
            linkedLights.add(pos);
            player.sendMessage(Text.literal("§a已添加到链接组 §7(共 §6" + linkedLights.size() + " §a个)"), true);
            if (linkedLights.size() == 1) {
                player.sendMessage(Text.literal("§7  继续右键添加更多红绿灯"), true);
                player.sendMessage(Text.literal("§7  按 §eShift+右键 §7完成链接"), true);
            }
        }

        return ActionResult.SUCCESS;
    }

    public static void clearPlayerLinking(PlayerEntity player) {
        PLAYER_LINKING.remove(player.getUuid());
    }
}