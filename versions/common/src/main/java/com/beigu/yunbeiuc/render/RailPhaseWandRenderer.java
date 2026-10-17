package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.api.text.Text;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.custom.PhaseWand;
import com.beigu.yunbeiuc.signal.ClientRailGroupIndex;
import com.beigu.yunbeiuc.signal.PhaseColors;
import com.beigu.yunbeiuc.signal.RailPhaseBinding;
import com.beigu.yunbeiuc.signal.mtr.RailRaycast;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

/**
 * 手持「魔杖」（{@code yunbeiuc:wand}）时，把**周围所有相位组里已链接的 MTR 轨道**
 * 画成高亮中心线，并在每条轨道中点上方浮一个「相位 x」的标签。
 *
 * <p>「周围有哪些组」**不来自物品**——魔杖上不记任何「我在哪一组」（用户要求）。
 * 相位组索引由 {@link ClientRailGroupIndex} 维护：客户端方块实体同步到数据时登记锚点，
 * 读取时顺手清掉失效项，所以换世界 / 重进存档都不会像以前那样丢状态。
 * 每组轨道用自己的组算颜色，多个组混在一起也不会串色。
 *
 * <p>红绿灯上方的悬浮字**不在这里画**——那是
 * {@code TrafficLightsBlockEntityRenderer} 的活（手持同一把魔杖时它已经在每盏灯上方
 * 画「方向 + 相位 x / n」）。这里再画一份就是重复的两行字。
 *
 * <h3>不再需要瞄准</h3>
 * 旧实现靠玩家准心做射线检测，一次只能看到一条轨道。现在改成直接遍历每个相位组的
 * {@code getRailBindings()}：每条绑定里就有两端节点坐标，用
 * {@link RailRaycast#railBetween} 从 {@code mtr.client.ClientData.RAILS} 直接取几何对象，
 * 于是**所有组轨道一次性全部显示**，玩家不用瞄也知道哪条轨道归哪个相位管。
 *
 * <h3>颜色 = 这条轨道此刻的红绿灯状态</h3>
 * 见 {@link PhaseColors}：绿=放行，黄=黄灯段即将变红，红=不放行，灰白=判断不了。
 * 状态由 {@code TrafficLightsBlockEntity#getRailLightState} 按灯同一套逻辑算出，
 * 且相位是 {@code cycleStartGameTime} + 本地游戏刻现算的，所以**整条链路不需要额外网络包**。
 * 悬浮标签用的是同一个颜色，线色与字色永远一致。
 *
 * <h3>画法：照抄 MTR 的 {@code mtr.client.IDrawing#drawLine}</h3>
 * <pre>
 * final VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.lines());
 * vertexConsumer.vertex(pose.pose(), x, y, z).color(r, g, b, 0xFF)
 *              .normal(pose.normal(), dx, dy, dz).endVertex();
 * </pre>
 * 用原版 {@link RenderType#lines()}，着色器、顶点格式（{@code POSITION_COLOR_NORMAL}）、
 * 混合、关闭背面剔除、线宽来源全部由它自己配好。**关键是顶点必须带法线**：
 * 1.17+ 的 {@code rendertype_lines} 着色器把法线当作「线段另一端的方向」，
 * 在屏幕上把线段撑成有宽度的四边形；法线缺失时 {@code normalize(0)} 得到 NaN，
 * 整条线会静默消失（不报任何错）。这里法线给的是**这一段的走向**，
 * 于是撑开方向始终垂直于线段本身，任何角度、任何距离线宽都一样。
 *
 * <h3>线宽：交给 {@code RenderType.lines()}，别自己调</h3>
 * {@code RenderType.LINES} 自带一个 {@code LineStateShard(OptionalDouble.empty())}，
 * 它在 {@code endBatch} 的 {@code setupRenderState()} 阶段把 {@code RenderSystem.lineWidth}
 * 覆盖成 {@code max(2.5 * 窗口宽度 / 1920, 2.5)}（跟着窗口缩放）。
 * 所以在 {@code endBatch} 之前调 {@link RenderSystem#lineWidth(float)} 是**无效**的——
 * 线宽在着色器里是**全局 uniform**，谁最后写谁生效。旧实现把中心线与节点十字标分成两个批次、
 * 各调了一次不同线宽，于是同一帧里两段线粗细不一样。现在全帧只剩**一个批次、一次线宽**。
 *
 * <h3>悬浮文本</h3>
 * 每条轨道中点上方浮一行 {@code 相位 1,3}——这条轨道被哪些相位放行。位置用
 * {@link RenderPlatform#drawInBatch} 画、方向用 {@link RenderPlatform#rotateY}
 * 绕 Y 轴转到正对玩家（公告板）。
 * 用 Y 轴公告板而不是完整相机朝向，是为了**七个版本都能编译**：
 * 1.16.5 的 {@code Camera.rotation} 是**字段**、返回 {@code com.mojang.math.Quaternion}，
 * 1.19.4+ 才是方法、返回 {@code org.joml.Quaternionf}；而
 * {@code Camera.getYRot() -> float} 七个版本完全一致，且 {@code rotateY} 已有版本实现。
 *
 * <h3>永远画在最上层：不能被遮挡</h3>
 * 高亮线是「瞄准辅助」，一旦被挡住玩家就不知道哪条轨道归哪个相位。而本阶段
 * （{@code AFTER_TRANSLUCENT_BLOCKS}）之前已经画完并写好深度的东西很多：
 * 地形、MTR 的轨道带与列车（MTR 的 {@code RenderTrains} 是 {@code EntitySeat} 的
 * **实体渲染器**，走实体阶段）、以及红绿灯 / PIDS / 信号机 / 路牌这些方块实体。
 * 所以这里直接 {@link RenderSystem#disableDepthTest()}，与 {@link LinkWandRenderer} 一致。
 *
 * <p>两处细节保证这个改法是安全的：
 * <ul>
 *   <li>{@code RenderType.LINES} 与 {@code RenderType.text} 的 {@code CompositeState} 里
 *       **都没有 {@code DepthTestStateShard}**（已用 javap 逐条核对 1.20.1 的 {@code RenderType}：
 *       {@code text} 工厂只设了 shader / texture / transparency / lightmap，
 *       只有 {@code textSeeThrough} 才显式设 {@code DepthTestStateShard("always")}），
 *       所以 {@code endBatch} 既不会覆盖也不会替我们还原 —— 必须自己 {@code enableDepthTest()}。</li>
 *   <li>GL 在深度测试关闭时**不写深度**，所以画在最上层也不会挡住后面画的粒子 / 雨雪。</li>
 * </ul>
 * 另外 {@code RenderType.text} 没有显式的 {@code CullStateShard}，而
 * {@code RenderType.lines()} 的 clear 会把剔除状态还原成「开」，所以画字之前要
 * {@link RenderSystem#disableCull()}，否则朝向算反时整行字会被背面剔除掉（同样不报错）。
 *
 * <p>挂载点见各 loader 的客户端入口（与 {@link LinkWandRenderer#renderLinkedLightsOutline}
 * 同一个世界渲染阶段）。未安装 MTR 时 {@link RailRaycast} 全部返回 {@code null} / 空表，
 * 本类安静地什么都不画。
 */
