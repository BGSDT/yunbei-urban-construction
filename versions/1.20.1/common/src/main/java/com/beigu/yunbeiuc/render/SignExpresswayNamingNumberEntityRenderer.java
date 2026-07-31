package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayNamingNumber;
import com.beigu.yunbeiuc.entity.SignExpresswayNamingNumberEntity;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.util.Identifier;
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

public class SignExpresswayNamingNumberEntityRenderer implements BlockEntityRenderer<SignExpresswayNamingNumberEntity> {
    private final TextRenderer textRenderer;

    public SignExpresswayNamingNumberEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }


    @Override
    public void render(SignExpresswayNamingNumberEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String expresswayNumber = entity.getExpresswayNumber();
        String expresswayName = entity.getExpresswayName();

        if (expresswayNumber == null || expresswayNumber.isEmpty()) expresswayNumber = " ";
        if (expresswayName == null || expresswayName.isEmpty()) expresswayName = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayNamingNumber.FACING);
        SignExpresswayNamingNumber.Type type = entity.getCachedState().get(SignExpresswayNamingNumber.TYPE);

        renderText(matrices, vertexConsumers, light, facing, expresswayNumber, type, 0f, 1.5f, false);
        renderText(matrices, vertexConsumers, light, facing, insertSpaceBetweenChars(expresswayName), type, 0f, -6.5f, true);
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignExpresswayNamingNumber.Type type, float andX, float andY, boolean isSmallScale) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        float scaleValue = isSmallScale ? 0.02f : 0.08f;
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
        this.textRenderer.draw(styledText, 0, -textHeight / 2.0f, 0XFFFFFF, false, matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);
        matrices.pop();
    }

    public static String insertSpaceBetweenChars(String str) {
        if (str == null || str.isEmpty() || str.equals(" ")) {
            return str;
        }
        return str.replaceAll(".(?!$)", "$0 ");
    }

    @Override
    public boolean rendersOutsideBoundingBox(SignExpresswayNamingNumberEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}