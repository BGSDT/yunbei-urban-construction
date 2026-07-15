package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning5;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning5Entity;
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

public class SignGuideIntersectionAdvanceWarning5EntityRenderer implements BlockEntityRenderer<SignGuideIntersectionAdvanceWarning5Entity> {
    private final TextRenderer textRenderer;

    public SignGuideIntersectionAdvanceWarning5EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SignGuideIntersectionAdvanceWarning5Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();
        String text4 = entity.getText4();
        Float text1AndY = entity.getText1AndY();
        Float text2AndY = entity.getText2AndY();
        Float text3AndY = entity.getText3AndY();
        Float text4AndY = entity.getText4AndY();


        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";
        if (text4 == null || text4.isEmpty()) text4 = " ";

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning5.FACING);
        SignGuideIntersectionAdvanceWarning5.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning5.TYPE);
        renderText(matrices, vertexConsumers, light, facing, text1, type, -10f, 10f + text1AndY);
        renderText(matrices, vertexConsumers, light, facing, text2, type, 10f, 10f + text2AndY);
        renderText(matrices, vertexConsumers, light, facing, text3, type, -10f, -10f + text3AndY);;
        renderText(matrices, vertexConsumers, light, facing, text4, type, 10f, -10f + text4AndY);
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionAdvanceWarning5.Type type, float andX, float andY) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.03f;

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
                0xFFFFFF,
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
    public boolean rendersOutsideBoundingBox(SignGuideIntersectionAdvanceWarning5Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}