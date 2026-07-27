package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class CustomSignBlockEntityRenderer implements BlockEntityRenderer<CustomSignBlockEntity> {
    private final TextRenderer textRenderer;

    public CustomSignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(CustomSignBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction facing = entity.getCachedState().get(net.minecraft.state.property.Properties.HORIZONTAL_FACING);

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        for (CustomSignBlockEntity.TextLineData lineData : entity.getTextLines()) {
            renderTextLine(matrices, vertexConsumers, light, lineData);
        }

        matrices.pop();
    }

    private void renderTextLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CustomSignBlockEntity.TextLineData lineData) {
        if (lineData.getText().isEmpty()) return;

        matrices.push();

        float baseScale = 0.04f * lineData.getFontSize();

        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = -0.43f;

        matrices.translate(xPos, yPos, zPos);
        matrices.scale(baseScale, -baseScale, baseScale);

        Style style = Style.EMPTY
                .withBold(lineData.isBold())
                .withItalic(lineData.isItalic())
                .withUnderline(lineData.isUnderline());

        Text text = Text.literal(lineData.getText()).setStyle(style);

        int textWidth = this.textRenderer.getWidth(text);

        float renderX = switch (lineData.getAlignment()) {
            case LEFT -> 0;
            case CENTER -> -textWidth / 2.0f;
            case RIGHT -> -textWidth;
        };

        this.textRenderer.draw(
                text,
                renderX,
                -this.textRenderer.fontHeight / 2.0f,
                lineData.getColor(),
                lineData.isShadow(),
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(CustomSignBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}