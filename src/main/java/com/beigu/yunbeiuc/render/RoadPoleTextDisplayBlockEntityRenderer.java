package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.RoadPolesTextDisplay;
import com.beigu.yunbeiuc.entity.RoadPolesTextDisplayEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;

public class RoadPoleTextDisplayBlockEntityRenderer implements BlockEntityRenderer<RoadPolesTextDisplayEntity> {
    private final TextRenderer textRenderer;

    public RoadPoleTextDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(RoadPolesTextDisplayEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text = entity.getText();
        if (text == null || text.isEmpty()) return;

        matrices.push();
        
        // 调整位置到方块中心
        matrices.translate(0.5, 1.2, 0.5);
        
        // 根据方块朝向旋转
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-entity.getCachedState().get(RoadPolesTextDisplay.FACING).asRotation()));
        
        // 移动到方块前方
        matrices.translate(0.0, 0.0, 0.27);
        
        // 根据字体大小调整缩放
        float baseScale = 0.01f;
        float sizeMultiplier = entity.getFontSize() / 12.0f; // 基于默认12号字体
        float scale = baseScale * sizeMultiplier;
        matrices.scale(scale, -scale, scale);
        
        // 获取颜色分量
        int color = entity.getColor();
        
        // 计算文本宽度用于居中
        int textWidth = this.textRenderer.getWidth(text);
        
        // 渲染文本
        this.textRenderer.draw(
            Text.literal(text),
            -textWidth / 2.0f, // 水平居中
            -this.textRenderer.fontHeight / 2.0f, // 垂直居中
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

    @Override
    public boolean rendersOutsideBoundingBox(RoadPolesTextDisplayEntity blockEntity) {
        return true; // 确保文本在碰撞箱外也能渲染
    }

    @Override
    public int getRenderDistance() {
        return 64; // 渲染距离，可以根据需要调整
    }
}