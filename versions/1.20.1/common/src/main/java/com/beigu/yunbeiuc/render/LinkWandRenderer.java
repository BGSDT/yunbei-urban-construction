package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.item.custom.LinkWand;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShape;

import java.util.List;
import java.util.UUID;

public class LinkWandRenderer {

    public static void renderLinkedLightsOutline(MatrixStack matrices, Camera camera) {
        MinecraftClient client = MinecraftClient.getInstance();
        PlayerEntity player = client.player;

        if (player == null || client.world == null) return;

        ItemStack mainHand = player.getMainHandStack();
        ItemStack offHand = player.getOffHandStack();

        boolean holdingWand = mainHand.getItem() instanceof LinkWand || offHand.getItem() instanceof LinkWand;
        if (!holdingWand) return;

        List<BlockPos> linkedPositions = LinkWand.getPlayerLinkedPositions(player.getUuid());
        if (linkedPositions == null || linkedPositions.isEmpty()) return;

        Vec3d cameraPos = camera.getPos();

        matrices.push();
        matrices.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.lineWidth(4.0f);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        for (BlockPos pos : linkedPositions) {
            if (!(client.world.getBlockEntity(pos) instanceof TrafficLightsBlockEntity)) continue;

            // 描边跟随方块实际碰撞箱大小
            VoxelShape shape = client.world.getBlockState(pos).getOutlineShape(client.world, pos, ShapeContext.absent());
            Box box = shape.getBoundingBox().offset(pos).expand(0.002);

            buffer.begin(VertexFormat.DrawMode.DEBUG_LINE_STRIP, VertexFormats.POSITION_COLOR);

            float r = 0.0f;
            float g = 1.0f;
            float b = 0.0f;
            float a = 0.8f;

            // 绘制完整的碰撞箱描边
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.minX, (float) box.minY, (float) box.minZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.minX, (float) box.maxY, (float) box.minZ).color(r, g, b, a).next();

            tessellator.draw();

            buffer.begin(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);

            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.maxX, (float) box.minY, (float) box.minZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.maxX, (float) box.maxY, (float) box.minZ).color(r, g, b, a).next();

            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.maxX, (float) box.minY, (float) box.maxZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.maxX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a).next();

            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.minX, (float) box.minY, (float) box.maxZ).color(r, g, b, a).next();
            buffer.vertex(matrices.peek().getPositionMatrix(), (float) box.minX, (float) box.maxY, (float) box.maxZ).color(r, g, b, a).next();

            tessellator.draw();
        }

        RenderSystem.lineWidth(1.0f);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();

        matrices.pop();
    }
}
