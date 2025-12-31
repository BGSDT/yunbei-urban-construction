package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.TransformableBlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.RotationAxis;

public class TransformableBlockEntityRenderer implements BlockEntityRenderer<TransformableBlockEntity> {

    public TransformableBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(TransformableBlockEntity entity, float tickDelta, MatrixStack matrices,
                       VertexConsumerProvider vertexConsumers, int light, int overlay) {

        System.out.println("=== 开始渲染 TransformableBlockEntity ===");
        System.out.println("位置: " + entity.getPos());
        System.out.println("实体模式: " + entity.isEntityMode());
        System.out.println("原始方块: " + entity.getOriginalState());

        if (!entity.isEntityMode()) {
            System.out.println("跳过渲染：不是实体模式");
            return;
        }

        var originalState = entity.getOriginalState();
        if (originalState == null) {
            System.out.println("跳过渲染：原始方块状态为null");
            return;
        }

        System.out.println("开始渲染变换后的方块: " + originalState);

        matrices.push();

        try {
            // 应用变换
            matrices.translate(0.5 + entity.getPosX(), 0.5 + entity.getPosY(), 0.5 + entity.getPosZ());
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(entity.getRotX()));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(entity.getRotY()));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(entity.getRotZ()));

            float scale = entity.getScale();
            matrices.scale(scale, scale, scale);
            matrices.translate(-0.5, -0.5, -0.5);

            // 渲染变换后的方块模型
            MinecraftClient.getInstance().getBlockRenderManager().renderBlockAsEntity(
                    originalState, matrices, vertexConsumers, light, OverlayTexture.DEFAULT_UV
            );

            System.out.println("渲染完成");

        } catch (Exception e) {
            System.out.println("渲染异常: " + e.getMessage());
            e.printStackTrace();
        } finally {
            matrices.pop();
        }
    }
}