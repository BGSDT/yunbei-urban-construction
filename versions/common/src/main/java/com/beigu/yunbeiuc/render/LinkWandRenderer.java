package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.custom.LinkWand;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Camera;
import net.minecraft.client.renderer.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.UUID;

public class LinkWandRenderer {

    public static void renderLinkedLightsOutline(PoseStack matrices, Camera camera) {
        Minecraft client = Minecraft.getInstance();
        Player player = client.player;

        if (player == null || client.level == null) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();

        boolean holdingWand = mainHand.getItem() instanceof LinkWand || offHand.getItem() instanceof LinkWand;
        if (!holdingWand) return;

        List<BlockPos> linkedPositions = LinkWand.getPlayerLinkedPositions(player.getUUID());
        if (linkedPositions == null || linkedPositions.isEmpty()) return;

        Vec3 cameraPos = camera.getPosition();

        matrices.pushPose();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.lineWidth(4.0f);

        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder buffer = tessellator.getBuilder();

        for (BlockPos pos : linkedPositions) {
            if (!(client.level.getBlockEntity(pos) instanceof TrafficLightsBlockEntity)) continue;

            // 描边跟随方块实际碰撞箱大小
            VoxelShape shape = client.level.getBlockState(pos).getShape(client.level, pos);
            AABB box = shape.bounds().move(pos).inflate(0.002);

            buffer.begin(VertexFormat.Mode.DEBUG_LINE_STRIP, DefaultVertexFormat.POSITION_COLOR);

            float r = 0.0f;
            float g = 1.0f;
            float b = 0.0f;
            float a = 0.8f;

            // 绘制完整的碰撞箱描边
            buffer.vertex(matrices.last().pose(), (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a).endVertex();

            tessellator.end();

            buffer.begin(VertexFormat.Mode.DEBUG_LINES, DefaultVertexFormat.POSITION_COLOR);

            buffer.vertex(matrices.last().pose(), (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a).endVertex();

            buffer.vertex(matrices.last().pose(), (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a).endVertex();

            buffer.vertex(matrices.last().pose(), (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a).endVertex();
            buffer.vertex(matrices.last().pose(), (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a).endVertex();

            tessellator.end();
        }

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        matrices.popPose();
    }
}
