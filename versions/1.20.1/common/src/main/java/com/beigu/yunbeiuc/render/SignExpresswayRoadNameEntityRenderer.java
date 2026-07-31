package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayRoadName;
import com.beigu.yunbeiuc.entity.SignExpresswayRoadNameEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.util.Identifier;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class SignExpresswayRoadNameEntityRenderer implements BlockEntityRenderer<SignExpresswayRoadNameEntity> {
    private final TextRenderer textRenderer;

    public SignExpresswayRoadNameEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SignExpresswayRoadNameEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayRoadName.FACING);
        SignExpresswayRoadName.Type type = entity.getCachedState().get(SignExpresswayRoadName.TYPE);

        renderText(matrices, vertexConsumers, light, facing, text1, type, 0f, 0f, 0XFFFFFF);
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignExpresswayRoadName.Type type, float andX, float andY, int color) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        float scaleValue = 0.045f;
        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true).withFont(new Identifier("minecraft", "uniform")));
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
    public boolean rendersOutsideBoundingBox(SignExpresswayRoadNameEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}