public final class RailPhaseWandRenderer {

    /** 只画这个距离内的轨道（方块）。相位组可能有几十条轨道，远处的没必要占资源。 */
    private static final double MAX_DISTANCE = 128.0;
    /**
     * 高亮线的高度偏移。
     *
     * <p>MTR 把轨道带画在 {@code y + 0.0625}，它自己的覆盖层再加 {@code SMALL_OFFSET}（≈0.003）。
     * 这里取 0.0825，比轨道带高 0.02 格，既不闪烁也仍然像贴在轨道上。
     */
    private static final double Y_OFFSET = 0.0825;
    /**
     * 顶点色里的 alpha。用 1.0（不透明），与 MTR 的 {@code .color(r, g, b, 0xFF)} 一致。
     *
     * <p>这里**不能**用半透明：折线是按一段一段写进同一个 {@code LINES} 批次的，
     * 相邻两段在拐点处会有一小块重叠，半透明混合会把它叠得更实、看起来「这一段粗一点」。
     */
    private static final float ALPHA = 1.0f;

    /** 悬浮标签相对轨道面的高度（方块）。 */
    private static final double LABEL_Y_OFFSET = 0.55;
    /** 悬浮标签的字号，与原名牌一致（0.025 ⇒ 1 像素 = 1/40 格）。 */
    private static final float LABEL_SCALE = 0.025f;
    /** 一帧最多画多少个轨道标签，防止相位组特别大时刷屏 / 掉帧。 */
    private static final int MAX_LABELS = 48;

