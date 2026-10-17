package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.api.mapper.ItemCompat;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPavementIntegrationBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 链接魔杖：交通信号这块的**总入口**。一次做完两件事——
 *
 * <h3>1) 把若干红绿灯连成一个相位组</h3>
 * 右键红绿灯逐个加入，Shift+右键完成成组。
 *
 * <h3>2) 把 MTR 轨道链接进「本次正在链接的这一组」</h3>
 * 在同一轮链接里右键 MTR 轨道节点：第一次记起点，第二次记终点，这条轨道就记下了。
 * Shift+右键成组时，红绿灯和这些轨道**一起**写进新组 —— 一次做完，不用再补一遍。
 *
 * <p>顺序随意：先点红绿灯还是先点轨道都行，成组那一刻一起生效。
 * 没成组就退出的进度只存在内存里（见 {@link Session}），Shift+右键空气可手动清空。
 *
 * <h3>魔杖上不记「我在哪一组」</h3>
 * 全程只靠 {@link Session}（本次链接会话的临时状态）：成组之前，轨道属于
 * 「本次正在链接的这一组」；成组之后就不再需要「当前组」这个概念 —— 想往已有的组里
 * 再加轨道，就重新组一次网。所以魔杖不带任何组属性，丢给别人、重开游戏都不会带错组。
 */
public class LinkWand extends ItemCompat {

    /**
     * 每个玩家本次「链接会话」的临时状态。
     *
     * <p>纯运行期内存：只放「还没成组的中间态」。成组时一次性落盘进相位组，
     * 之后这份状态就没用了。魔杖物品上**不**存任何组信息。
     */
    private static final Map<UUID, Session> SESSIONS = new HashMap<>();

    private static final class Session {
        /** 本次要组成一个新组的红绿灯。 */
        final List<BlockPos> lights = new ArrayList<>();
        /** 本次已选好的轨道（每项 {起点, 终点}），成组时一次性写进新组。 */
        final List<BlockPos[]> rails = new ArrayList<>();
        /** 已点下的轨道起点；未选时为 null。 */
        BlockPos pendingStart;

        void clearPending() {
            lights.clear();
            rails.clear();
            pendingStart = null;
        }
    }

    private static Session sessionOf(Player player) {
        return SESSIONS.computeIfAbsent(player.getUUID(), uuid -> new Session());
    }

    public LinkWand(Properties settings) {
        super(settings);
    }

    /** 本次正在链接的红绿灯（供 {@code LinkWandRenderer} 画描边用）。 */
    public static List<BlockPos> getPlayerLinkedPositions(UUID playerId) {
        final Session session = SESSIONS.get(playerId);
        return session == null ? null : session.lights;
    }

    public static void clearPlayerLinking(Player player) {
        SESSIONS.remove(player.getUUID());
    }

