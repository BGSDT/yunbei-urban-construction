package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation2;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation2;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation2;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation2Entity;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation2Entity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class SignExpresswayDistanceFromLocation2EntityRenderer implements BlockEntityRenderer<SignExpresswayDistanceFromLocation2Entity> {
    private final TextRenderer textRenderer;

    public SignExpresswayDistanceFromLocation2EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    private static final Identifier NATIONAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_1.png");
    private static final Identifier PROVINCIAL_1 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_1.png");
    private static final Identifier NATIONAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_national_logo_2.png");
    private static final Identifier PROVINCIAL_2 = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_provicial_logo_2.png");

    @Override
    public void render(SignExpresswayDistanceFromLocation2Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignExpresswayDistanceFromLocation2Entity.Expressway expressway1 = entity.getExpressway1();
        String text1 = entity.getText1();
        String expresswayNumber = entity.getExpresswayNumber();
        String text3 = entity.getText3();
        String length1 = entity.getLength1();
        String length2 = entity.getLength2();
        String length3 = entity.getLength3();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (expresswayNumber == null || expresswayNumber.isEmpty()) expresswayNumber = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";
        if (length1 == null || length1.isEmpty()) length1 = " ";
        if (length2 == null || length2.isEmpty()) length2 = " ";
        if (length3 == null || length3.isEmpty()) length3 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayDistanceFromLocation2.FACING);
        SignExpresswayDistanceFromLocation2.Type type = entity.getCachedState().get(SignExpresswayDistanceFromLocation2.TYPE);

        renderLeftText(matrices, vertexConsumers, light, facing, text1, type, -15f, 11f);
        renderExpresswayLogo(matrices, vertexConsumers, light, overlay, facing, expressway1, -11f, 0f, type, expresswayNumber);
        renderExpresswayText(matrices, vertexConsumers, light, facing, expresswayNumber, type, -11f, -1f);
        renderLeftText(matrices, vertexConsumers, light, facing, text3, type, -15f, -11f);
        renderRightText(matrices, vertexConsumers, light, facing, length1, type, 10f, 11f, false);
        renderRightText(matrices, vertexConsumers, light, facing, length2, type, 10f, 0f, false);
        renderRightText(matrices, vertexConsumers, light, facing, length3, type, 10f, -11f, false);
        renderRightText(matrices, vertexConsumers, light, facing, "km", type, 14f, 10.5f, true);
        renderRightText(matrices, vertexConsumers, light, facing, "km", type, 14f, -0.5f, true);
        renderRightText(matrices, vertexConsumers, light, facing, "km", type, 14f, -11.5f, true);
    }

    private void renderExpresswayLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignExpresswayDistanceFromLocation2Entity.Expressway expressway, float andX, float andY, SignExpresswayDistanceFromLocation2.Type type, String expresswayNumber) {
        Identifier texture = switch (expressway) {
            case NATIONAL -> {
                if (expresswayNumber == null || expresswayNumber.trim().isEmpty() || !expresswayNumber.matches(".*\\d.*")) {
                    yield NATIONAL_1;
                } else {
                    // 提取字符串中的数字
                    String digits = expresswayNumber.replaceAll("[^0-9]", "");
                    if (digits.length() == 1) {
                        yield NATIONAL_2;
                    } else {
                        yield NATIONAL_1;
                    }
                }
            }
            case PROVINCIAL -> {
                if (expresswayNumber == null || expresswayNumber.trim().isEmpty() || !expresswayNumber.matches(".*\\d.*")) {
                    yield PROVINCIAL_1;
                } else {
                    // 提取字符串中的数字
                    String digits = expresswayNumber.replaceAll("[^0-9]", "");
                    if (digits.length() == 1) {
                        yield PROVINCIAL_2;
                    } else {
                        yield PROVINCIAL_1;
                    }
                }
            }
        };

        matrices.push();

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float arrowSize = 0.65f;
        float halfSize = arrowSize / 2f;

        float x = andX / 16f;
        if (texture == PROVINCIAL_1 || texture == NATIONAL_1) x = x + 1f / 16f;
        float y = andY / 16f;

        matrices.translate(x, y, zOffset);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(texture));
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        consumer.vertex(matrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, -halfSize, halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();

        matrices.pop();
    }

    private void renderLeftText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignExpresswayDistanceFromLocation2.Type type, float andX, float andY) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.04f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true).withFont(new Identifier("minecraft", "uniform")));
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

    private void renderRightText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignExpresswayDistanceFromLocation2.Type type, float andX, float andY, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = isSmallScale ? 0.025f : 0.04f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true).withFont(new Identifier("minecraft", "uniform")));
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

    private void renderExpresswayText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignExpresswayDistanceFromLocation2.Type type, float andX, float andY) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        float scaleValue = 0.045f;
        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true).withFont(new Identifier("minecraft", "uniform")));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;
        float zOffset = switch (type) {
            case POLE_L -> -0.74f;
            case POLE_H -> -0.78f;
            case NORMAL -> -0.42f;
        };
        float centeredX = andX / 16f - (textWidth * scaleValue) / 2f;
        float centeredY = andY / 16f;
        String digits = text.replaceAll("[^0-9]", "");
        if (text.trim().isEmpty() || !text.matches(".*\\d.*") || digits.length() != 1) centeredX = centeredX + 1f / 16f;
        matrices.translate(centeredX, centeredY, zOffset);
        matrices.scale(scaleValue, -scaleValue, scaleValue);
        this.textRenderer.draw(styledText, 0, -textHeight / 2.0f, 0xFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(SignExpresswayDistanceFromLocation2Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}