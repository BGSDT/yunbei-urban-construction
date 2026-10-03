package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.api.gui.RenderPlatform;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.custom.LinkWand;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexConsumer;
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
        RenderSystem.lineWidth(4.0f);

        RenderPlatform render = VersionServices.render();

        for (BlockPos pos : linkedPositions) {
            if (!(client.level.getBlockEntity(pos) instanceof TrafficLightsBlockEntity)) continue;

            // 描边跟随方块实际碰撞箱大小
            VoxelShape shape = client.level.getBlockState(pos).getShape(client.level, pos);
            AABB box = shape.bounds().move(pos).inflate(0.002);

            VertexConsumer lineBuffer = render.beginLines(RenderPlatform.LinePrimitive.LINE_STRIP);

            float r = 0.0f;
            float g = 1.0f;
            float b = 0.0f;
            float a = 0.8f;

            // 绘制完整的碰撞箱描边
            render.vertex(lineBuffer, matrices.last(), (float) box.minX, (float) box.minY, (float) box.minZ, r, g, b, a);
            render.vertex(lineBuffer, matrices.last(), (float) box.maxX, (float) box.minY, (float) box.minZ, r, g, b, a);
            render.vertex(lineBuffer, matrices.last(), (float) box.maxX, (float) box.minY, (float) box.maxZ, r, g, b, a);
            render.vertex(lineBuffer, matrices.last(), (float) box.minX, (float) box.minY, (float) box.maxZ, r, g, b, a);
            render.vertex(lineBuffer, matrices.last(), (float) box.minX, (float) box.minY, (float) box.minZ, r, g, b, a);
            render.vertex(lineBuffer, matrices.last(), (float) box.minX, (float) box.maxY, (float) box.minZ, r, g, b, a);
            render.vertex(lineBuffer, matrices.last(), (float) box.maxX, (float) box.maxY, (float) box.minZ, r, g, b, a);
            render.vertex(lineBuffer, matrices.last(), (float) box.maxX, (float) box.maxY, (float) box.maxZ, r, g, b, a);
            render.vertex(lineBuffer, matrices.last(), (float) box.minX, (float) box.maxY, (float) box.maxZ, r, g, b, a);
            render.vertex(lineBuffer, matrices.last(), (float) box.minX, (float) box.maxY, (float) box.minZ, r, g, b, a);

            render.endLines();

            VertexConsumer linesBuffer = render.beginLines(RenderPlatform.LinePrimitive.LINES);

            render.vertex(linesBuffer, matrices.last(), (float) box.maxX, (float) box.minY, (float) box.minZ, r, g, b, a);
            render.vertex(linesBuffer, matrices.last(), (float) box.maxX, (float) box.maxY, (float) box.minZ, r, g, b, a);

            render.vertex(linesBuffer, matrices.last(), (float) box.maxX, (float) box.minY, (float) box.maxZ, r, g, b, a);
            render.vertex(linesBuffer, matrices.last(), (float) box.maxX, (float) box.maxY, (float) box.maxZ, r, g, b, a);

            render.vertex(linesBuffer, matrices.last(), (float) box.minX, (float) box.minY, (float) box.maxZ, r, g, b, a);
            render.vertex(linesBuffer, matrices.last(), (float) box.minX, (float) box.maxY, (float) box.maxZ, r, g, b, a);

            render.endLines();
        }

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        matrices.popPose();
    }
}
