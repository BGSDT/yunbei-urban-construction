package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.RoadNameSignBlock;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1Wuhan;
import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import com.mojang.math.Vector3f;

import java.util.Map;

public class RoadNameSignBlockEntityRenderer implements BlockEntityRenderer<RoadNameSignBlockEntity> {
    private final Font textRenderer;
    private Block currentBlock;

    public RoadNameSignBlockEntityRenderer(BlockEntityRendererProvider.Context ctx) {
        this.textRenderer = ctx.getFont();
    }

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

    private void renderText(PoseStack matrices, MultiBufferSource vertexConsumers, int light, Direction facing, String text, boolean isBlack, float andY, boolean backTF, boolean isSmallScale) {
        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
        if (backTF) {
            matrices.mulPose(Vector3f.YP.rotationDegrees(180));
        }

        float scaleValue = isSmallScale ? 0.025f : 0.035f;

        Component styledText = new TextComponent(text).setStyle(Style.EMPTY.withBold(true).withFont(new ResourceLocation("minecraft", "uniform")));
        int textWidth = this.textRenderer.width(styledText);
        int textHeight = this.textRenderer.lineHeight;
        float zOffset = 1.5f;

        float centeredX = -1 * (textWidth * scaleValue) / 2f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset / 16f);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        int textColor = isSmallScale ? 0X000000 : 0xFFFFFF;

        if(this.currentBlock == MunicipalBlocks.ROAD_NAME_SIGN_RA.get()){
            textColor = 0xFFFFFF;
        }

        this.textRenderer.drawInBatch(
                styledText,
                0,
                -textHeight / 2.0f,
                textColor,
                false,
                matrices.last().pose(),
                vertexConsumers,
                false,
                0,
                light
        );

        matrices.popPose();
    }

    private void renderDirectionText(PoseStack matrices, MultiBufferSource vertexConsumers, int light, Direction facing, String directionKey, boolean backTF, boolean leftTF,  boolean cnTF) {
        matrices.pushPose();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.mulPose(Vector3f.YP.rotationDegrees(-facing.toYRot()));
        if (backTF) {
            matrices.mulPose(Vector3f.YP.rotationDegrees(180));
        }
        String directionText = DIRECTION_MAP.get(facing).get(directionKey);
        Component styledText = new TextComponent(directionText).setStyle(Style.EMPTY.withBold(true).withFont(new ResourceLocation("minecraft", "uniform")));
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
        }else{
            y = 0f;
            color = 0x000000;
        }
        float centeredX = x / 16f - (textWidth * 0.02f) / 2f;
        float centeredY = y / 16f;
        matrices.translate(centeredX, centeredY, zOffset / 16f);
        matrices.scale(0.02f, -0.02f, 0.02f);

        if(this.currentBlock == MunicipalBlocks.ROAD_NAME_SIGN_RA.get()){
            color = 0xFFFFFF;
        }

        this.textRenderer.drawInBatch(
                styledText,
                0,
                -textHeight / 2.0f,
                color,
                false,
                matrices.last().pose(),
                vertexConsumers,
                false,
                0,
                light
        );

        matrices.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(RoadNameSignBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}
