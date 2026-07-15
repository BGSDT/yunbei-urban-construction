package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.sign.ZonesBoardImage;
import com.beigu.yunbeiuc.entity.ZonesBoardImageEntity;
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

public class ZonesBoardImageEntityRenderer implements BlockEntityRenderer<ZonesBoardImageEntity> {
    private final TextRenderer textRenderer;

    public ZonesBoardImageEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    private static final Identifier RED = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_red_number_logo.png");
    private static final Identifier YELLOW = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_yellow_number_logo.png");
    private static final Identifier WHITE = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_white_number_logo.png");
    private static final Identifier EXPRESSWAY = new Identifier(YunbeiUrbanConstruction.MOD_ID, "textures/block/sign/sign_expressway_logo.png");

    @Override
    public void render(ZonesBoardImageEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        ZonesBoardImageEntity.BoardImage image = entity.getImage();
        Float andX = entity.getAndX();
        Float andY = entity.getAndY();
        Float andScale = entity.getAndScale();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Direction facing = entity.getCachedState().get(ZonesBoardImage.FACING);
        ZonesBoardImage.Type type = entity.getCachedState().get(ZonesBoardImage.TYPE);

        renderLogo(matrices, vertexConsumers, light, overlay, facing, image, 0f + andX, 4f + andY, type, andScale);

        renderText(matrices, vertexConsumers, light, facing, text1, type, 0f + andX, 4f + andY, image, andScale);
    }

    private void renderLogo(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, Direction facing, ZonesBoardImageEntity.BoardImage image, float andX, float andY, ZonesBoardImage.Type type, Float andScale) {
        Identifier texture = switch (image) {
            case RED -> RED;
            case YELLOW -> YELLOW;
            case WHITE -> WHITE;
            case EXPRESSWAY -> EXPRESSWAY;
        };

        matrices.push();

        float zOffset = switch (type) {
            case POLE_L -> -1.75f;
            case POLE_H -> -1.79f;
            case NORMAL -> -1.43f;
        };

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float arrowSize = 0.75f;
        if(image == ZonesBoardImageEntity.BoardImage.EXPRESSWAY){
            arrowSize = 1f;
        }
        arrowSize = arrowSize + andScale;
        float halfSize = arrowSize / 2f;

        float x = andX / 16f;
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

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, ZonesBoardImage.Type type, float andX, float andY, ZonesBoardImageEntity.BoardImage image, Float andScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = 0.03f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -1.74f;
            case POLE_H -> -1.78f;
            case NORMAL -> -1.42f;
        };

        int color = 0X000000;
        if(image == ZonesBoardImageEntity.BoardImage.RED) color = 0XFFFFFF;
        if(image == ZonesBoardImageEntity.BoardImage.EXPRESSWAY){
            color = 0X2D9B47;
            scaleValue = 0.02f;
        }

        scaleValue = scaleValue + andScale * 0.02f;

        float centeredX = andX / 16f - (textWidth * scaleValue) / 2f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                color,
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
    public boolean rendersOutsideBoundingBox(ZonesBoardImageEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}