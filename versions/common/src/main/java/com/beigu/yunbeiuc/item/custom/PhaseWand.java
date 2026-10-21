package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.api.mapper.ItemCompat;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.network.NetworkCompat;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.network.ModMessages;
import com.beigu.yunbeiuc.network.RailPhaseAimPacket;
import com.beigu.yunbeiuc.network.RailPhaseOpenPacket;
import com.beigu.yunbeiuc.signal.RailPhaseBinding;
import com.beigu.yunbeiuc.signal.RailSignalRegistry;
import com.beigu.yunbeiuc.signal.mtr.RailRaycast;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

// 普通魔杖：手持显示周围所有已组轨道的高亮线，瞄准轨道右键开 RailPhaseScreen。
// 不存任何组信息（一律从世界里现查）；右键编辑距离上限 7 格，超出静默不动作；潜行一律 PASS。
// 物品上没有 tooltip；轨道魔杖判定统一在 isHoldingRailWand（普通魔杖或链接魔杖）。

public class PhaseWand extends ItemCompat {

    private static final org.apache.logging.log4j.Logger LOGGER =
            com.beigu.yunbeiuc.YunbeiUrbanConstruction.LOGGER;

    private static final double AIM_MAX_DISTANCE = 7.0;

    private static final double AIM_THRESHOLD = 1.25;

    private static boolean highlightLogged;

    private static volatile boolean holdingWand;

    public static void setHoldingWand(boolean holding) {
        holdingWand = holding;
    }

    public static boolean isHoldingWandCached() {
        return holdingWand;
    }

    public static void noteWandHighlight() {
        if (highlightLogged) return;
        highlightLogged = true;
        LOGGER.info("[yunbeiuc] 手持魔杖：MTR 轨道着色已启用（{}，renderColors = true）",
                com.beigu.yunbeiuc.signal.mtr.MtrVersion.detect().displayName());
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

        if (world.isClientSide) {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof TrafficLightsBlockEntity) && !isMtrRailNode(world, pos)) {
                sendAim(player);
            }
            return InteractionResult.sidedSuccess(true);
        }

        if (!(player instanceof ServerPlayer)) return InteractionResult.PASS;

        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity) {
            return InteractionResult.sidedSuccess(false);
        }

        if (isMtrRailNode(world, pos)) {
            message(player, "§7链接轨道请用 §e链接魔杖 §7：右键两个轨道节点即可把这条轨道加入本次正在链接的相位组");
            return InteractionResult.sidedSuccess(false);
        }

        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        if (player.isShiftKeyDown()) return InteractionResultHolder.pass(stack);

        if (level.isClientSide) {
            return sendAim(player)
                    ? InteractionResultHolder.sidedSuccess(stack, true)
                    : InteractionResultHolder.pass(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    private static boolean sendAim(Player player) {
        final RailRaycast.Hit hit = RailRaycast.raycastClient(player.getEyePosition(1.0f),
                player.getLookAngle(), AIM_MAX_DISTANCE, AIM_THRESHOLD);
        if (hit == null) return false;

        final FriendlyByteBuf buf = NetworkCompat.newBuffer();
        new RailPhaseAimPacket(hit.start, hit.end).write(buf);
        NetworkCompat.sendToServer(ModMessages.AIM_RAIL_PHASE, buf);
        return true;
    }

    public static void openRailPhaseScreen(ServerPlayer player, BlockPos start, BlockPos end) {
        if (player == null || start == null || end == null) return;

        final RailSignalRegistry.Entry entry =
                RailSignalRegistry.get(RailPhaseBinding.railProductOf(start, end));
        final BlockPos groupMember = entry == null ? null : entry.groupAnchor;
        if (groupMember == null) {
            message(player, "§c这条轨道还没有链接到任何相位组：请先用链接魔杖组网并把轨道链接进来！");
            return;
        }

        final TrafficLightsBlockEntity group = resolveGroup(player.getCommandSenderWorld(), groupMember);
        if (group == null) {
            message(player, "§c相位组已失效！");
            return;
        }

        final RailPhaseBinding binding = group.findRailBinding(start, end);
        if (binding == null) {
            LOGGER.info("[yunbeiuc] 打开相位界面失败：轨道 {} → {} 不在本组（本组共 {} 条）",
                    start.toShortString(), end.toShortString(), group.getRailBindings().size());
            message(player, "§c这条轨道还没有链接到本组，请先用链接魔杖把它链接进来！");
            return;
        }

        final FriendlyByteBuf buf = NetworkCompat.newBuffer();
        new RailPhaseOpenPacket(start, end, groupMember,
                group.getPhaseCount(), group.getPhaseTimes(), binding.getPhaseMask()).write(buf);
        NetworkCompat.sendToPlayer(player, ModMessages.OPEN_RAIL_PHASE, buf);
        LOGGER.info("[yunbeiuc] 已发送轨道相位界面数据：{} → {}，相位 {} 个，掩码 {}",
                start.toShortString(), end.toShortString(),
                group.getPhaseCount(), Long.toBinaryString(binding.getPhaseMask()));
    }

    private static boolean isMtrRailNode(Level level, BlockPos pos) {
        try {
            final ResourceLocation id = VersionServices.registries().blockId(level.getBlockState(pos).getBlock());
            return id != null && "mtr".equals(id.getNamespace()) && "rail".equals(id.getPath());
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
        player.displayClientMessage(Text.literal(text), true);
    }
}