    @Override
    public void appendHoverTextCompat(ItemStack stack, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Text.translatable("item.yunbeiuc.link_wand.tooltip"));
        tooltip.add(Text.translatable("item.yunbeiuc.link_wand.tooltip2"));
    }

    // ------------------------------------------------------------------ 右键方块

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        final Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        // 人行道一体化红绿灯是 3 段方块，方块实体只挂在最下面一段：
        // 无论点中哪一段都先解析到 BOTTOM，再取方块实体。
        // 不能只在「点中的位置没有方块实体」时才解析——那样一旦某一段上意外存在方块实体，
        // 就会把非 BOTTOM 的坐标当成红绿灯写进链接组。
        final BlockState clickedState = world.getBlockState(pos);
        if (clickedState.getBlock() instanceof TrafficLightsPavementIntegrationBlock
                && clickedState.hasProperty(TrafficLightsPavementIntegrationBlock.PART)) {
            final TrafficLightsPavementIntegrationBlock.TriplePart part =
                    clickedState.getValue(TrafficLightsPavementIntegrationBlock.PART);
            switch (part) {
                case BOTTOM:
                    break;
                case MIDDLE:
                    pos = pos.below();
                    break;
                case TOP:
                    pos = pos.below(2);
                    break;
            }
        }

        // ① MTR 轨道节点：选起点 / 记下一条轨道
        if (isMtrRailNode(world, pos)) {
            if (world.isClientSide) return InteractionResult.sidedSuccess(true);
            handleRailNode(player, pos);
            return InteractionResult.sidedSuccess(false);
        }

        // ② 红绿灯：客户端只做挥手反馈，状态变更一律在服务端
        if (world.isClientSide) return InteractionResult.SUCCESS;

        final BlockEntity blockEntity = world.getBlockEntity(pos);
        if (!(blockEntity instanceof TrafficLightsBlockEntity)) {
            player.displayClientMessage(Text.literal("§c这不是一个红绿灯，也不是 MTR 轨道节点！"), true);
            return InteractionResult.FAIL;
        }

        handleLight(player, pos);
        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        // Shift+右键空气：清空本次链接进度（待成组红绿灯 / 待链接轨道 / 未完成的起点）
        if (level.isClientSide) {
            return player.isShiftKeyDown()
                    ? InteractionResultHolder.sidedSuccess(stack, true)
                    : InteractionResultHolder.pass(stack);
        }

        if (player.isShiftKeyDown()) {
            final Session session = SESSIONS.get(player.getUUID());
            if (session != null) session.clearPending();
            player.displayClientMessage(Text.literal(
                    "§7已清空本次链接进度（待成组红绿灯 / 待链接轨道 / 未完成的起点）"), true);
            return InteractionResultHolder.sidedSuccess(stack, false);
        }
        return InteractionResultHolder.pass(stack);
    }

    // ------------------------------------------------------------------ 红绿灯

    private static void handleLight(Player player, BlockPos pos) {
        final Level world = player.getCommandSenderWorld();
        final Session session = sessionOf(player);
        if (!(world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity light)) return;

        if (player.isShiftKeyDown()) {
            finalizeGroup(player, world, session);
            return;
        }

        // 已经在别的组里：本魔杖只在「成组」这一刻写入轨道，没有「往已有的组追加」这条路，
        // 所以直接拒绝，免得玩家以为点了就进去了。
        if (light.isInGroup()) {
            player.displayClientMessage(Text.literal(
                    "§c该红绿灯已有相位序列！要往那一组加轨道，请重新组一次网。"), true);
            return;
        }

        // 加入 / 移出待成组列表
        if (session.lights.contains(pos)) {
            session.lights.remove(pos);
            if (session.lights.isEmpty()) {
                player.displayClientMessage(Text.literal("§c已清空链接组"), true);
            } else {
                player.displayClientMessage(Text.literal("§a已成功链接 §6§l" + session.lights.size()
                        + " §a个红绿灯 §7| §eshift+右键完成链接"), true);
            }
        } else {
            session.lights.add(pos);
            player.displayClientMessage(Text.literal("§a已成功链接 §6§l" + session.lights.size()
                    + " §a个红绿灯 §7| §eshift+右键完成链接"), true);
        }
    }

    /** Shift+右键红绿灯：把本次点过的红绿灯组成一个新组，并把本次记下的轨道一起写进去。 */
    private static void finalizeGroup(Player player, Level world, Session session) {
        if (session.lights.size() < 2) {
            player.displayClientMessage(Text.literal("§c你需要链接至少2个红绿灯！"), true);
            return;
        }

        for (BlockPos linkedPos : session.lights) {
            if (!(world.getBlockEntity(linkedPos) instanceof TrafficLightsBlockEntity tl)) {
                player.displayClientMessage(Text.literal("§c一些已链接的红绿灯不再有效！"), true);
                session.clearPending();
                return;
            }
            if (tl.isInGroup()) {
                player.displayClientMessage(Text.literal(
                        "§c红绿灯 §6" + linkedPos.toShortString() + " §c已有相位序列！"), true);
                session.clearPending();
                return;
            }
        }

        final String groupId = UUID.randomUUID().toString();
        final List<BlockPos> finalPositions = new ArrayList<>(session.lights);
        for (BlockPos linkedPos : finalPositions) {
            if (world.getBlockEntity(linkedPos) instanceof TrafficLightsBlockEntity tl) {
                tl.setGroup(groupId, finalPositions);
            }
        }

        // 红绿灯和轨道在这一次里一起落盘。
        // 注意 setGroup() 会把时间表清空，而 addRailBinding 现在**不再要求有时间表**
        // （见 TrafficLightsBlockEntity#addRailBinding 的注释），所以这里一定能写进去。
        final BlockPos anchor = finalPositions.get(0);
        int linked = 0;
        if (world.getBlockEntity(anchor) instanceof TrafficLightsBlockEntity group) {
            for (BlockPos[] rail : session.rails) {
                if (group.addRailBinding(rail[0], rail[1], player)) linked++;
            }
        }

        player.displayClientMessage(Text.literal("§a已成功链接 §6§l" + finalPositions.size()
                + " §a个红绿灯" + (linked > 0 ? "§a，并链接了 §6§l" + linked + " §a条轨道" : "")), true);
        player.displayClientMessage(Text.literal("§7请用 §e普通魔杖 §7右键任意已链接的红绿灯设置时间表，"
                + "设好之前这些轨道会保持红灯"), true);

        session.clearPending();
    }

    // ------------------------------------------------------------------ 轨道节点

    private static void handleRailNode(Player player, BlockPos pos) {
        final Session session = sessionOf(player);

        if (session.pendingStart == null) {
            session.pendingStart = pos.immutable();
            player.displayClientMessage(Text.literal("§a已选择轨道起点 §6" + pos.toShortString()
                    + "§a，请右键另一个节点作为终点"), true);
            return;
        }
        if (session.pendingStart.equals(pos)) {
            session.pendingStart = null;
            player.displayClientMessage(Text.literal("§7已取消起点选择"), true);
            return;
        }

        final BlockPos start = session.pendingStart;
        session.pendingStart = null;

        // 只记下来，Shift+右键红绿灯成组时和红绿灯一起写进新组
        session.rails.add(new BlockPos[]{start, pos.immutable()});
        player.displayClientMessage(Text.literal("§a已记录轨道 §6" + start.toShortString()
                + " §a→ §6" + pos.toShortString() + "§a（共 §6" + session.rails.size() + " §6条）"
                + (session.lights.isEmpty()
                        ? "§7，别忘了右键红绿灯组网"
                        : "§7，Shift+右键红绿灯成组时会一起链接")), true);
    }

    // ------------------------------------------------------------------ 工具

    /** 判断该方块是否为 MTR 的轨道节点（{@code mtr:rail}），不依赖 MTR 的类。 */
    private static boolean isMtrRailNode(Level level, BlockPos pos) {
        try {
            final ResourceLocation id = VersionServices.registries().blockId(level.getBlockState(pos).getBlock());
            return id != null && "mtr".equals(id.getNamespace()) && "rail".equals(id.getPath());
        } catch (Throwable t) {
            return false;
        }
    }
}
