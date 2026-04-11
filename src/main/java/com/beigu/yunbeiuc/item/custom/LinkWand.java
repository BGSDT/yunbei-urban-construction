package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
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
    // 存储玩家是否已选择模式
    private static final Set<PlayerEntity> modeSelected = new HashSet<>();

    public LinkWand(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        PlayerEntity player = context.getPlayer();
        BlockPos pos = context.getBlockPos();

        // 调试信息
        System.out.println("玩家 " + player.getName().getString() + " 点击了位置 " + pos);
        System.out.println("modeSelected状态: " + modeSelected.contains(player));
        System.out.println("pendingGroups状态: " + pendingGroups.containsKey(player));

        // 检查玩家是否已选择模式
        if (!modeSelected.contains(player)) {
            player.sendMessage(Text.literal("请先输入 /yunbeiuc lights 1 选择模式！").formatted(Formatting.RED), false);
            return ActionResult.FAIL;
        }

        // 检查点击的方块是否是红绿灯
        if (!(world.getBlockState(pos).getBlock() instanceof TrafficLightsBlock)) {
            player.sendMessage(Text.literal("请点击红绿灯方块！").formatted(Formatting.RED), false);
            return ActionResult.FAIL;
        }

        // 处理红绿灯选择
        return handleLightSelection(world, player, pos);
    }

    /**
     * 处理红绿灯选择
     */
    private ActionResult handleLightSelection(World world, PlayerEntity player, BlockPos pos) {
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
     * 检查选择的8个方块是否符合标准
     */
    private void checkAndPromptForGroup(World world, PlayerEntity player, List<BlockPos> selections) {
        // 检查是否符合标准
        TrafficLightGroup tempGroup = validateAndCreateGroup(world, selections);

        if (tempGroup != null) {
            // 符合标准，保存临时组并提示玩家确认
            pendingGroups.put(player, tempGroup);
            player.sendMessage(Text.literal("✓ 检测到一组有效的红绿灯！").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("请输入 §e/yunbeiuc answer confirm §r确认创建").formatted(Formatting.GOLD), false);
            player.sendMessage(Text.literal("输入 §e/yunbeiuc answer reset §r重新选择，§e/yunbeiuc answer cancel §r取消").formatted(Formatting.GOLD), false);

            // 调试信息
            System.out.println("已保存pendingGroups，大小: " + pendingGroups.size());
            System.out.println("当前玩家pendingGroups: " + pendingGroups.containsKey(player));
        } else {
            // 不符合标准，清空选择并提示
            playerSelections.remove(player);
            player.sendMessage(Text.literal("✗ 选择的8个方块不符合标准！需要每个方向各一个左转和一个直行").formatted(Formatting.RED), false);
            player.sendMessage(Text.literal("请重新选择8个方块").formatted(Formatting.YELLOW), false);
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
            boolean isLeft = world.getBlockState(pos).getBlock() == MunicipalBlocks.TRAFFIC_LIGHTS_LEFT;

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
     * 设置玩家已选择模式
     */
    public static void setModeSelected(PlayerEntity player) {
        modeSelected.add(player);
        System.out.println("玩家 " + player.getName().getString() + " 已选择模式，modeSelected大小: " + modeSelected.size());
    }

    /**
     * 处理玩家回答输入（confirm/reset/cancel）
     */
    public static boolean handleAnswerInput(ServerPlayerEntity player, String action) {
        System.out.println("处理回答: " + action + " 玩家: " + player.getName().getString());
        System.out.println("pendingGroups包含玩家: " + pendingGroups.containsKey(player));
        System.out.println("pendingGroups大小: " + pendingGroups.size());

        if (!pendingGroups.containsKey(player)) {
            player.sendMessage(Text.literal("没有待确认的红绿灯组！请先点击8个红绿灯").formatted(Formatting.RED), false);
            return false;
        }

        if (action.equalsIgnoreCase("confirm")) {
            // 确认创建组
            TrafficLightGroup group = pendingGroups.get(player);
            ServerWorld serverWorld = player.getServerWorld();

            // 设置世界引用
            group.setWorld(serverWorld);

            // 添加到管理器
            TrafficLightManager.get(serverWorld).addGroup(group);

            // 立即更新灯光
            group.updateAllLightsImmediately(serverWorld);

            player.sendMessage(Text.literal("✓ 红绿灯组创建成功！").formatted(Formatting.GREEN), false);
            player.sendMessage(Text.literal("组ID: " + group.getGroupId().toString().substring(0, 8) +
                    " | 包含 " + group.getLightCount() + " 个红绿灯").formatted(Formatting.AQUA), false);
            player.sendMessage(Text.literal("现在可以继续选择下一组红绿灯了").formatted(Formatting.AQUA), false);

            // 清理状态（保留模式选择，可以继续创建下一组）
            pendingGroups.remove(player);
            playerSelections.remove(player);

            System.out.println("组创建成功，已清理pendingGroups");
            return true;

        } else if (action.equalsIgnoreCase("reset")) {
            // 重新选择
            pendingGroups.remove(player);
            playerSelections.remove(player);
            player.sendMessage(Text.literal("已取消当前选择，请重新点击8个红绿灯").formatted(Formatting.YELLOW), false);
            System.out.println("已重置选择");
            return true;

        } else if (action.equalsIgnoreCase("cancel")) {
            // 完全取消
            pendingGroups.remove(player);
            playerSelections.remove(player);
            modeSelected.remove(player); // 清除模式选择，需要重新输入指令
            player.sendMessage(Text.literal("已取消所有操作，如需继续请重新输入 /yunbeiuc lights 1").formatted(Formatting.GRAY), false);
            System.out.println("已取消所有操作");
            return true;
        }

        return false;
    }

    /**
     * 获取玩家当前选择的方块数量
     */
    public static int getSelectionCount(PlayerEntity player) {
        List<BlockPos> selections = playerSelections.get(player);
        return selections == null ? 0 : selections.size();
    }

    /**
     * 清除玩家的所有状态
     */
    public static void clearAll(PlayerEntity player) {
        playerSelections.remove(player);
        pendingGroups.remove(player);
        modeSelected.remove(player);
    }

    /**
     * 检查玩家是否有待确认的组
     */
    public static boolean hasPendingGroup(PlayerEntity player) {
        return pendingGroups.containsKey(player);
    }

    /**
     * 检查玩家是否已选择模式
     */
    public static boolean isModeSelected(PlayerEntity player) {
        return modeSelected.contains(player);
    }
}