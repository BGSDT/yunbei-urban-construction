package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix4f;

public class CustomSignBlockEntityRenderer implements BlockEntityRenderer<CustomSignBlockEntity> {
    private final TextRenderer textRenderer;

    public CustomSignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(CustomSignBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        Direction facing = entity.getCachedState().get(net.minecraft.state.property.Properties.HORIZONTAL_FACING);

        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        for (CustomSignBlockEntity.TextLineData lineData : entity.getTextLines()) {
            renderTextLine(matrices, vertexConsumers, light, overlay, lineData);
        }

        matrices.pop();
    }

    private void renderTextLine(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CustomSignBlockEntity.TextLineData lineData) {
        if (lineData.getText().isEmpty()) return;
        String text = lineData.getText().trim();

        if (text.startsWith("-rect")) {
            String[] parts = text.split("\\s+");
            if (parts.length >= 3) {
                try {
                    float w = Float.parseFloat(parts[1]);
                    float h = Float.parseFloat(parts[2]);
                    renderRect(matrices, lineData, w, h);
                    return;
                } catch (NumberFormatException ignored) {}
            }
        }

        if (text.startsWith("-texture")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length >= 2) {
                Identifier textureId = Identifier.tryParse(parts[1]);
                if (textureId != null) {
                    renderTexture(matrices, vertexConsumers, light, overlay, lineData, textureId);
                    return;
                }
            }
        }

        if (text.startsWith("-json")) {
            String[] parts = text.split("\\s+", 2);
            if (parts.length >= 2) {
                try {
                    Text jsonText = Text.Serializer.fromLenientJson(parts[1]);
                    if (jsonText != null) {
                        renderJsonText(matrices, vertexConsumers, light, lineData, jsonText);
                        return;
                    }
                } catch (Exception ignored) {}
            }
        }

        renderText(matrices, vertexConsumers, light, lineData);
    }

    private void renderJsonText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CustomSignBlockEntity.TextLineData lineData, Text jsonText) {
        matrices.push();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f - 0.43f;

        matrices.translate(xPos, yPos, zPos);
        matrices.scale(baseScale, -baseScale, baseScale);

        Text renderText = jsonText.copy();
        if (renderText.getStyle().getFont() == null) {
            renderText = renderText.copy().styled(s -> s.withFont(new Identifier("minecraft", "uniform")));
        }
        if (lineData.isBold()) renderText = renderText.copy().styled(s -> s.withBold(true));
        if (lineData.isItalic()) renderText = renderText.copy().styled(s -> s.withItalic(true));
        if (lineData.isUnderline()) renderText = renderText.copy().styled(s -> s.withUnderline(true));

        int textWidth = this.textRenderer.getWidth(renderText);
        int textHeight = this.textRenderer.fontHeight;

        float renderX = switch (lineData.getAlignment().hAlign) {
            case 0 -> 0;
            case 2 -> -textWidth;
            default -> -textWidth / 2.0f;
        };
        float renderY = switch (lineData.getAlignment().vAlign) {
            case 0 -> 0;
            case 2 -> -textHeight;
            default -> -textHeight / 2.0f;
        };

        this.textRenderer.draw(renderText, renderX, renderY, lineData.getColor(), lineData.isShadow(),
                matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, CustomSignBlockEntity.TextLineData lineData) {
        matrices.push();

        float baseScale = 0.05f * lineData.getFontSize();
        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f - 0.43f;

        matrices.translate(xPos, yPos, zPos);
        matrices.scale(baseScale, -baseScale, baseScale);

        Style style = Style.EMPTY
                .withBold(lineData.isBold())
                .withItalic(lineData.isItalic())
                .withUnderline(lineData.isUnderline())
                .withFont(new Identifier("minecraft", "uniform"));

        Text renderText = Text.literal(lineData.getText()).setStyle(style);
        int textWidth = this.textRenderer.getWidth(renderText);
        int textHeight = this.textRenderer.fontHeight;

        float renderX = switch (lineData.getAlignment().hAlign) {
            case 0 -> 0;
            case 2 -> -textWidth;
            default -> -textWidth / 2.0f;
        };
        float renderY = switch (lineData.getAlignment().vAlign) {
            case 0 -> 0;
            case 2 -> -textHeight;
            default -> -textHeight / 2.0f;
        };

        this.textRenderer.draw(renderText, renderX, renderY, lineData.getColor(), lineData.isShadow(),
                matrices.peek().getPositionMatrix(), vertexConsumers, TextRenderer.TextLayerType.NORMAL, 0, light);

        matrices.pop();
    }

    private void renderRect(MatrixStack matrices, CustomSignBlockEntity.TextLineData lineData, float width, float height) {
        matrices.push();
        float centerX = lineData.getXOffset() / 16f;
        float centerY = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f - 0.43f;
        // 矩形大小乘以 fontSize
        float scale = lineData.getFontSize();
        float halfW = width / 16f / 2f * scale;
        float halfH = height / 16f / 2f * scale;

        float offsetX = switch (lineData.getAlignment().hAlign) {
            case 0 -> halfW;
            case 2 -> -halfW;
            default -> 0;
        };
        float offsetY = switch (lineData.getAlignment().vAlign) {
            case 0 -> -halfH;
            case 2 -> halfH;
            default -> 0;
        };

        matrices.translate(centerX + offsetX, centerY + offsetY, zPos);

        int color = lineData.getColor();
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (color >> 24) == 0 ? 255 : (color >> 24) & 0xFF;

        Matrix4f matrix = matrices.peek().getPositionMatrix();

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(VertexFormat.DrawMode.TRIANGLES, VertexFormats.POSITION_COLOR);
        buffer.vertex(matrix, -halfW, -halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, halfW, -halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, halfW, halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, -halfW, -halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, halfW, halfH, 0).color(r, g, b, a).next();
        buffer.vertex(matrix, -halfW, halfH, 0).color(r, g, b, a).next();
        BufferRenderer.drawWithGlobalProgram(buffer.end());

        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();

        matrices.pop();
    }

    private void renderTexture(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay, CustomSignBlockEntity.TextLineData lineData, Identifier textureId) {
        matrices.push();

        float xPos = lineData.getXOffset() / 16f;
        float yPos = lineData.getYOffset() / 16f;
        float zPos = lineData.getZOffset() / 16f - 0.43f;

        matrices.translate(xPos, yPos, zPos);

        float imageSize = 0.4f * lineData.getFontSize();
        float halfSize = imageSize / 2f;

        float offsetX = switch (lineData.getAlignment().hAlign) {
            case 0 -> halfSize;
            case 2 -> -halfSize;
            default -> 0;
        };
        float offsetY = switch (lineData.getAlignment().vAlign) {
            case 0 -> -halfSize;
            case 2 -> halfSize;
            default -> 0;
        };

        matrices.translate(offsetX, offsetY, 0);

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderLayer.getEntityCutout(textureId));
        Matrix4f matrix = matrices.peek().getPositionMatrix();

        consumer.vertex(matrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, -halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 1.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, halfSize, halfSize, 0).color(255, 255, 255, 255).texture(1.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();
        consumer.vertex(matrix, -halfSize, halfSize, 0).color(255, 255, 255, 255).texture(0.0f, 0.0f).overlay(overlay).light(light).normal(0, 0, 1).next();

        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(CustomSignBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}