    /** 轨道几何缓存时长。中心线采样不便宜，没必要每帧重算。 */
    private static final long GEOMETRY_CACHE_MS = 500L;

    /**
     * 缓存的一条轨道：两端节点 + 采样好的中心线 + 放行相位文本 + **它所属的相位组**。
     *
     * <p>连组一起存是因为一帧可能同时画好几个组：颜色与标签都必须按「这条轨道自己的组」
     * 来算，不能像以前那样共用一个组。
     */
    private static final class CachedRail {
        final TrafficLightsBlockEntity group;
        final BlockPos start;
        final BlockPos end;
        final List<Vec3> points;
        final String phaseLabel;

        CachedRail(TrafficLightsBlockEntity group, BlockPos start, BlockPos end,
                   List<Vec3> points, String phaseLabel) {
            this.group = group;
            this.start = start;
            this.end = end;
            this.points = points;
            this.phaseLabel = phaseLabel;
        }
    }

    /** 上一次构建缓存时的「组集合指纹」；变了就重算。 */
    private static long cachedSignature = Long.MIN_VALUE;
    private static long cachedAt;
    private static final List<CachedRail> cachedRails = new ArrayList<>();

    private RailPhaseWandRenderer() {
    }

    /**
     * 在世界渲染阶段调用（各 loader 的客户端入口，与
     * {@link LinkWandRenderer#renderLinkedLightsOutline} 同一个挂载点）。
     */
    public static void renderAimedRail(PoseStack matrices, Camera camera) {
        if (matrices == null || camera == null) return;

        final Minecraft client = Minecraft.getInstance();
        final Player player = client.player;
        if (player == null || client.level == null) return;

        // 手持魔杖才显示（主手或副手）
        final ItemStack wand = PhaseWand.findHeldWand(player);
        if (wand == null) {
            invalidate();
            return;
        }

        // 周围有哪些相位组：从世界里的方块实体索引拿，物品上不留任何记录
        final List<BlockPos> anchors = ClientRailGroupIndex.validAnchors(client.level);
        if (anchors.isEmpty()) {
            invalidate();
            return;
        }

        final List<CachedRail> rails = geometryFor(client.level, anchors);
        if (rails.isEmpty()) return;

        final Vec3 cameraPos = camera.getPosition();

        matrices.pushPose();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        // 高亮线是「瞄准辅助」，不能被任何东西挡住：地形、MTR 的列车与轨道带
        // （在实体渲染阶段画）、以及红绿灯 / PIDS / 信号机 / 路牌这些方块实体
        // （在方块实体阶段画，都在本阶段之前），全都不行。
        // 所以关掉深度测试，永远画在最上层——与 LinkWandRenderer 画连接方框的做法一致。
        // RenderType.LINES / RenderType.text 的状态里**都没有** DepthTestStateShard，
        // 所以这个 GL 状态不会被它们覆盖；而深度测试关闭时 GL 不写深度，
        // 所以也不会挡住后面画的粒子 / 雨雪。
        RenderSystem.disableDepthTest();
        // RenderType.lines() 的 clear 会把剔除还原成「开」，而 RenderType.text 没有剔除状态，
        // 所以画字之前自己关掉，免得朝向算反时整行字被背面剔除（静默消失）。
        RenderSystem.disableCull();

        final MultiBufferSource.BufferSource buffers = client.renderBuffers().bufferSource();

        // ---------- 1) 高亮中心线：全帧只有一个批次，且完全不碰线宽 ----------
        final List<CachedRail> visible = new ArrayList<>(rails.size());
        if (!rails.isEmpty()) {
            final VertexConsumer lineBuffer = buffers.getBuffer(RenderType.lines());
            final PoseStack.Pose pose = matrices.last();

            for (CachedRail rail : rails) {
                if (nearestDistanceSqr(rail.points, cameraPos) > MAX_DISTANCE * MAX_DISTANCE) continue;
                visible.add(rail);

                final int argb = stateColor(rail.group, rail.start, rail.end);
                final float red = PhaseColors.red(argb);
                final float green = PhaseColors.green(argb);
                final float blue = PhaseColors.blue(argb);

                Vec3 previous = rail.points.get(0);
                for (int i = 1; i < rail.points.size(); i++) {
                    final Vec3 current = rail.points.get(i);
                    final float dx = (float) (current.x - previous.x);
                    final float dy = (float) (current.y - previous.y);
                    final float dz = (float) (current.z - previous.z);
                    // 退化成一个点就跳过，免得着色器里 normalize(0) 出 NaN
                    if (dx != 0.0f || dy != 0.0f || dz != 0.0f) {
                        lineBuffer.vertex(pose.pose(),
                                        (float) previous.x, (float) (previous.y + Y_OFFSET), (float) previous.z)
                                .color(red, green, blue, ALPHA)
                                .normal(pose.normal(), dx, dy, dz)
                                .endVertex();
                        lineBuffer.vertex(pose.pose(),
                                        (float) current.x, (float) (current.y + Y_OFFSET), (float) current.z)
                                .color(red, green, blue, ALPHA)
                                .normal(pose.normal(), dx, dy, dz)
                                .endVertex();
                    }
                    previous = current;
                }
            }
            buffers.endBatch(RenderType.lines());
        }

        // ---------- 2) 每条轨道中点上方浮一行「相位 x」 ----------
        // 内容 = 这条轨道被哪些相位放行，颜色 = 它此刻的红绿灯状态，
        // 所以世界里看到的颜色永远和灯本身同步。
        if (!visible.isEmpty()) {
            final Font font = client.font;
            final RenderPlatform render = VersionServices.render();
            // 相机偏航角：绕 Y 轴转 -yaw 再把文字翻 180°，就是原版名牌的朝向做法。
            // 只绕 Y 轴（不跟俯仰角）→ 玩家平视时永远是正的，七个版本也都能编译。
            final float billboardYaw = 180.0f - camera.getYRot();

            int railBudget = MAX_LABELS;
            for (CachedRail rail : visible) {
                if (railBudget-- <= 0) break;
                final Vec3 mid = rail.points.get(rail.points.size() / 2);
                final int argb = stateColor(rail.group, rail.start, rail.end);
                drawLabel(matrices, render, font, buffers, billboardYaw,
                        mid.x, mid.y + Y_OFFSET + LABEL_Y_OFFSET, mid.z,
                        Text.translatable("text.yunbeiuc.rail_phase.rail_label", rail.phaseLabel), argb);
            }

            // 文本用的 RenderType 由 Font 内部决定，这里统一把挂起的批次冲掉
            buffers.endBatch();
        }

        // 恢复 GL 状态（这两个 RenderType 都不管它们，必须自己还原）
        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();

        matrices.popPose();
    }

