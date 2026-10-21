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

// 链接魔杖：组网 + 轨道链接一次做完 —— 右键红绿灯组网、右键两个轨道节点记轨道、Shift+右键成组。
// 只在运行期 Session 里记进度，不写物品 NBT；Shift+右键空气清进度。

public class LinkWand extends ItemCompat {

    private static final Map<UUID, Session> SESSIONS = new HashMap<>();

    private static final class Session {
        final List<BlockPos> lights = new ArrayList<>();
        final List<BlockPos[]> rails = new ArrayList<>();
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

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Level world = context.getLevel();
        BlockPos pos = context.getClickedPos();
        final Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

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

        if (isMtrRailNode(world, pos)) {
            if (world.isClientSide) return InteractionResult.sidedSuccess(true);
            handleRailNode(player, pos);
            return InteractionResult.sidedSuccess(false);
        }

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

    private static void handleLight(Player player, BlockPos pos) {
        final Level world = player.getCommandSenderWorld();
        final Session session = sessionOf(player);
        if (!(world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity light)) return;

        if (player.isShiftKeyDown()) {
            finalizeGroup(player, world, session);
            return;
        }

        if (light.isInGroup()) {
            player.displayClientMessage(Text.literal(
                    "§c该红绿灯已有相位序列！要往那一组加轨道，请重新组一次网。"), true);
            return;
        }

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

        session.rails.add(new BlockPos[]{start, pos.immutable()});
        player.displayClientMessage(Text.literal("§a已记录轨道 §6" + start.toShortString()
                + " §a→ §6" + pos.toShortString() + "§a（共 §6" + session.rails.size() + " §6条）"
                + (session.lights.isEmpty()
                        ? "§7，别忘了右键红绿灯组网"
                        : "§7，Shift+右键红绿灯成组时会一起链接")), true);
    }

    private static boolean isMtrRailNode(Level level, BlockPos pos) {
        try {
            final ResourceLocation id = VersionServices.registries().blockId(level.getBlockState(pos).getBlock());
            return id != null && "mtr".equals(id.getNamespace()) && "rail".equals(id.getPath());
        } catch (Throwable t) {
            return false;
        }
    }
}
