package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning1;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning4;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideRoadsideFacilityOverloadCheckpoint1;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning1Entity;
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

import java.util.Map;

public class SignGuideIntersectionWarning1EntityRenderer implements BlockEntityRenderer<SignGuideIntersectionWarning1Entity> {
    private final TextRenderer textRenderer;

    public SignGuideIntersectionWarning1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SignGuideIntersectionWarning1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionWarning1.FACING);
        SignGuideIntersectionWarning1.Type type = entity.getCachedState().get(SignGuideIntersectionWarning1.TYPE);
        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_1.get() || currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_6.get()){
            renderText(matrices, vertexConsumers, light, facing, text1, type, 0f, 0f, true);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_2.get()){
            renderText(matrices, vertexConsumers, light, facing, text1, type, -2.5f, 0f, true);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_3.get()){
            renderText(matrices, vertexConsumers, light, facing, text1, type, 0f, 0f, true);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_1.get() || currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_2.get() || currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_3.get()){
            renderUnitText(matrices, vertexConsumers, light, facing, text1, type, 5f, 0f, false);
            renderUnitText(matrices, vertexConsumers, light, facing, "km", type, 9f, -0.5f, true);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_4.get() || currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_5.get() || currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_6.get()){
            renderUnitText(matrices, vertexConsumers, light, facing, text1, type, -0.5f, -4f, false);
            renderUnitText(matrices, vertexConsumers, light, facing, "km", type, 3.5f, -4.5f, true);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_ODOMETER.get()){
            renderText(matrices, vertexConsumers, light, facing, text1, type, 0f, 3f, false);
        }
    }

    private void renderUnitText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionWarning1.Type type, float andX, float andY, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = isSmallScale ? 0.03f : 0.045f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        float centeredX = andX / 16f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                -textWidth,
                -textHeight / 2.0f,
                0X275aa8,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionWarning1.Type type, float andX, float andY, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = isSmallScale ? 0.035f : 0.055f;

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
    public boolean rendersOutsideBoundingBox(SignGuideIntersectionWarning1Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}