    /** 在世界坐标 {@code (x, y, z)} 处画一行始终朝向玩家的文字（公告板）。 */
    private static void drawLabel(PoseStack matrices, RenderPlatform render, Font font,
                                  MultiBufferSource buffers, float billboardYaw,
                                  double x, double y, double z, Component label, int argb) {
        matrices.pushPose();
        matrices.translate(x, y, z);
        render.rotateY(matrices, billboardYaw);
        matrices.scale(-LABEL_SCALE, -LABEL_SCALE, LABEL_SCALE);
        final float halfWidth = -font.width(label.getString()) / 2.0f;
        // 满亮度（0xF000F0）→ 夜里 / 隧道里也看得清
        render.drawInBatch(font, label, halfWidth, 0.0f, argb, true,
                matrices, buffers, 0, 0xF000F0);
        matrices.popPose();
    }

    // ------------------------------------------------------------------ 几何缓存

    /**
     * 取周围所有相位组的轨道几何，带短缓存。
     *
     * <p>缓存指纹 = 「组锚点 + 每条轨道的两端节点」，任一变化就重算（玩家链接轨道时必然变），
     * 另外加 500ms 时效兜底；相位掩码变化不影响几何，所以不用管。
     */
    private static List<CachedRail> geometryFor(Level level, List<BlockPos> anchors) {
        final List<TrafficLightsBlockEntity> groups = new ArrayList<>(anchors.size());
        long signature = 17L;
        for (BlockPos anchor : anchors) {
            if (!(level.getBlockEntity(anchor) instanceof TrafficLightsBlockEntity group)
                    || !group.isInGroup()) {
                continue;
            }
            final List<RailPhaseBinding> bindings = group.getRailBindings();
            if (bindings.isEmpty()) continue;
            groups.add(group);
            signature = signature * 31L + anchor.asLong();
            for (RailPhaseBinding binding : bindings) {
                signature = signature * 31L + binding.getStart().asLong();
                signature = signature * 31L + binding.getEnd().asLong();
            }
        }
        if (groups.isEmpty()) {
            invalidate();
            return cachedRails;
        }

        final long now = System.currentTimeMillis();
        if (signature == cachedSignature && now - cachedAt < GEOMETRY_CACHE_MS) {
            return cachedRails;
        }

        cachedRails.clear();
        cachedSignature = signature;
        cachedAt = now;

        for (TrafficLightsBlockEntity group : groups) {
            for (RailPhaseBinding binding : group.getRailBindings()) {
                final Object rail = RailRaycast.railBetween(binding.getStart(), binding.getEnd());
                if (rail == null) continue;
                final List<Vec3> points = RailRaycast.centerline(rail);
                if (points.size() < 2) continue;
                cachedRails.add(new CachedRail(group, binding.getStart(), binding.getEnd(), points,
                        phaseLabelOf(binding)));
            }
        }
        return cachedRails;
    }

