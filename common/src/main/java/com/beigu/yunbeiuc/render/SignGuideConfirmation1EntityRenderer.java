package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideConfirmation1;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideConfirmation1;
import com.beigu.yunbeiuc.entity.SignGuideConfirmation1Entity;
import com.beigu.yunbeiuc.entity.SignGuideConfirmation1Entity;
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
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import org.joml.Matrix4f;
import net.minecraft.util.Identifier;
import com.beigu.yunbeiuc.YunbeiUrbanConstruction;

public class SignGuideConfirmation1EntityRenderer implements BlockEntityRenderer<SignGuideConfirmation1Entity> {
    private final TextRenderer textRenderer;

    public SignGuideConfirmation1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SignGuideConfirmation1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideConfirmation1Entity.Unit unit1 = entity.getUnit1();
        SignGuideConfirmation1Entity.Unit unit2 = entity.getUnit2();
        SignGuideConfirmation1Entity.Unit unit3 = entity.getUnit3();
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();
        String length1 = entity.getLength1();
        String length2 = entity.getLength2();
        String length3 = entity.getLength3();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";
        if (length1 == null || length1.isEmpty()) length1 = " ";
        if (length2 == null || length2.isEmpty()) length2 = " ";
        if (length3 == null || length3.isEmpty()) length3 = " ";

        String unit1_number = switch (unit1){
            case KILOMETRE -> "km";
            case METRE -> "m";
        };
        String unit2_number = switch (unit2){
            case KILOMETRE -> "km";
            case METRE -> "m";
        };String unit3_number = switch (unit3){
            case KILOMETRE -> "km";
            case METRE -> "m";
        };

        Direction facing = entity.getCachedState().get(SignGuideConfirmation1.FACING);
        SignGuideConfirmation1.Type type = entity.getCachedState().get(SignGuideConfirmation1.TYPE);

        Block currentBlock = entity.getCachedState().getBlock();

        if(currentBlock == SignBlocks.SIGN_GUIDE_CONFIRMATION_1.get()){
            renderLeftText(matrices, vertexConsumers, light, facing, text1, type, -16f, 11f);
            renderLeftText(matrices, vertexConsumers, light, facing, text2, type, -16f, 0f);
            renderLeftText(matrices, vertexConsumers, light, facing, text3, type, -16f, -11f);
            renderRightText(matrices, vertexConsumers, light, facing, length1, type, 13f, 11f, false);
            renderRightText(matrices, vertexConsumers, light, facing, length2, type, 13f, 0f, false);
            renderRightText(matrices, vertexConsumers, light, facing, length3, type, 13f, -11f, false);
            renderRightText(matrices, vertexConsumers, light, facing, unit1_number, type, 16f, 10.5f, true);
            renderRightText(matrices, vertexConsumers, light, facing, unit2_number, type, 16f, -0.5f, true);
            renderRightText(matrices, vertexConsumers, light, facing, unit3_number, type, 16f, -11.5f, true);
        }else{
            renderLeftText(matrices, vertexConsumers, light, facing, text1, type, -12f, 11f);
            renderLeftText(matrices, vertexConsumers, light, facing, text2, type, -12f, 0f);
            renderLeftText(matrices, vertexConsumers, light, facing, text3, type, -12f, -11f);
            renderRightText(matrices, vertexConsumers, light, facing, length1, type, 9f, 11f, false);
            renderRightText(matrices, vertexConsumers, light, facing, length2, type, 9f, 0f, false);
            renderRightText(matrices, vertexConsumers, light, facing, length3, type, 9f, -11f, false);
            renderRightText(matrices, vertexConsumers, light, facing, unit1_number, type, 12f, 11f, true);
            renderRightText(matrices, vertexConsumers, light, facing, unit2_number, type, 12f, 0f, true);
            renderRightText(matrices, vertexConsumers, light, facing, unit3_number, type, 12f, -11f, true);
        }
    }

    private void renderLeftText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideConfirmation1.Type type, float andX, float andY) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.035f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
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
                0,
                -textHeight / 2.0f,
                0XFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    private void renderRightText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideConfirmation1.Type type, float andX, float andY, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = isSmallScale ? 0.02f : 0.035f;

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
                0XFFFFFF,
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
    public boolean rendersOutsideBoundingBox(SignGuideConfirmation1Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}