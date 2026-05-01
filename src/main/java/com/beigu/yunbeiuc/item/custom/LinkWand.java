package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightsGroup;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightsManager;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightsBlock;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LinkWand extends Item {
    private static final Map<PlayerEntity, List<BlockPos>> playerSelections = new HashMap<>();
    private static final Map<PlayerEntity, TrafficLightsGroup> pendingGroups = new HashMap<>();
    private static final Map<PlayerEntity, Integer> playerModes = new HashMap<>(); // 存储每个玩家的模式

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
        if (world.isClient) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        if (!playerModes.containsKey(player)) {
            player.sendMessage(Text.literal("请先输入 /yunbeiuc lights <mode> 选择模式！").formatted(Formatting.RED), false);
            return ActionResult.FAIL;
        }

        if (!(world.getBlockState(pos).getBlock() instanceof TrafficLightsBlock)) {
            player.sendMessage(Text.literal("请点击红绿灯方块！").formatted(Formatting.RED), false);
            return ActionResult.FAIL;
        }

        return handleLightSelection(world, player, pos);
    }

    private ActionResult handleLightSelection(World world, PlayerEntity player, BlockPos pos) {
        List<BlockPos> selections = playerSelections.computeIfAbsent(player, k -> new ArrayList<>());
        int mode = playerModes.get(player);
        int requiredCount = (mode == 1) ? 8 : 3;

        if (selections.contains(pos)) {
            player.sendMessage(Text.literal("已经选择了这个方块！").formatted(Formatting.YELLOW), false);
            return ActionResult.FAIL;
        }

        selections.add(pos);
        player.sendMessage(Text.literal("已选择第 " + selections.size() + " 个方块 (需要" + requiredCount + "个)").formatted(Formatting.GREEN), false);

        if (selections.size() == requiredCount) {
            if (mode == 1) {
                checkAndPromptForCross(world, player, selections);
            } else {
                checkAndPromptForT(world, player, selections);
            }
        }

        return ActionResult.SUCCESS;
    }

    // ==================== 十字路口验证（原有逻辑） ====================
    private void checkAndPromptForCross(World world, PlayerEntity player, List<BlockPos> selections) {
        TrafficLightsGroup tempGroup = validateCrossGroup(world, selections);

        if (tempGroup != null) {
            pendingGroups.put(player, tempGroup);
            player.sendMessage(Text.literal("✓ 检测到一组有效的十字路口红绿灯！").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("请输入 §e/yunbeiuc answer confirm §r确认创建").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("输入 §e/yunbeiuc answer reset §r重新选择，§e/yunbeiuc answer cancel §r取消").formatted(Formatting.GOLD), false);
        } else {
            playerSelections.remove(player);
            player.sendMessage(Text.literal("✗ 选择的8个方块不符合标准！需要每个方向各一个左转和一个直行").formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("请重新选择8个方块").formatted(Formatting.YELLOW), false);
        }
    }

    private TrafficLightsGroup validateCrossGroup(World world, List<BlockPos> selections) {
        Map<Direction, List<BlockPos>> leftMap = new HashMap<>();
        Map<Direction, List<BlockPos>> straightMap = new HashMap<>();
        for (Direction dir : Direction.Type.HORIZONTAL) {
            leftMap.put(dir, new ArrayList<>());
            straightMap.put(dir, new ArrayList<>());
        }

        for (BlockPos pos : selections) {
            Direction facing = world.getBlockState(pos).get(TrafficLightsBlock.FACING);
            boolean isLeft = world.getBlockState(pos).getBlock() == MunicipalBlocks.TRAFFIC_LIGHTS_LEFT;
            if (isLeft) {
                leftMap.get(facing).add(pos);
            } else {
                straightMap.get(facing).add(pos);
            }
        }

        for (Direction dir : Direction.Type.HORIZONTAL) {
            if (leftMap.get(dir).size() != 1 || straightMap.get(dir).size() != 1) {
                return null;
            }
        }

        TrafficLightsGroup group = new TrafficLightsGroup(UUID.randomUUID(), 1);
        for (Direction dir : Direction.Type.HORIZONTAL) {
            group.addLight(leftMap.get(dir).get(0), dir, true);
            group.addLight(straightMap.get(dir).get(0), dir, false);
        }
        return group;
    }

    // ==================== T字路口验证 ====================
    private void checkAndPromptForT(World world, PlayerEntity player, List<BlockPos> selections) {
        TrafficLightsGroup tempGroup = validateTGroup(world, selections);

        if (tempGroup != null) {
            pendingGroups.put(player, tempGroup);
            player.sendMessage(Text.literal("✓ 检测到一组有效的T字路口红绿灯！").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("请输入 §e/yunbeiuc answer confirm §r确认创建").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("输入 §e/yunbeiuc answer reset §r重新选择，§e/yunbeiuc answer cancel §r取消").formatted(Formatting.GOLD), false);
        } else {
            playerSelections.remove(player);
            player.sendMessage(Text.literal("✗ 选择的3个方块不符合T字路口标准！").formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("T字路口需要3个不同方向各一个直行红绿灯，且缺失方向形成T字形").formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("请重新选择3个方块").formatted(Formatting.YELLOW), false);
        }
    }

    private TrafficLightsGroup validateTGroup(World world, List<BlockPos> selections) {
        // T字路口只用直行灯
        Set<Direction> selectedDirs = new HashSet<>();
        Map<Direction, BlockPos> dirToPos = new HashMap<>();

        for (BlockPos pos : selections) {
            Direction facing = world.getBlockState(pos).get(TrafficLightsBlock.FACING);
            // T字路口只允许直行灯
            boolean isLeft = world.getBlockState(pos).getBlock() == MunicipalBlocks.TRAFFIC_LIGHTS_LEFT;
            if (isLeft) {
                return null; // 不允许左转灯
            }
            if (selectedDirs.contains(facing)) {
                return null; // 重复方向
            }
            selectedDirs.add(facing);
            dirToPos.put(facing, pos);
        }

        // 必须有恰好3个不同方向
        if (selectedDirs.size() != 3) {
            return null;
        }

        // 验证是否为有效的T字组合（缺失方向必须是单个方向）
        Set<Direction> allDirs = new HashSet<>(List.of(Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST));
        allDirs.removeAll(selectedDirs);
        if (allDirs.size() != 1) {
            return null; // 缺失方向不是恰好1个
        }

        // 有效的T字组合：缺失方向为NORTH/EAST/SOUTH/WEST之一
        TrafficLightsGroup group = new TrafficLightsGroup(UUID.randomUUID(), 2);
        for (Direction dir : selectedDirs) {
            group.addLight(dirToPos.get(dir), dir, false); // 全部作为直行灯
        }
        return group;
    }

    // ==================== 模式设置 ====================
    public static void setPlayerMode(PlayerEntity player, int mode) {
        playerModes.put(player, mode);
        playerSelections.remove(player); // 清除旧选择
        pendingGroups.remove(player);
    }

    // ==================== 回答处理 ====================
    public static boolean handleAnswerInput(ServerPlayerEntity player, String action) {
        if (!pendingGroups.containsKey(player)) {
            player.sendMessage(Text.literal("没有待确认的红绿灯组！请先点击红绿灯").formatted(Formatting.RED), false);
            return false;
        }

        if (action.equalsIgnoreCase("confirm")) {
            TrafficLightsGroup group = pendingGroups.get(player);
            ServerWorld serverWorld = player.getServerWorld();

            group.setWorld(serverWorld);
            TrafficLightsManager.get(serverWorld).addGroup(group);
            group.updateAllLightsImmediately(serverWorld);

            player.sendMessage(Text.literal("✓ 红绿灯组创建成功！").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("组ID: " + group.getGroupId().toString().substring(0, 8) +
                    " | 模式: " + (group.getMode() == 1 ? "十字路口" : "T字路口") +
                    " | 包含 " + group.getLightCount() + " 个红绿灯").formatted(Formatting.AQUA), false);
            player.sendMessage(Text.literal("现在可以继续选择下一组红绿灯了").formatted(Formatting.AQUA), false);

            pendingGroups.remove(player);
            playerSelections.remove(player);
            return true;

        } else if (action.equalsIgnoreCase("reset")) {
            pendingGroups.remove(player);
            playerSelections.remove(player);
            player.sendMessage(Text.literal("已取消当前选择，请重新点击红绿灯").formatted(Formatting.YELLOW), false);
            return true;

        } else if (action.equalsIgnoreCase("cancel")) {
            pendingGroups.remove(player);
            playerSelections.remove(player);
            playerModes.remove(player);
            player.sendMessage(Text.literal("已取消所有操作，如需继续请重新输入 /yunbeiuc lights <mode>").formatted(Formatting.GRAY), false);
            return true;
        }

        return false;
    }

    public static int getSelectionCount(PlayerEntity player) {
        List<BlockPos> selections = playerSelections.get(player);
        return selections == null ? 0 : selections.size();
    }

    public static void clearAll(PlayerEntity player) {
        playerSelections.remove(player);
        pendingGroups.remove(player);
        playerModes.remove(player);
    }

    public static boolean hasPendingGroup(PlayerEntity player) {
        return pendingGroups.containsKey(player);
    }

    public static boolean isModeSelected(PlayerEntity player) {
        return playerModes.containsKey(player);
    }

    public static int getPlayerMode(PlayerEntity player) {
        return playerModes.getOrDefault(player, 0);
    }
}