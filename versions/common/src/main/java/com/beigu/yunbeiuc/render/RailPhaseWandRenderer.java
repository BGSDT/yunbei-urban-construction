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
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

// 手持「轨道魔杖」（普通魔杖或链接魔杖）时画周围所有已组轨道的高亮线与「相位 x」标签（客户端）。
// 组信息从 ClientRailGroupIndex 现查，不写物品 NBT；显示距离 128 格、几何缓存 500ms。
// 红绿灯上方的悬浮字不在这里画，由 TrafficLightsBlockEntityRenderer 负责。

public final class RailPhaseWandRenderer {

    private static final double MAX_DISTANCE = 128.0;
    private static final double Y_OFFSET = 0.0825;
    private static final float ALPHA = 1.0f;

    private static final double LABEL_Y_OFFSET = 0.55;
    private static final float LABEL_SCALE = 0.025f;
    private static final int MAX_LABELS = 48;

    private static final long GEOMETRY_CACHE_MS = 500L;

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

    private static long cachedSignature = Long.MIN_VALUE;
    private static long cachedAt;
    private static final List<CachedRail> cachedRails = new ArrayList<>();

    private RailPhaseWandRenderer() {
    }

    public static void renderAimedRail(PoseStack matrices, Camera camera) {
        if (matrices == null || camera == null) return;

        final Minecraft client = Minecraft.getInstance();
        final Player player = client.player;
        if (player == null || client.level == null) {
            PhaseWand.setHoldingWand(false);
            return;
        }

        final boolean holdingRailWand = PhaseWand.isHoldingRailWand(player);
        PhaseWand.setHoldingWand(holdingRailWand);
        if (!holdingRailWand) {
            invalidate();
            return;
        }

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

        RenderSystem.disableDepthTest();
        RenderSystem.disableCull();

        final MultiBufferSource.BufferSource buffers = client.renderBuffers().bufferSource();
        final RenderPlatform render = VersionServices.render();

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
                    if (previous.x != current.x || previous.y != current.y || previous.z != current.z) {
                        render.line(lineBuffer, pose,
                                (float) previous.x, (float) (previous.y + Y_OFFSET), (float) previous.z,
                                (float) current.x, (float) (current.y + Y_OFFSET), (float) current.z,
                                red, green, blue, ALPHA);
                    }
                    previous = current;
                }
            }
            buffers.endBatch(RenderType.lines());
        }

        if (!visible.isEmpty()) {
            final Font font = client.font;
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

            buffers.endBatch();
        }

        RenderSystem.enableCull();
        RenderSystem.enableDepthTest();

        matrices.popPose();
    }

    private static void drawLabel(PoseStack matrices, RenderPlatform render, Font font,
                                  MultiBufferSource buffers, float billboardYaw,
                                  double x, double y, double z, Component label, int argb) {
        matrices.pushPose();
        matrices.translate(x, y, z);
        render.rotateY(matrices, billboardYaw);
        matrices.scale(-LABEL_SCALE, -LABEL_SCALE, LABEL_SCALE);
        final float halfWidth = -font.width(label.getString()) / 2.0f;
        render.drawInBatch(font, label, halfWidth, 0.0f, argb, true,
                matrices, buffers, 0, 0xF000F0);
        matrices.popPose();
    }

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

    private static double nearestDistanceSqr(List<Vec3> points, Vec3 cameraPos) {
        double best = Double.MAX_VALUE;
        for (Vec3 point : points) {
            final double d = point.distanceToSqr(cameraPos);
            if (d < best) best = d;
        }
        return best;
    }

    private static String phaseLabelOf(RailPhaseBinding binding) {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < RailPhaseBinding.MAX_PHASES; i++) {
            if (!binding.isPhaseEnabled(i)) continue;
            if (sb.length() > 0) sb.append(',');
            sb.append(i + 1);
        }
        return sb.length() == 0 ? "-" : sb.toString();
    }

    private static int stateColor(TrafficLightsBlockEntity group, BlockPos start, BlockPos end) {
        final RailPhaseBinding binding = group.findRailBinding(start, end);
        if (binding == null) return PhaseColors.UNKNOWN;

        switch (group.getRailLightState(binding)) {
            case GREEN:
            case GRAY:
                return PhaseColors.RELEASED;
            case YELLOW:
                return PhaseColors.WARNING;
            case RED:
            default:
                return PhaseColors.HELD;
        }
    }
}
