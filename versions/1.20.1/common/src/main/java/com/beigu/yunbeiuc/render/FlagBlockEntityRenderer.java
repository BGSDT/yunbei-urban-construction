package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.pole.RoadPoleFlag;
import com.beigu.yunbeiuc.entity.FlagBlockEntity;
import com.beigu.yunbeiuc.render.json.CustomFlag;
import com.beigu.yunbeiuc.render.json.FlagLoader;
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

        // 从 FlagLoader 获取旗帜数据
        CustomFlag flag = FlagLoader.getFlag(flagId);
        if (flag == null) return;

        Identifier texture = flag.getTexture();

        try {
            matrices.push();

            // 旗帜尺寸
            float flagWidth = 0.6f;
            float flagHeight = flagWidth * 2.67f;

            Direction facing = entity.getCachedState().get(RoadPoleFlag.FACING);

            if (facing == Direction.WEST || facing == Direction.EAST) {
                matrices.translate(0.5, 0.5, -0.3);
                renderTwoFaces(matrices, vertexConsumers, texture, flagWidth, flagHeight, facing, light, overlay);

                matrices.translate(0.0, 0.0, 1.6);
                renderTwoFaces(matrices, vertexConsumers, texture, flagWidth, flagHeight, facing, light, overlay);
            } else {
                matrices.translate(-0.3, 0.5, 0.5);
                renderTwoFaces(matrices, vertexConsumers, texture, flagWidth, flagHeight, facing, light, overlay);

                matrices.translate(1.6, 0.0, 0.0);
                renderTwoFaces(matrices, vertexConsumers, texture, flagWidth, flagHeight, facing, light, overlay);
            }

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
        renderFace(entry, vertexConsumer, facing, -halfWidth, -halfHeight, halfWidth, halfHeight, 0.0f, light, overlay);

        // 渲染背面（与正面相对的面）
        Direction backFace = facing.getOpposite();
        renderFace(entry, vertexConsumer, backFace, -halfWidth, -halfHeight, halfWidth, halfHeight, 0.0f, light, overlay);
    }

    private void renderFace(MatrixStack.Entry entry, VertexConsumer consumer, Direction face,
                            float minX, float minY, float maxX, float maxY, float offset, int light, int overlay) {
        switch (face) {
            case NORTH:
                renderNorthFace(entry, consumer, minX, minY, maxX, maxY, -offset, light, overlay);
                break;
            case SOUTH:
                renderSouthFace(entry, consumer, minX, minY, maxX, maxY, offset, light, overlay);
                break;
            case WEST:
                renderWestFace(entry, consumer, minX, minY, maxX, maxY, -offset, light, overlay);
                break;
            case EAST:
                renderEastFace(entry, consumer, minX, minY, maxX, maxY, offset, light, overlay);
                break;
        }
    }

    private void renderNorthFace(MatrixStack.Entry entry, VertexConsumer consumer,
                                 float minX, float minY, float maxX, float maxY, float z, int light, int overlay) {
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
        consumer.vertex(entry.getPositionMatrix(), maxX, minY, z)
                .color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, 1).next();
        consumer.vertex(entry.getPositionMatrix(), minX, minY, z)
                .color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, 1).next();
        consumer.vertex(entry.getPositionMatrix(), minX, maxY, z)
                .color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, 1).next();
        consumer.vertex(entry.getPositionMatrix(), maxX, maxY, z)
                .color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light)
                .normal(entry.getNormalMatrix(), 0, 0, 1).next();
    }

    private void renderWestFace(MatrixStack.Entry entry, VertexConsumer consumer,
                                float minX, float minY, float maxX, float maxY, float x, int light, int overlay) {
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