package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1Wuhan;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
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

public class SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer implements BlockEntityRenderer<SignGuideIntersectionAdvanceWarning1WuhanEntity> {
    private final TextRenderer textRenderer;

    public SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SignGuideIntersectionAdvanceWarning1WuhanEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String cnText3 = entity.getCnText3();
        String enText3 = entity.getEnText3();
        String cnText4 = entity.getCnText4();
        String enText4 = entity.getEnText4();
        String cnText5 = entity.getCnText5();
        String enText5 = entity.getEnText5();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (cnText3 == null || cnText3.isEmpty()) cnText3 = " ";
        if (enText3 == null || enText3.isEmpty()) enText3 = " ";
        if (cnText4 == null || cnText4.isEmpty()) cnText4 = " ";
        if (enText4 == null || enText4.isEmpty()) enText4 = " ";
        if (cnText5 == null || cnText5.isEmpty()) cnText5 = " ";
        if (enText5 == null || enText5.isEmpty()) enText5 = " ";
        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1Wuhan.FACING);
        SignGuideIntersectionAdvanceWarning1Wuhan.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1Wuhan.TYPE);

        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_RIGHT){
            renderText(matrices, vertexConsumers, light, facing, text1, type, true, 16.5f, 12f, true);
            renderText(matrices, vertexConsumers, light, facing, text2, type, true, 16.5f, -12f, true);
            renderText(matrices, vertexConsumers, light, facing, cnText3, type, false, -6f, 12f, false);
            renderText(matrices, vertexConsumers, light, facing, enText3, type, false, -6f, 8f, true);
            renderText(matrices, vertexConsumers, light, facing, cnText4, type, false, -6f, 1f, false);
            renderText(matrices, vertexConsumers, light, facing, enText4, type, false, -6f, -3f, true);
            renderText(matrices, vertexConsumers, light, facing, cnText5, type, false, -6f, -10f, false);
            renderText(matrices, vertexConsumers, light, facing, enText5, type, false, -6f, -14f, true);
        }else {
            renderText(matrices, vertexConsumers, light, facing, text1, type, true, -16.5f, 12f, true);
            renderText(matrices, vertexConsumers, light, facing, text2, type, true, -16.5f, -12f, true);
            renderText(matrices, vertexConsumers, light, facing, cnText3, type, false, 6f, 12f, false);
            renderText(matrices, vertexConsumers, light, facing, enText3, type, false, 6f, 8f, true);
            renderText(matrices, vertexConsumers, light, facing, cnText4, type, false, 6f, 1f, false);
            renderText(matrices, vertexConsumers, light, facing, enText4, type, false, 6f, -3f, true);
            renderText(matrices, vertexConsumers, light, facing, cnText5, type, false, 6f, -10f, false);
            renderText(matrices, vertexConsumers, light, facing, enText5, type, false, 6f, -14f, true);
        }
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionAdvanceWarning1Wuhan.Type type, boolean isBlue, float andX, float andY, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        float scaleValue = isSmallScale ? 0.023f : 0.035f;

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

        int textColor = isBlue ? 0X275aa8 : 0xFFFFFF;

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                textColor,
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
    public boolean rendersOutsideBoundingBox(SignGuideIntersectionAdvanceWarning1WuhanEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}