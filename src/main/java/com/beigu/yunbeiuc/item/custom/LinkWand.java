package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightGroup;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightManager;
import com.beigu.yunbeiuc.block.custom.lights.TrafficLightsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public class LinkWand extends Item {
    // 存储每个玩家的当前选择
    private static final Map<PlayerEntity, List<BlockPos>> playerSelections = new HashMap<>();
    // 存储等待玩家确认的临时组
    private static final Map<PlayerEntity, TrafficLightGroup> pendingGroups = new HashMap<>();

    public LinkWand(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        // 检查点击的方块是否是红绿灯
        if (!(world.getBlockState(pos).getBlock() instanceof TrafficLightsBlock)) {
            player.sendMessage(Text.literal("请点击红绿灯方块！").formatted(Formatting.RED), false);
            return ActionResult.FAIL;
        }

        // 获取或创建玩家的选择列表
        List<BlockPos> selections = playerSelections.computeIfAbsent(player, k -> new ArrayList<>());

        // 检查是否已经选择了这个方块
        if (selections.contains(pos)) {
            player.sendMessage(Text.literal("已经选择了这个方块！").formatted(Formatting.YELLOW), false);
            return ActionResult.FAIL;
        }

        // 添加选择
        selections.add(pos);
        player.sendMessage(Text.literal("已选择第 " + selections.size() + " 个方块 (需要8个)").formatted(Formatting.GREEN), false);

        // 当选择满8个方块时，检查是否符合标准
        if (selections.size() == 8) {
            checkAndPromptForGroup(world, player, selections);
        }

        return ActionResult.SUCCESS;
    }

    /**
     * 检查选择的8个方块是否符合标准，如果符合则提示玩家输入指令确认
     */
    private void checkAndPromptForGroup(World world, PlayerEntity player, List<BlockPos> selections) {
        // 检查是否符合标准
        TrafficLightGroup tempGroup = validateAndCreateGroup(world, selections);

        if (tempGroup != null) {
            // 符合标准，保存临时组并提示玩家确认
            pendingGroups.put(player, tempGroup);
            player.sendMessage(Text.literal("✓ 检测到一组有效的红绿灯！").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("请输入 §e/traffic yes §r来确认创建").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("或输入 §e/traffic no §r重新选择，§e/traffic cancel §r取消操作").formatted(Formatting.GOLD), false);
        } else {
            // 不符合标准，清空选择并提示
            playerSelections.remove(player);
            player.sendMessage(Text.literal("✗ 选择的8个方块不符合标准！需要每个方向各一个左转和一个直行").formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("请重新选择新的8个方块").formatted(Formatting.YELLOW), false);
        }
    }

    /**
     * 验证并创建临时的红绿灯组
     */
    private TrafficLightGroup validateAndCreateGroup(World world, List<BlockPos> selections) {
        // 按方向分类方块
        List<BlockPos> northLeft = new ArrayList<>();
        List<BlockPos> northStraight = new ArrayList<>();
        List<BlockPos> southLeft = new ArrayList<>();
        List<BlockPos> southStraight = new ArrayList<>();
        List<BlockPos> eastLeft = new ArrayList<>();
        List<BlockPos> eastStraight = new ArrayList<>();
        List<BlockPos> westLeft = new ArrayList<>();
        List<BlockPos> westStraight = new ArrayList<>();

        for (BlockPos pos : selections) {
            Direction facing = world.getBlockState(pos).get(TrafficLightsBlock.FACING);
            boolean isLeft = world.getBlockState(pos).getBlock() == ModBlocks.TRAFFIC_LIGHTS_LEFT;

            switch (facing) {
                case NORTH:
                    if (isLeft) northLeft.add(pos);
                    else northStraight.add(pos);
                    break;
                case SOUTH:
                    if (isLeft) southLeft.add(pos);
                    else southStraight.add(pos);
                    break;
                case EAST:
                    if (isLeft) eastLeft.add(pos);
                    else eastStraight.add(pos);
                    break;
                case WEST:
                    if (isLeft) westLeft.add(pos);
                    else westStraight.add(pos);
                    break;
                default:
                    break;
            }
        }

        // 验证是否每个类别都有且只有一个方块
        if (northLeft.size() != 1 || northStraight.size() != 1 ||
                southLeft.size() != 1 || southStraight.size() != 1 ||
                eastLeft.size() != 1 || eastStraight.size() != 1 ||
                westLeft.size() != 1 || westStraight.size() != 1) {
            return null;
        }

        // 创建红绿灯组
        TrafficLightGroup group = new TrafficLightGroup(UUID.randomUUID());

        // 添加所有方块到组中
        group.addLight(northLeft.get(0), Direction.NORTH, true);
        group.addLight(northStraight.get(0), Direction.NORTH, false);
        group.addLight(southLeft.get(0), Direction.SOUTH, true);
        group.addLight(southStraight.get(0), Direction.SOUTH, false);
        group.addLight(eastLeft.get(0), Direction.EAST, true);
        group.addLight(eastStraight.get(0), Direction.EAST, false);
        group.addLight(westLeft.get(0), Direction.WEST, true);
        group.addLight(westStraight.get(0), Direction.WEST, false);

        return group;
    }

    /**
     * 获取玩家当前选择的方块数量
     */
    public static int getSelectionCount(PlayerEntity player) {
        List<BlockPos> selections = playerSelections.get(player);
        return selections == null ? 0 : selections.size();
    }

    /**
     * 清除玩家的选择（用于调试）
     */
    public static void clearSelection(PlayerEntity player) {
        playerSelections.remove(player);
        pendingGroups.remove(player);
    }

    /**
     * 处理玩家输入的指令
     * @param player 玩家
     * @param message 指令内容 (YES/NO/CANCEL)
     * @return 是否成功处理
     */
    public static boolean handlePlayerInput(ServerPlayerEntity player, String message) {
        if (!pendingGroups.containsKey(player)) {
            // 没有等待确认的组，返回false让消息正常广播
            return false;
        }

        if (message.equalsIgnoreCase("YES")) {
            // 确认创建组
            TrafficLightGroup group = pendingGroups.get(player);
            ServerWorld serverWorld = player.getServerWorld();

            // 设置世界引用，确保红绿灯能开始工作
            group.setWorld(serverWorld);

            // 添加到管理器
            TrafficLightManager.get(serverWorld).addGroup(group);

            // 立即更新一次所有红绿灯的状态，让它们根据当前阶段显示正确颜色
            group.updateAllLightsImmediately(serverWorld);

            player.sendMessage(Text.literal("✓ 红绿灯组创建成功！").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("组ID: " + group.getGroupId().toString().substring(0, 8) +
                    " | 包含 " + group.getLightCount() + " 个红绿灯").formatted(Formatting.AQUA), false);
            player.sendMessage(Text.literal("现在可以继续选择下一组红绿灯了").formatted(Formatting.AQUA), false);

            // 清理当前状态
            pendingGroups.remove(player);
            playerSelections.remove(player);

            return true;

        } else if (message.equalsIgnoreCase("NO")) {
            // 拒绝创建，重新选择
            pendingGroups.remove(player);
            playerSelections.remove(player);
            player.sendMessage(Text.literal("已取消当前选择，请重新选择新的8个方块").formatted(Formatting.YELLOW), false);

            return true;

        } else if (message.equalsIgnoreCase("CANCEL")) {
            // 完全取消操作
            pendingGroups.remove(player);
            playerSelections.remove(player);
            player.sendMessage(Text.literal("已取消所有操作").formatted(Formatting.GRAY), false);

            return true;
        }

        return false;
    }
}