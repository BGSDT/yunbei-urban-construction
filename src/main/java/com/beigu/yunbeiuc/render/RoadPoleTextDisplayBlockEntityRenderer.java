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

        matrices.translate(0.5, 0.05, 0.5);

        float originalRotation = entity.getCachedState().get(RoadPoleTextDisplay.FACING).asRotation();
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-originalRotation - 90));

        matrices.translate(0.0, 0.0, 0.2);

        float baseScale = 0.01f;
        float sizeMultiplier = entity.getFontSize() / 12.0f;
        float scale = baseScale * sizeMultiplier;
        matrices.scale(scale, -scale, scale);

        int textWidth = this.textRenderer.getWidth(text);
        int textHeight = this.textRenderer.fontHeight;

        float x = -textWidth / 2.0f;
        float y = -textHeight / 2.0f;

        int color = entity.getColor();

        this.textRenderer.draw(
                Text.literal(text),
                x,
                y,
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
        return 256;
    }
}