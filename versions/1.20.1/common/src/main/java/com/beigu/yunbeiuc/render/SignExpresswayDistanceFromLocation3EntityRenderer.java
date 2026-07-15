package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation3;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation3Entity;
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

public class   SignExpresswayDistanceFromLocation3EntityRenderer implements BlockEntityRenderer<SignExpresswayDistanceFromLocation3Entity> {
    private final TextRenderer textRenderer;

    public SignExpresswayDistanceFromLocation3EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }


    @Override
    public void render(SignExpresswayDistanceFromLocation3Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayDistanceFromLocation3.FACING);
        SignExpresswayDistanceFromLocation3.Type type = entity.getCachedState().get(SignExpresswayDistanceFromLocation3.TYPE);

        renderText(matrices, vertexConsumers, light, facing, text1, type, 0f, 5.5f, 0XFFFFFF);
        renderText(matrices, vertexConsumers, light, facing, text2 + "个出口", type, 0f, -5.5f, 0XFFFFFF);
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignExpresswayDistanceFromLocation3.Type type, float andX, float andY, int color) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        float scaleValue = 0.045f;
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
        this.textRenderer.draw(styledText, 0, -textHeight / 2.0f, color, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(SignExpresswayDistanceFromLocation3Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}