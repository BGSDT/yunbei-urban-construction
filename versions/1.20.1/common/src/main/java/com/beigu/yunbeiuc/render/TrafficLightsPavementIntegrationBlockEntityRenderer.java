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
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import com.mojang.math.Axis;
import org.joml.Matrix4f;

public class TrafficLightsPavementIntegrationBlockEntityRenderer implements BlockEntityRenderer<TrafficLightsPavementIntegrationBlockEntity> {

    private static final ResourceLocation PAVEMENT_RED = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_red.png");
    private static final ResourceLocation PAVEMENT_GREEN = new ResourceLocation(YunbeiUrbanConstruction.MOD_ID, "textures/block/lights/pavement_green.png");

    private final Font textRenderer;

    public TrafficLightsPavementIntegrationBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.textRenderer = ctx.getFont();
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

        // 正面渲染
        renderLogo(matrices, vertexConsumers, light, overlay, facing, lightState, zOffset, false, directionType);
        renderPedestrianText(matrices, vertexConsumers, light, facing, lightState, zOffset, false);

        if (entity.isShowSeconds()) {
            renderPavementSeconds(entity, matrices, vertexConsumers, light, facing, lightState, zOffset, false);
        }

        // 反面渲染
        renderLogo(matrices, vertexConsumers, light, overlay, facing, lightState, zOffset, true, directionType);
        renderPedestrianText(matrices, vertexConsumers, light, facing, lightState, zOffset, true);

        if (entity.isShowSeconds()) {
            renderPavementSeconds(entity, matrices, vertexConsumers, light, facing, lightState, zOffset, true);
        }
    }

    private void renderLogo(PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay,
                           Direction facing, TrafficLightsBlock.LightState lightState, float zOffset, boolean isBack,
                           TrafficLightsBlockEntity.DirectionType directionType) {
        // COLOR_FLASH 图案的色闪逻辑：与灯的GRAY闪烁同步（0.5s一闪），隐藏阶段不渲染
        if (directionType == TrafficLightsBlockEntity.DirectionType.COLOR_FLASH) {
            long currentTimeMillis = System.currentTimeMillis();
            int flashPhase = (int) ((currentTimeMillis / 500) % 2);
            if (flashPhase == 0) {
                return;
            }
        }

        // SLOW_FLASH 图案的慢闪逻辑：与灯的GRAY闪烁同步（0.5s一闪），隐藏阶段不渲染
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

        // 移动到方块中心
        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        // 反面需要旋转180度
        if (isBack) {
            matrices.mulPose(Axis.YP.rotationDegrees(180));
        }

        // Logo的Y位置：红灯在上方，绿灯在下方
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

        // 根据灯光状态显示不同的文字
        if (lightState == TrafficLightsBlock.LightState.RED || lightState == TrafficLightsBlock.LightState.YELLOW) {
            text = "行人禁止通行";
            color = 0xFF0000; // 红色
        } else if (lightState == TrafficLightsBlock.LightState.GREEN) {
            text = "行人可以通行";
            color = 0x39FF00; // 绿色
        } else {
            return; // GRAY状态不显示文字
        }

        float baseY = 10.85f / 16f;

        float scaleValue = 0.027f;

        // 竖排显示：每个字单独渲染，从上到下排列
        char[] chars = text.toCharArray();
        float lineHeight = 0.25f; // 每行间距
        float startY = baseY + (chars.length - 1) * lineHeight / 2f; // 居中起始位置

        for (int i = 0; i < chars.length; i++) {
            matrices.pushPose();

            matrices.translate(0.5, 0.5, 0.5);
            matrices.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

            // 反面需要旋转180度
            if (isBack) {
                matrices.mulPose(Axis.YP.rotationDegrees(180));
            }

            float currentY = startY - i * lineHeight;
            matrices.translate(0f, currentY, zOffset);
            matrices.scale(scaleValue, -scaleValue, scaleValue);

            // 使用 Minecraft 原生 Font 渲染单个字
            Component styledText = Text.literal(String.valueOf(chars[i])).setStyle(Style.EMPTY.withBold(true)
                    .withFont(new ResourceLocation("minecraft", "uniform")));
            int textWidth = this.textRenderer.width(styledText);
            int textHeight = this.textRenderer.lineHeight;

            // 居中对齐
            float centeredX = -textWidth / 2f;

            this.textRenderer.drawInBatch(styledText, centeredX, -textHeight / 2.0f, color, false,
                    matrices.last().pose(), vertexConsumers,
                    Font.DisplayMode.NORMAL, 0, light);

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
        matrices.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        // 反面需要旋转180度
        if (isBack) {
            matrices.mulPose(Axis.YP.rotationDegrees(180));
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

        // 渲染实际数字
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

    @Override
    public boolean shouldRenderOffScreen(TrafficLightsPavementIntegrationBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
