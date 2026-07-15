package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning7;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning7Entity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
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

public class SignGuideIntersectionAdvanceWarning7EntityRenderer implements BlockEntityRenderer<SignGuideIntersectionAdvanceWarning7Entity> {
    private final TextRenderer textRenderer;

    public SignGuideIntersectionAdvanceWarning7EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    private static final Identifier LEFT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_left.png");
    private static final Identifier STRAIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_straight.png");
    private static final Identifier RIGHT = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_indication_right.png");

    @Override
    public void render(SignGuideIntersectionAdvanceWarning7Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideIntersectionAdvanceWarning7Entity.Direction direction1 = entity.getDirection1();
        SignGuideIntersectionAdvanceWarning7Entity.Direction direction2 = entity.getDirection2();
        SignGuideIntersectionAdvanceWarning7Entity.Direction direction3 = entity.getDirection3();
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning7.FACING);
        SignGuideIntersectionAdvanceWarning7.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning7.TYPE);

        renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction1, -13f, 12f, type);
        renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction2, -13f, 0f, type);
        renderDirectionLogo(matrices, vertexConsumers, light, overlay, facing, direction3, -13f, -12f, type);
        renderText(matrices, vertexConsumers, light, facing, text1, type, 6f, 12f, direction1);
        renderText(matrices, vertexConsumers, light, facing, text2, type, 6f, 0f, direction2);
        renderText(matrices, vertexConsumers, light, facing, text3, type, 6f, -12f, direction3);
    }

    private void renderDirectionLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, SignGuideIntersectionAdvanceWarning7Entity.Direction direction, float andX, float andY, SignGuideIntersectionAdvanceWarning7.Type type) {
        Identifier texture = switch (direction) {
            case LEFT -> LEFT;
            case STRAIGHT -> STRAIGHT;
            case RIGHT -> RIGHT;
        };

        matrices.push();

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float arrowSize = 0.4f;
        float halfSize = arrowSize / 2f;

        float x = andX / 16f;
        if(texture == RIGHT) x = -x;
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

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionAdvanceWarning7.Type type, float andX, float andY, SignGuideIntersectionAdvanceWarning7Entity.Direction direction) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.035f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        if(direction == SignGuideIntersectionAdvanceWarning7Entity.Direction.RIGHT) andX = -andX;
        float centeredX = andX / 16f - (textWidth * scaleValue) / 2f;
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

    @Override
    public boolean rendersOutsideBoundingBox(SignGuideIntersectionAdvanceWarning7Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}