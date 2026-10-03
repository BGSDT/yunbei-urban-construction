package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.RoadNameSignBlock;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1Wuhan;
import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import com.beigu.yunbeiuc.api.mapper.VersionServices;

import java.util.Map;

public class RoadNameSignBlockEntityRenderer extends BlockEntityRendererCompat<RoadNameSignBlockEntity> {
    private final Font textRenderer;
    private Block currentBlock;

    public RoadNameSignBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        super(ctx);
        this.textRenderer = ctx.getFont();
    }

    private static final int ENGLISH_FREE_CHARS = 12;

    private static final float X_SCALE_DECREASE_PER_CHAR = 0.025f;

    private static final float MIN_X_SCALE_FACTOR = 0.65f;

    private static final float ENGLISH_BASE_SCALE = 0.025f;

    private static final float CHINESE_BASE_SCALE = 0.035f;

    private static final Map<Direction, Map<String, String>> DIRECTION_MAP = Map.of(
            Direction.NORTH, Map.of(
                    "cnLeft", "西", "cnRight", "东",
                    "enLeft", "W", "enRight", "E",
                    "cnLeftBack", "东", "cnRightBack", "西",
                    "enLeftBack", "E", "enRightBack", "W"
            ),
            Direction.SOUTH, Map.of(
                    "cnLeft", "东", "cnRight", "西",
                    "enLeft", "E", "enRight", "W",
                    "cnLeftBack", "西", "cnRightBack", "东",
                    "enLeftBack", "W", "enRightBack", "E"
            ),
            Direction.WEST, Map.of(
                    "cnLeft", "南", "cnRight", "北",
                    "enLeft", "S", "enRight", "N",
                    "cnLeftBack", "北", "cnRightBack", "南",
                    "enLeftBack", "N", "enRightBack", "S"
            ),
            Direction.EAST, Map.of(
                    "cnLeft", "北", "cnRight", "南",
                    "enLeft", "N", "enRight", "S",
                    "cnLeftBack", "南", "cnRightBack", "北",
                    "enLeftBack", "S", "enRightBack", "N"
            )
    );

    @Override
    public void render(RoadNameSignBlockEntity entity, float tickDelta, PoseStack matrices, MultiBufferSource vertexConsumers, int light, int overlay) {
        String chineseText = entity.getChineseText();
        String englishText = entity.getEnglishText();
        if (chineseText == null || chineseText.isEmpty()) return;
        if (englishText == null || englishText.isEmpty()) return;

        Direction facing = entity.getBlockState().getValue(RoadNameSignBlock.FACING);
        this.currentBlock = entity.getBlockState().getBlock();

        renderText(matrices, vertexConsumers, light, facing, chineseText, true, 4.5f, false, false);
        renderText(matrices, vertexConsumers, light, facing, englishText, false, 0f, false, true);

        renderDirectionText(matrices, vertexConsumers, light, facing, "cnLeft", false, true, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "cnRight", false, false, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enLeft", false, true, false);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enRight", false, false, false);

        renderText(matrices, vertexConsumers, light, facing, chineseText, true, 4.5f, true, false);
        renderText(matrices, vertexConsumers, light, facing, englishText, false, 0f, true, true);

        renderDirectionText(matrices, vertexConsumers, light, facing, "cnLeftBack", true, true, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "cnRightBack", true, false, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enLeftBack", true, true, false);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enRightBack", true, false, false);
    }

    /**
     * 计算 X 方向额外缩放系数。
     * 只对英文小字号生效。
     * 超过 ENGLISH_FREE_CHARS 后，每多 1 个字符，X 缩放直接减少 X_SCALE_DECREASE_PER_CHAR。
     */
    private float getXScaleFactor(String text, boolean isSmallScale) {
        if (!isSmallScale) {
            return 1.0f;
        }

        int extraChars = text.length() - ENGLISH_FREE_CHARS;
        if (extraChars <= 0) {
            return 1.0f;
        }

        float xScale = 1.0f - extraChars * X_SCALE_DECREASE_PER_CHAR;
        return Math.max(xScale, MIN_X_SCALE_FACTOR);
    }

    private void renderText(PoseStack matrices, MultiBufferSource vertexConsumers, int light, Direction facing, String text, boolean isBlack, float andY, boolean backTF, boolean isSmallScale) {
        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
        if (backTF) {
            VersionServices.render().rotateY(matrices, 180);
        }

        float scaleValue = isSmallScale ? ENGLISH_BASE_SCALE : CHINESE_BASE_SCALE;
        float xScaleFactor = getXScaleFactor(text, isSmallScale);

        Component styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true).withFont(VersionServices.resources().create("minecraft", "uniform")));
        int textWidth = this.textRenderer.width(styledText);
        int textHeight = this.textRenderer.lineHeight;
        float zOffset = 1.5f;

        float centeredX = -1 * (textWidth * scaleValue * xScaleFactor) / 2f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset / 16f);

        // 只额外压缩 X 方向，Y / Z 保持原缩放
        matrices.scale(scaleValue * xScaleFactor, -scaleValue, scaleValue);

        int textColor = isSmallScale ? 0X000000 : 0xFFFFFF;

        if (this.currentBlock == MunicipalBlocks.ROAD_NAME_SIGN_RA.get()) {
            textColor = 0xFFFFFF;
        }

        VersionServices.render().drawInBatch(this.textRenderer,
                styledText,
                0,
                -textHeight / 2.0f,
                textColor,
                false,
                matrices,
                vertexConsumers,
                0,
                light
        );

        matrices.popPose();
    }

    private void renderDirectionText(PoseStack matrices, MultiBufferSource vertexConsumers, int light, Direction facing, String directionKey, boolean backTF, boolean leftTF, boolean cnTF) {
        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        VersionServices.render().rotateY(matrices, -facing.toYRot());
        if (backTF) {
            VersionServices.render().rotateY(matrices, 180);
        }

        String directionText = DIRECTION_MAP.get(facing).get(directionKey);
        Component styledText = Text.literal(directionText).setStyle(Style.EMPTY.withBold(true).withFont(VersionServices.resources().create("minecraft", "uniform")));
        int textWidth = this.textRenderer.width(styledText);
        int textHeight = this.textRenderer.lineHeight;
        float zOffset = 1.5f;

        float x = 14f;
        float y;
        int color;
        if (!leftTF) {
            x = -x;
        }
        if (cnTF) {
            y = 4f;
            color = 0xFFFFFF;
        } else {
            y = 0f;
            color = 0x000000;
        }

        float centeredX = x / 16f - (textWidth * 0.02f) / 2f;
        float centeredY = y / 16f;
        matrices.translate(centeredX, centeredY, zOffset / 16f);
        matrices.scale(0.02f, -0.02f, 0.02f);

        if (this.currentBlock == MunicipalBlocks.ROAD_NAME_SIGN_RA.get()) {
            color = 0xFFFFFF;
        }

        VersionServices.render().drawInBatch(this.textRenderer,
                styledText,
                0,
                -textHeight / 2.0f,
                color,
                false,
                matrices,
                vertexConsumers,
                0,
                light
        );

        matrices.popPose();
    }

    public boolean shouldRenderOffScreen(RoadNameSignBlockEntity blockEntity) {
        return true;
    }

    public int getViewDistance() {
        return 256;
    }
}