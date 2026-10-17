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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

/**
 * 「魔杖」（{@code yunbeiuc:wand}）在交通信号这块的角色：**查看与编辑轨道相位**。
 *
 * <p>这个物品本身早就是 mod 的「万能魔杖」——龙门架 / 道闸 / 路灯 / 路牌 / 红绿灯面板
 * 都是靠方块自己的 {@code useCompat} 判断「手上是不是这把魔杖」来触发的
 * （见 {@code ModItems.WAND} 的那一堆引用）。本类**只新增**轨道相位相关的行为，
 * 原来那些方块交互一个都不动：方块在玩家右键时先跑，只有方块返回 {@code PASS}
 * 才会走到这里的 {@link #useOn}。
 *
 * <h3>本类负责的两件事</h3>
 * <ol>
 *   <li><b>手持即显示</b>（见 {@code RailPhaseWandRenderer}）→ **周围所有相位组**的
 *       全部已链接轨道都会画出高亮线（不需要瞄准、也不需要魔杖记着「我在哪一组」），
 *       颜色是**该轨道所属组此刻的红绿灯状态**（绿=放行 / 黄=即将变红 / 红=不放行，
 *       见 {@code PhaseColors}），并在每条轨道中点上方浮一个「相位 x」标签。
 *       红绿灯上方的悬浮字由 {@code TrafficLightsBlockEntityRenderer} 在手持本魔杖时画
 *       （方向 + 相位），这里不重复画一份。</li>
 *   <li><b>瞄准轨道中段右键</b> → 打开轨道相位界面，勾选「哪些相位放行这条轨道」。
 *       仅限 <b>{@value #AIM_MAX_DISTANCE} 格以内</b>（远处只显示高亮线，不能编辑）。</li>
 * </ol>
 *
 * <p>红绿灯的右键交互一律由**方块自己**处理（打开红绿灯面板），本类不插手；
 * 本类也**没有**任何「Shift+右键绑定相位组」的操作。
 *
 * <h3>轨道链接不在这里</h3>
 * 「把一条 MTR 轨道链接进相位组」由 {@link LinkWand} 负责（右键轨道节点两次）。
 * 本类右键轨道节点只会提示一句「请用链接魔杖」。
 *
 * <h3>魔杖上**不记**「我在哪一组」</h3>
 * 相位组一律从世界里现查，物品上不留任何组信息：
 * <ul>
 *   <li>开界面：按**瞄准的那条轨道**所属的组打开（见 {@link #openRailPhaseScreen}，
 *       查服务端运行时索引 {@link RailSignalRegistry}）；</li>
 *   <li>手持高亮：按**周围所有相位组**画（见 {@code RailPhaseWandRenderer} 与
 *       {@code ClientRailGroupIndex}）。</li>
 * </ul>
 * 所以魔杖是一把**无状态**的工具：丢给别人、重开游戏、换存档都不会带错组，
 * 也不会出现「退出地图后显示白线」这种对不上的情况。
 *
 * <p>列车是否停车由 {@code RailSignalHook} 读取 {@link RailSignalRegistry} 决定，
 * 因此不安装 MTR 时本物品仍可正常使用。
 */
public class PhaseWand extends ItemCompat {

    private static final org.apache.logging.log4j.Logger LOGGER =
            com.beigu.yunbeiuc.YunbeiUrbanConstruction.LOGGER;

    /**
     * 瞄准轨道时允许的最远距离（方块）。同时也是**右键编辑**轨道的距离上限。
     *
     * <p>这个值曾经是 32，而轨道高亮线/「相位 x」标签在 128 格内都会画
     * （见 {@code RailPhaseWandRenderer#MAX_DISTANCE}）——于是「看得见」和「点得开」
     * 被拉到了两个量级上：站在 30 格外对着轨道右键空气也能弹出相位界面。
     * 现在收紧到 7 格：**远处只做显示，要编辑必须走近**。
     *
     * <p>超出距离一律**静默不动作**（不给任何提示），与「没瞄到轨道」表现一致。
     */
    private static final double AIM_MAX_DISTANCE = 7.0;

    /** 瞄准轨道时允许的横向偏差（方块）。MTR 画出的轨道带宽是 ±1，略放宽一点手感更好。 */
    private static final double AIM_THRESHOLD = 1.25;

    /** 客户端首次确认「手持魔杖 → MTR 轨道高亮」时打一条日志，用于自检。 */
    private static boolean highlightLogged;

