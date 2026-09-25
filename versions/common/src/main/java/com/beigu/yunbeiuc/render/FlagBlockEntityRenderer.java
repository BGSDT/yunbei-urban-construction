package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.pole.RoadPoleFlag;
import com.beigu.yunbeiuc.entity.FlagBlockEntity;
import com.beigu.yunbeiuc.util.CustomFlag;
import com.beigu.yunbeiuc.util.FlagLoader;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;

public class FlagBlockEntityRenderer implements BlockEntityRenderer<FlagBlockEntity> {

    public FlagBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(FlagBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        String flagId = entity.getFlagId();
        if (flagId == null || flagId.isEmpty()) return;

        // 从 FlagLoader 获取旗帜数据
        CustomFlag flag = FlagLoader.getFlag(flagId);
        if (flag == null) return;

        ResourceLocation texture = flag.getTexture();

        try {
            matrices.pushPose();

            // 旗帜尺寸
            float flagWidth = 0.6f;
            float flagHeight = flagWidth * 2.67f;

            Direction facing = entity.getBlockState().getValue(RoadPoleFlag.FACING);

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

            matrices.popPose();

        } catch (Exception e) {
            System.err.println("Error rendering flag: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void renderTwoFaces(PoseStack matrices, MultiBufferSource vertexConsumers, ResourceLocation texture,
                                float width, float height, Direction facing, int light, int overlay) {
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(RenderType.entityCutout(texture));
        PoseStack.Pose entry = matrices.last();

        float halfWidth = width / 2;
        float halfHeight = height / 2;

        // 渲染正面（方块朝向的面）
        renderFace(entry, vertexConsumer, facing, -halfWidth, -halfHeight, halfWidth, halfHeight, 0.0f, light, overlay);

        // 渲染背面（与正面相对的面）
        Direction backFace = facing.getOpposite();
        renderFace(entry, vertexConsumer, backFace, -halfWidth, -halfHeight, halfWidth, halfHeight, 0.0f, light, overlay);
    }

    private void renderFace(PoseStack.Pose entry, VertexConsumer consumer, Direction face,
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

    private void renderNorthFace(PoseStack.Pose entry, VertexConsumer consumer,
                                 float minX, float minY, float maxX, float maxY, float z, int light, int overlay) {
        consumer.vertex(entry.pose(), minX, minY, z)
                .color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 0, 0, -1).endVertex();
        consumer.vertex(entry.pose(), maxX, minY, z)
                .color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 0, 0, -1).endVertex();
        consumer.vertex(entry.pose(), maxX, maxY, z)
                .color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 0, 0, -1).endVertex();
        consumer.vertex(entry.pose(), minX, maxY, z)
                .color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 0, 0, -1).endVertex();
    }

    private void renderSouthFace(PoseStack.Pose entry, VertexConsumer consumer,
                                 float minX, float minY, float maxX, float maxY, float z, int light, int overlay) {
        consumer.vertex(entry.pose(), maxX, minY, z)
                .color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 0, 0, 1).endVertex();
        consumer.vertex(entry.pose(), minX, minY, z)
                .color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 0, 0, 1).endVertex();
        consumer.vertex(entry.pose(), minX, maxY, z)
                .color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 0, 0, 1).endVertex();
        consumer.vertex(entry.pose(), maxX, maxY, z)
                .color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 0, 0, 1).endVertex();
    }

    private void renderWestFace(PoseStack.Pose entry, VertexConsumer consumer,
                                float minX, float minY, float maxX, float maxY, float x, int light, int overlay) {
        consumer.vertex(entry.pose(), x, minY, minX)
                .color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), -1, 0, 0).endVertex();
        consumer.vertex(entry.pose(), x, minY, maxX)
                .color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), -1, 0, 0).endVertex();
        consumer.vertex(entry.pose(), x, maxY, maxX)
                .color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), -1, 0, 0).endVertex();
        consumer.vertex(entry.pose(), x, maxY, minX)
                .color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), -1, 0, 0).endVertex();
    }

    private void renderEastFace(PoseStack.Pose entry, VertexConsumer consumer,
                                float minX, float minY, float maxX, float maxY, float x, int light, int overlay) {
        consumer.vertex(entry.pose(), x, minY, maxX)
                .color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 1, 0, 0).endVertex();
        consumer.vertex(entry.pose(), x, minY, minX)
                .color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 1, 0, 0).endVertex();
        consumer.vertex(entry.pose(), x, maxY, minX)
                .color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 1, 0, 0).endVertex();
        consumer.vertex(entry.pose(), x, maxY, maxX)
                .color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(overlay).uv2(light)
                .normal(entry.normal(), 1, 0, 0).endVertex();
    }
}