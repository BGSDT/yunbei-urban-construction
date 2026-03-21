package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.pole.RoadPoleTextDisplay;
import com.beigu.yunbeiuc.entity.RoadPoleTextDisplayEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.math.RotationAxis;

public class RoadPoleTextDisplayBlockEntityRenderer implements BlockEntityRenderer<RoadPoleTextDisplayEntity> {
    private final TextRenderer textRenderer;

    public RoadPoleTextDisplayBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(RoadPoleTextDisplayEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text = entity.getText();
        if (text == null || text.isEmpty()) return;

        matrices.push();

        // 1. 调整位置到方块中心（先平移）
        matrices.translate(0.5, 0.25, 0.5);

        // 2. 根据方块朝向旋转（旋转顺序很重要）
        float originalRotation = entity.getCachedState().get(RoadPoleTextDisplay.FACING).asRotation();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-originalRotation - 90));

        // 3. 移动到方块前方
        matrices.translate(0.0, 0.0, 0.2);

        // 4. 缩放（先缩放再计算居中，避免坐标被缩放影响）
        float baseScale = 0.01f;
        float sizeMultiplier = entity.getFontSize() / 12.0f;
        float scale = baseScale * sizeMultiplier;
        matrices.scale(scale, -scale, scale);

        // 5. 计算文本居中（关键修复）
        int textWidth = this.textRenderer.getWidth(text);
        int textHeight = this.textRenderer.fontHeight;

        // 水平居中：-文本宽度的一半；垂直居中：-文本高度的一半（修复垂直居中计算）
        float x = -textWidth / 2.0f;
        float y = -textHeight / 2.0f;

        // 6. 获取文本颜色
        int color = entity.getColor();

        // 7. 渲染文本（使用正确的居中坐标）
        this.textRenderer.draw(
                Text.literal(text),
                x,          // 水平居中
                y,          // 垂直居中
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
    public boolean rendersOutsideBoundingBox(RoadPoleTextDisplayEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 128;
    }
}