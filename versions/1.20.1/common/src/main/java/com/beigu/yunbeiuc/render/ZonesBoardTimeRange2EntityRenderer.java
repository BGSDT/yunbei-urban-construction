package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardTimeRange1;
import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardTimeRange2;
import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange2Entity;
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

public class ZonesBoardTimeRange2EntityRenderer implements BlockEntityRenderer<ZonesBoardTimeRange2Entity> {
    private final TextRenderer textRenderer;

    public ZonesBoardTimeRange2EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(ZonesBoardTimeRange2Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String time1 = entity.getTime1();
        String time2 = entity.getTime2();
        String time3 = entity.getTime3();
        String time4 = entity.getTime4();

        if (time1 == null || time1.isEmpty()) time1 = " ";
        if (time2 == null || time2.isEmpty()) time2 = " ";
        if (time3 == null || time3.isEmpty()) time3 = " ";
        if (time4 == null || time4.isEmpty()) time4 = " ";

        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(ZonesBoardTimeRange2.FACING);
        ZonesBoardTimeRange2.Type type = entity.getCachedState().get(ZonesBoardTimeRange2.TYPE);
        renderText(matrices, vertexConsumers, light, facing, time1 + "-" + time2, type, 0f, 6f);
        renderText(matrices, vertexConsumers, light, facing, time3 + "-" + time4, type, 0f, 3f);
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, ZonesBoardTimeRange2.Type type, float andX, float andY) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.02f;

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

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                0x000000,
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
    public boolean rendersOutsideBoundingBox(ZonesBoardTimeRange2Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}