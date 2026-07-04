package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.ZonesBoard1;
import com.beigu.yunbeiuc.entity.ZonesBoard1Entity;
import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

public class ZonesBoard1EntityRenderer implements BlockEntityRenderer<ZonesBoard1Entity> {
    private final TextRenderer textRenderer;

    public ZonesBoard1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(ZonesBoard1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();

        Block currentBlock = entity.getCachedState().getBlock();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Direction facing = entity.getCachedState().get(ZonesBoard1.FACING);
        ZonesBoard1.Type type = entity.getCachedState().get(ZonesBoard1.TYPE);
        renderText(matrices, vertexConsumers, light, facing, text1, type, 0f, 0f, currentBlock);
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, ZonesBoard1.Type type, float andX, float andY, Block currentBlock) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.04f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        float centeredX = andX / 16f - (textWidth * scaleValue) / 2f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        int color = 0X000000;
        if(currentBlock == SignBlocks.ZONES_BOARD_RED.get()) color = 0XFFFFFF;

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
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
    public boolean rendersOutsideBoundingBox(ZonesBoard1Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}