    private static void invalidate() {
        cachedSignature = Long.MIN_VALUE;
        cachedRails.clear();
    }

    /** 采样点里离相机最近的距离平方，用来做距离裁剪。 */
    private static double nearestDistanceSqr(List<Vec3> points, Vec3 cameraPos) {
        double best = Double.MAX_VALUE;
        for (Vec3 point : points) {
            final double d = point.distanceToSqr(cameraPos);
            if (d < best) best = d;
        }
        return best;
    }

    // ------------------------------------------------------------------ 文本

    /**
     * 这条轨道放行哪些相位，例如 {@code "1,3"}；一个都没有时是 {@code "-"}。
     *
     * <p>就是 {@code RailPhaseBinding} 位掩码里置位的那些位（1 起数），
     * 与界面上勾选的内容一致。
     */
    private static String phaseLabelOf(RailPhaseBinding binding) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < RailPhaseBinding.MAX_PHASES; i++) {
            if (!binding.isPhaseEnabled(i)) continue;
            if (sb.length() > 0) sb.append(',');
            sb.append(i + 1);
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    // ------------------------------------------------------------------ 颜色

    /**
     * 这条轨道此刻该用什么颜色，也就是它的「红绿灯状态」。
     *
     * <p>状态数据全部来自**客户端**那份 {@code TrafficLightsBlockEntity}：
     * 轨道相位掩码随方块实体更新包同步（{@code getUpdateTag} → {@code saveAdditionalCompat}），
     * 活动相位由 {@code cycleStartGameTime} + 本地游戏刻推算，所以不需要额外的同步包。
     * 判断不了就退回中性灰白。
     */
    private static int stateColor(TrafficLightsBlockEntity group, BlockPos start, BlockPos end) {
        final RailPhaseBinding binding = group.findRailBinding(start, end);
        if (binding == null) return PhaseColors.UNKNOWN;

        switch (group.getRailLightState(binding)) {
            case GREEN:
            case GRAY:   // 绿闪：仍然算放行
                return PhaseColors.RELEASED;
            case YELLOW:
                return PhaseColors.WARNING;
            case RED:
            default:
                return PhaseColors.HELD;
        }
    }
}
