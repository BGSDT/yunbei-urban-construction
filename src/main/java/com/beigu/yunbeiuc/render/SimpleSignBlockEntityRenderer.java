// SimpleSignBlockEntityRenderer.java
package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.SimpleSignBlock;
import com.beigu.yunbeiuc.entity.SimpleSignEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;

public class SimpleSignBlockEntityRenderer implements BlockEntityRenderer<SimpleSignEntity> {
    private final TextRenderer textRenderer;

    public SimpleSignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SimpleSignEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text = entity.getText();
        if (text == null || text.isEmpty()) return;

        matrices.push();
        
        // 调整文本位置和方向
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getCachedState().get(SimpleSignBlock.FACING).asRotation()));
        matrices.translate(0.0, 0.0, 0.5 + 0.0625); // 稍微突出方块表面
        
        float scale = 0.01f;
        matrices.scale(scale, -scale, scale);
        
        int color = 0x000000; // 黑色文本
        
        // 渲染文本
        this.textRenderer.draw(
            Text.literal(text),
            -this.textRenderer.getWidth(text) / 2.0f,
            -this.textRenderer.fontHeight / 2.0f,
            color,
            false,
            matrices.peek().getPositionMatrix(),
            vertexConsumers,
            TextRenderer.TextLayerType.NORMAL,
            0,
            light
        );
        
        matrices.pop();
    }
}