package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.RoadNameSignBlock;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1Wuhan;
import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;

import java.util.Map;

public class RoadNameSignBlockEntityRenderer implements BlockEntityRenderer<RoadNameSignBlockEntity> {
    private final TextRenderer textRenderer;
    private Block currentBlock;

    public RoadNameSignBlockEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
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
    public void render(RoadNameSignBlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String chineseText = entity.getChineseText();
        String englishText = entity.getEnglishText();
        if (chineseText == null || chineseText.isEmpty()) return;
        if (englishText == null || englishText.isEmpty()) return;

        Direction facing = entity.getCachedState().get(RoadNameSignBlock.FACING);
        this.currentBlock = entity.getCachedState().getBlock();

        renderText(matrices, vertexConsumers, light, facing, chineseText, true, 4.5f, false, false);
        renderText(matrices, vertexConsumers, light, facing, chineseText, true, 4.5f, true, false);
        renderText(matrices, vertexConsumers, light, facing, englishText, false, 0f, false, true);
        renderText(matrices, vertexConsumers, light, facing, englishText, false, 0f, true, true);

        renderDirectionText(matrices, vertexConsumers, light, facing, "cnLeft", false, true, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "cnRight", false, false, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enLeft", false, true, false);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enRight", false, false, false);
        renderDirectionText(matrices, vertexConsumers, light, facing, "cnLeftBack", true, true, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "cnRightBack", true, false, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enLeftBack", true, true, false);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enRightBack", true, false, false);
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, boolean isBlack, float andY, boolean backTF, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        if (backTF) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }

        float scaleValue = isSmallScale ? 0.025f : 0.035f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;
        float zOffset = 1.5f;

        float centeredX = -1 * (textWidth * scaleValue) / 2f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset / 16f);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        int textColor = isSmallScale ? 0X000000 : 0xFFFFFF;

        if(this.currentBlock == MunicipalBlocks.ROAD_NAME_SIGN_RA.get()){
            textColor = 0xFFFFFF;
        }

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                textColor,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.pop();
    }

    private void renderDirectionText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String directionKey, boolean backTF, boolean leftTF,  boolean cnTF) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        if (backTF) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
        String directionText = DIRECTION_MAP.get(facing).get(directionKey);
        Text styledText = Text.literal(directionText).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;
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
    public boolean rendersOutsideBoundingBox(RoadNameSignBlockEntity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}