    /**
     * 由客户端 mixin（{@code RenderTrainsRailHighlightMixin}）在首次判定
     * 「手持本魔杖 → 开启 MTR 轨道高亮」时调用，用来在启动日志里直接确认高亮链路是否生效。
     */
    public static void noteWandHighlight() {
        if (highlightLogged) return;
        highlightLogged = true;
        LOGGER.info("[yunbeiuc] 手持魔杖：MTR 轨道着色已启用（renderColors = true）");
    }

    /** 取玩家手上（主手优先，其次副手）的魔杖；没拿则返回 {@code null}。 */
    public static ItemStack findHeldWand(Player player) {
        if (player == null) return null;
        final ItemStack main = player.getMainHandItem();
        if (main.getItem() instanceof PhaseWand) return main;
        final ItemStack off = player.getOffhandItem();
        if (off.getItem() instanceof PhaseWand) return off;
        return null;
    }

    public PhaseWand(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverTextCompat(ItemStack stack, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Text.translatable("item.yunbeiuc.wand.tooltip"));
        tooltip.add(Text.translatable("item.yunbeiuc.wand.tooltip2"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        final Level world = context.getLevel();
        final BlockPos pos = context.getClickedPos();
        final Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        // 潜行时本物品不做任何事，右键完全让给方块自己。
        // （本类没有 Shift+右键绑定相位组这种操作。）
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        // 客户端：红绿灯方块自己会打开红绿灯面板（方块先于物品执行），能走到这里
        // 说明玩家是在瞄准轨道 → 把瞄准结果上报给服务端。
        if (world.isClientSide) {
            final BlockEntity blockEntity = world.getBlockEntity(pos);
            if (!(blockEntity instanceof TrafficLightsBlockEntity) && !isMtrRailNode(world, pos)) {
                sendAim(player);
            }
            return InteractionResult.sidedSuccess(true);
        }

        if (!(player instanceof ServerPlayer)) return InteractionResult.PASS;

        // 1) 红绿灯：面板由方块自己打开，本物品不插手
        if (world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity) {
            return InteractionResult.sidedSuccess(false);
        }

        // 2) MTR 轨道节点：链接轨道归链接魔杖，这里只提示一句
        if (isMtrRailNode(world, pos)) {
            message(player, "§7链接轨道请用 §e链接魔杖 §7：右键两个轨道节点即可把这条轨道加入本次正在链接的相位组");
            return InteractionResult.sidedSuccess(false);
        }

        // 3) 瞄到的是普通方块：界面由客户端发来的 RailPhaseAimPacket 驱动，
        //    服务端这里不再重复算一遍瞄准。
        return InteractionResult.sidedSuccess(false);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        final ItemStack stack = player.getItemInHand(hand);

        // 潜行时不做事（本物品没有 Shift+右键功能）
        if (player.isShiftKeyDown()) return InteractionResultHolder.pass(stack);

        // 客户端：没瞄到轨道就完全不动（返回 pass，手臂不挥），
        // 免得这把「万能魔杖」在普通右键空气时凭空多出一个动作。
        if (level.isClientSide) {
            return sendAim(player)
                    ? InteractionResultHolder.sidedSuccess(stack, true)
                    : InteractionResultHolder.pass(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    // ------------------------------------------------------------------ 瞄准

    /**
     * 客户端专用：按玩家视线找一条轨道，把它的两个节点坐标发给服务端。
     * 未装 MTR / 没瞄到轨道 / 轨道在 {@link #AIM_MAX_DISTANCE} 之外时静默返回
     * （玩家会看到准心前方没有高亮线，或高亮线太远而右键没反应）。
     *
     * @return 是否真的瞄到并上报了一条轨道
     */
    private static boolean sendAim(Player player) {
        final RailRaycast.Hit hit = RailRaycast.raycastClient(player.getEyePosition(),
                player.getLookAngle(), AIM_MAX_DISTANCE, AIM_THRESHOLD);
        if (hit == null) return false;

        final FriendlyByteBuf buf = NetworkCompat.newBuffer();
        new RailPhaseAimPacket(hit.start, hit.end).write(buf);
        NetworkCompat.sendToServer(ModMessages.AIM_RAIL_PHASE, buf);
        return true;
    }

    /**
     * 服务端：查出「这条轨道属于哪个相位组」，然后让客户端打开相位界面。
     *
     * <p>由 {@link RailPhaseAimPacket#apply} 调用，因此所有失败分支都会给玩家一句明确提示，
     * 而不是静默什么都不发生。
     *
     * <p>组**不来自魔杖**（物品上不记任何组信息），只查服务端运行时索引
     * {@link RailSignalRegistry}：每条轨道每 tick 都由它所属的相位组写进去，所以
     * 「这条轨道属于哪一组」一定查得到；查不到就说明这条轨道根本没链接过。
     */
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
