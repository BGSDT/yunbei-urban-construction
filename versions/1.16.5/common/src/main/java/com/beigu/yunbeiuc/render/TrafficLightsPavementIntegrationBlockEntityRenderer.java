package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.YunbeiUrbanConstruction;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsPavementIntegrationBlock;
import com.beigu.yunbeiuc.block.custom.traffic.TrafficLightsBlock;
import com.beigu.yunbeiuc.entity.TrafficLightsPavementIntegrationBlockEntity;
import com.beigu.yunbeiuc.entity.TrafficLightsBlockEntity;
import com.beigu.yunbeiuc.util.CustomFontRenderer;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.RenderType;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;
import com.mojang.math.Matrix4f;

// 人行道一体化红绿灯渲染器：与人行横道信号灯同一套灯色与读秒显示。

public class TrafficLightsPavementIntegrationBlockEntityRenderer extends BlockEntityRendererCompat<TrafficLightsPavementIntegrationBlockEntity> {

    private static final ResourceLocation PAVEMENT_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_red.png");
    private static final ResourceLocation PAVEMENT_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green.png");

    private Font textRenderer;

    public TrafficLightsPavementIntegrationBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
    }

    private Font textRenderer() {
        if (this.textRenderer == null) {
            this.textRenderer = net.minecraft.client.Minecraft.getInstance().font;
        }
        return this.textRenderer;
    }

    @Override
    public void render(TrafficLightsPavementIntegrationBlockEntity entity, float tickDelta, PoseStack matrices,
                       MultiBufferSource vertexConsumers, int light, int overlay) {
        if (entity.getBlockState().getValue(TrafficLightsPavementIntegrationBlock.PART) != TrafficLightsPavementIntegrationBlock.TriplePart.BOTTOM) {
            return;
        }

        Direction facing = entity.getBlockState().getValue(TrafficLightsPavementIntegrationBlock.FACING);
        TrafficLightsBlock.LightState lightState = entity.getBlockState().getValue(TrafficLightsPavementIntegrationBlock.LIGHT_STATE);
        TrafficLightsBlockEntity.DirectionType directionType = entity.getDirectionType();
        float zOffset = 0.19f;

        renderLogo(matrices, vertexConsumers, light, overlay, facing, lightState, zOffset, false, directionType);
        renderPedestrianText(matrices, vertexConsumers, light, facing, lightState, zOffset, false);

        if (entity.isShowSeconds()) {
            renderPavementSeconds(entity, matrices, vertexConsumers, light, facing, lightState, zOffset, false);
        }

        renderLogo(matrices, vertexConsumers, light, overlay, facing, lightState, zOffset, true, directionType);
        renderPedestrianText(matrices, vertexConsumers, light, facing, lightState, zOffset, true);

        if (entity.isShowSeconds()) {
            renderPavementSeconds(entity, matrices, vertexConsumers, light, facing, lightState, zOffset, true);
        }
    }

    private void renderLogo(PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay,
                           Direction facing, TrafficLightsBlock.LightState lightState, float zOffset, boolean isBack,
                           TrafficLightsBlockEntity.DirectionType directionType) {
        if (directionType == TrafficLightsBlockEntity.DirectionType.COLOR_FLASH) {
            long currentTimeMillis = System.currentTimeMillis();
            int flashPhase = (int) ((currentTimeMillis / 500) % 2);
            if (flashPhase == 0) {
                return;
            }
        }

        if (directionType == TrafficLightsBlockEntity.DirectionType.SLOW_FLASH) {
            long currentTimeMillis = System.currentTimeMillis();
            int flashPhase = (int) ((currentTimeMillis / 500) % 2);
            if (flashPhase == 0) {
                return;
            }
        }

        ResourceLocation texture = switch (lightState) {
            case RED, YELLOW -> PAVEMENT_RED;
            case GREEN -> PAVEMENT_GREEN;
            default -> null;
        };

        if (texture == null) return;

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));

        if (isBack) {
            matrices.mulPose(Vector3f.YP.rotationDegrees(180));
        }

        float logoY = (lightState == TrafficLightsBlock.LightState.RED || lightState == TrafficLightsBlock.LightState.YELLOW)
                ? 35f / 16f : 27f / 16f;

        matrices.translate(0f, logoY, zOffset);

        float logoSize = 0.4f;
        float halfSize = logoSize / 2f;

        VertexConsumer consumer = vertexConsumers.getBuffer(RenderType.entityCutout(texture));
        Matrix4f positionMatrix = matrices.last().pose();

        consumer.vertex(positionMatrix, -halfSize, -halfSize, 0).color(255, 255, 255, 255).uv(0.0f, 1.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();
        consumer.vertex(positionMatrix, halfSize, -halfSize, 0).color(255, 255, 255, 255).uv(1.0f, 1.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();
        consumer.vertex(positionMatrix, halfSize, halfSize, 0).color(255, 255, 255, 255).uv(1.0f, 0.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();
        consumer.vertex(positionMatrix, -halfSize, halfSize, 0).color(255, 255, 255, 255).uv(0.0f, 0.0f).overlayCoords(overlay).uv2(light).normal(matrices.last().normal(), 0, 0, 1).endVertex();

        matrices.popPose();
    }

    private void renderPedestrianText(PoseStack matrices, MultiBufferSource vertexConsumers, int light,
                                     Direction facing, TrafficLightsBlock.LightState lightState, float zOffset, boolean isBack) {
        String text;
        int color;

        if (lightState == TrafficLightsBlock.LightState.RED || lightState == TrafficLightsBlock.LightState.YELLOW) {
            text = "行人禁止通行";
            color = 0xFF0000;
        } else if (lightState == TrafficLightsBlock.LightState.GREEN) {
            text = "行人可以通行";
            color = 0x39FF00;
        } else {
            return;
        }

        float baseY = 10.85f / 16f;

        float scaleValue = 0.027f;

        char[] chars = text.toCharArray();
        float lineHeight = 0.25f;
        float startY = baseY + (chars.length - 1) * lineHeight / 2f;

        for (int i = 0; i < chars.length; i++) {
            matrices.pushPose();

            matrices.translate(0.5, 0.5, 0.5);
            matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));

            if (isBack) {
                matrices.mulPose(Vector3f.YP.rotationDegrees(180));
            }

            float currentY = startY - i * lineHeight;
            matrices.translate(0f, currentY, zOffset);
            matrices.scale(scaleValue, -scaleValue, scaleValue);

            Component styledText = Text.literal(String.valueOf(chars[i])).setStyle(Style.EMPTY.withBold(true)
                    .withFont(new ResourceLocation("minecraft", "uniform")));
            Font font = textRenderer();
            int textWidth = font.width(styledText);
            int textHeight = font.lineHeight;

            float centeredX = -textWidth / 2f;

            font.drawInBatch(styledText, centeredX, -textHeight / 2.0f, color, false,
                    matrices.last().pose(), vertexConsumers,
                    false, 0, light);

            matrices.popPose();
        }
    }

    private void renderPavementSeconds(TrafficLightsPavementIntegrationBlockEntity entity, PoseStack matrices,
                                      MultiBufferSource vertexConsumers, int light, Direction facing, TrafficLightsBlock.LightState lightState, float zOffset, boolean isBack) {
        int remaining;
        if (entity.isInGroup()) {
            TrafficLightsBlockEntity.LightTimingInfo info = entity.getLightTimingInfo();

            remaining = info.getActiveRemaining();
            if (remaining < 0) {
                return;
            }
        } else {
            remaining = entity.getFixedSeconds();
        }

        int color = (lightState == TrafficLightsBlock.LightState.RED) ? 0xFF0000 : 0x39FF00;

        float logoY = (lightState == TrafficLightsBlock.LightState.RED || lightState == TrafficLightsBlock.LightState.YELLOW)
                ? 27f / 16f : 35f / 16f;

        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));

        if (isBack) {
            matrices.mulPose(Vector3f.YP.rotationDegrees(180));
        }

        matrices.translate(0.19f, logoY, zOffset);

        float scaleValue = 0.07f;
        matrices.scale(scaleValue, -scaleValue, scaleValue);

        String text = String.valueOf(remaining);
        if (remaining >= 99) text = "99";

        CustomFontRenderer.renderText(
                matrices, vertexConsumers, "88", 0X2e3134,
                0, -2.5f, 0,
                0.035f,
                light,
                CustomFontRenderer.TextAlignment.RIGHT,
                "ds_digital",
                1,
                1.4f
        );

        if (lightState == TrafficLightsBlock.LightState.GRAY) {
            matrices.popPose();
            return;
        }

        matrices.translate(0f, 0.0f, 0.01f);

        CustomFontRenderer.renderText(
                matrices, vertexConsumers, text, color,
                0, -2.5f, 0,
                0.035f,
                light,
                CustomFontRenderer.TextAlignment.RIGHT,
                "ds_digital",
                1,
                1.4f
        );

        matrices.popPose();
    }

    public boolean shouldRenderOffScreen(TrafficLightsPavementIntegrationBlockEntity blockEntity) {
        return true;
    }

    public int getViewDistance() {
        return 256;
    }
}
