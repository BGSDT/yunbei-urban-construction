package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.api.ApiCompat;

import com.beigu.yunbeiuc.api.mapper.ItemCompat;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.signal.ClientRailGroupIndex;
import com.beigu.yunbeiuc.signal.RailPhaseBinding;
import com.beigu.yunbeiuc.signal.mtr.RailRaycast;

import net.minecraft.core.BlockPos;
import com.beigu.yunbeiuc.api.resource.Id;
import com.beigu.yunbeiuc.api.resource.IdAdapter;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

// 普通魔杖：手持显示周围所有已组轨道的高亮线，瞄准轨道右键开 RailPhaseScreen。
// 不存任何组信息（一律从世界里现查）；右键编辑距离上限 7 格，超出静默不动作；潜行一律 PASS。
// 物品上没有 tooltip；轨道魔杖判定统一在 isHoldingRailWand（普通魔杖或链接魔杖）。

public class PhaseWand extends ItemCompat {

    private static final double AIM_MAX_DISTANCE = 7.0;

    private static final double AIM_THRESHOLD = 1.25;

    private static volatile boolean holdingWand;

    public static void setHoldingWand(boolean holding) {
        holdingWand = holding;
    }

    public static boolean isHoldingWandCached() {
        return holdingWand;
    }

    public static boolean isHoldingRailWand(Player player) {
        if (player == null) return false;
        return isRailWand(player.getMainHandItem()) || isRailWand(player.getOffhandItem());
    }

    private static boolean isRailWand(ItemStack stack) {
        if (stack == null) return false;
        return stack.getItem() instanceof PhaseWand || stack.getItem() instanceof LinkWand;
    }

    public PhaseWand(Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        if (world.isClientSide()) {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof TrafficLightsBlockEntity) && !isMtrRailNode(world, pos)) {
                sendAim(player);
            }
            return ApiCompat.sidedSuccess(true);
        }

        if (!(player instanceof ServerPlayer)) return InteractionResult.PASS;

        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity) {
            return ApiCompat.sidedSuccess(false);
        }

        if (isMtrRailNode(world, pos)) {
            message(player, "§7链接轨道请用 §e链接魔杖 §7：右键两个轨道节点即可把这条轨道加入本次正在链接的相位组");
            return ApiCompat.sidedSuccess(false);
        }

        return ApiCompat.sidedSuccess(false);
    }

    @Override
    protected ApiCompat.UseResult useCompat(Level level, Player player, InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) return ApiCompat.UseResult.PASS;

        if (level.isClientSide()) {
            return sendAim(player)
                    ? ApiCompat.UseResult.SUCCESS
                    : ApiCompat.UseResult.PASS;
        }

        return ApiCompat.UseResult.PASS;
    }

    private static boolean sendAim(Player player) {
        final RailRaycast.Hit hit = RailRaycast.raycastClient(player.getEyePosition(1.0f),
                player.getLookAngle(), AIM_MAX_DISTANCE, AIM_THRESHOLD);
        if (hit == null) return false;

        final Level level = ApiCompat.level(player);
        for (BlockPos anchor : ClientRailGroupIndex.validAnchors(level)) {
            if (!(level.getBlockEntity(anchor) instanceof TrafficLightsBlockEntity lights)) continue;
            final RailPhaseBinding binding = lights.findRailBinding(hit.start, hit.end);
            if (binding == null) continue;
            VersionServices.gui().openRailPhaseScreen(hit.start, hit.end, anchor,
                    lights.getPhaseCount(), lights.getPhaseTimes(), binding.getPhaseMask());
            return true;
        }

        message(player, "§c这条轨道还没有链接到任何相位组：请先用链接魔杖组网并把轨道链接进来！");
        return true;
    }

    private static boolean isMtrRailNode(Level level, BlockPos pos) {
        try {
            final Id id = VersionServices.registries().blockId(level.getBlockState(pos).getBlock());
            return id != null && "mtr".equals(id.namespace()) && "rail".equals(id.path());
        } catch (Throwable t) {
            return false;
        }
    }

    private static TrafficLightsBlockEntity resolveGroup(Level level, BlockPos memberPos) {
        if (level == null || memberPos == null) return null;
        final BlockEntity blockEntity = level.getBlockEntity(memberPos);
        if (blockEntity instanceof TrafficLightsBlockEntity lights && lights.isInGroup()) {
            return lights;
        }
        return null;
    }

    private static void message(Player player, String text) {
        ApiCompat.message(player, Text.literal(text), true);
    }
}
