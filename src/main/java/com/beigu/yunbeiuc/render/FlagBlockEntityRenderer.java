package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.pole.FlagBlock;
import com.beigu.yunbeiuc.entity.FlagBlockEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;

public class FlagBlockEntityRenderer implements BlockEntityRenderer<FlagBlockEntity> {

    public FlagBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(FlagBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String flagId = entity.getFlagId();
        if (flagId == null || flagId.isEmpty()) return;

        try {
            matrices.push();

            // 调整位置 - 在方块中心
            matrices.translate(0.5, 0.75, 0.5);

            // 旗帜尺寸
            float flagWidth = 0.6f;
            float flagHeight = flagWidth * 2.67f;

            // 纹理路径
            Identifier texture = new Identifier("yunbeiuc", "textures/block/flags/" + flagId + ".png");

            // 根据方块朝向渲染两个相对的面
            Direction facing = entity.getCachedState().get(FlagBlock.FACING);
            renderTwoFaces(matrices, vertexConsumers, texture, flagWidth, flagHeight, facing, light, overlay);

            matrices.pop();

        } catch (Exception e) {
            System.err.println("Error rendering flag: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderTwoFaces(MatrixStack matrices, VertexConsumerProvider vertexConsumers, Identifier texture,
                                float width, float height, Direction facing, int light, int overlay) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        MatrixStack.Entry entry = matrices.peek();

        float halfWidth = width / 2;
        float halfHeight = height / 2;

        // 渲染正面（方块朝向的面）
        renderFace(entry, vertexConsumer, facing, -halfWidth, -halfHeight, halfWidth, halfHeight, 0.505f, light, overlay);

        // 渲染背面（与正面相对的面）
        Direction backFace = facing.getOpposite();
        renderFace(entry, vertexConsumer, backFace, -halfWidth, -halfHeight, halfWidth, halfHeight, 0.505f, light, overlay);
    }

    private void renderFace(MatrixStack.Entry entry, VertexConsumer consumer, Direction face,
                            float minX, float minY, float maxX, float maxY, float offset, int light, int overlay) {
        switch (face) {
            case NORTH: // 朝向北方 (Z-)
                renderNorthFace(entry, consumer, minX, minY, maxX, maxY, -offset, light, overlay);
                break;
            case SOUTH: // 朝向南方 (Z+)
                renderSouthFace(entry, consumer, minX, minY, maxX, maxY, offset, light, overlay);
                break;
            case WEST: // 朝向西方 (X-)
                renderWestFace(entry, consumer, minX, minY, maxX, maxY, -offset, light, overlay);
                break;
            case EAST: // 朝向东方 (X+)
                renderEastFace(entry, consumer, minX, minY, maxX, maxY, offset, light, overlay);
                break;
        }
    }

    private void renderNorthFace(MatrixStack.Entry entry, VertexConsumer consumer,
                                 float minX, float minY, float maxX, float maxY, float z, int light, int overlay) {
        // 北面 (Z-)
        consumer.vertex(entry.getPositionMatrix(), minX, minY, z)
                .color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, -1).next();
        consumer.vertex(entry.getPositionMatrix(), maxX, minY, z)
                .color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, -1).next();
        consumer.vertex(entry.getPositionMatrix(), maxX, maxY, z)
                .color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, -1).next();
        consumer.vertex(entry.getPositionMatrix(), minX, maxY, z)
                .color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, -1).next();
    }

    private void renderSouthFace(MatrixStack.Entry entry, VertexConsumer consumer,
                                 float minX, float minY, float maxX, float maxY, float z, int light, int overlay) {
        // 南面 (Z+)
        consumer.vertex(entry.getPositionMatrix(), minX, minY, z)
                .color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, 1).next();
        consumer.vertex(entry.getPositionMatrix(), maxX, minY, z)
                .color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, 1).next();
        consumer.vertex(entry.getPositionMatrix(), maxX, maxY, z)
                .color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, 1).next();
        consumer.vertex(entry.getPositionMatrix(), minX, maxY, z)
                .color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, 1).next();
    }

    private void renderWestFace(MatrixStack.Entry entry, VertexConsumer consumer,
                                float minX, float minY, float maxX, float maxY, float x, int light, int overlay) {
        // 西面 (X-)
        consumer.vertex(entry.getPositionMatrix(), x, minY, minX)
                .color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), -1, 0, 0).next();
        consumer.vertex(entry.getPositionMatrix(), x, minY, maxX)
                .color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), -1, 0, 0).next();
        consumer.vertex(entry.getPositionMatrix(), x, maxY, maxX)
                .color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), -1, 0, 0).next();
        consumer.vertex(entry.getPositionMatrix(), x, maxY, minX)
                .color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), -1, 0, 0).next();
    }

    private void renderEastFace(MatrixStack.Entry entry, VertexConsumer consumer,
                                float minX, float minY, float maxX, float maxY, float x, int light, int overlay) {
        // 东面 (X+)
        consumer.vertex(entry.getPositionMatrix(), x, minY, maxX)
                .color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 1, 0, 0).next();
        consumer.vertex(entry.getPositionMatrix(), x, minY, minX)
                .color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 1, 0, 0).next();
        consumer.vertex(entry.getPositionMatrix(), x, maxY, minX)
                .color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 1, 0, 0).next();
        consumer.vertex(entry.getPositionMatrix(), x, maxY, maxX)
                .color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 1, 0, 0).next();
    }
}