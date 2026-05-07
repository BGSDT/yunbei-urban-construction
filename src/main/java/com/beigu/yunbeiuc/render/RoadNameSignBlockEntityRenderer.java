package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.custom.RoadNameSignBlock;
import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
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

        renderRoadName(matrices, vertexConsumers, light, facing, chineseText, englishText, false);
        renderRoadName(matrices, vertexConsumers, light, facing, chineseText, englishText, true);

        renderDirectionText(matrices, vertexConsumers, light, facing, "cnLeft", false, true, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "cnRight", false, false, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enLeft", false, true, false);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enRight", false, false, false);
        renderDirectionText(matrices, vertexConsumers, light, facing, "cnLeftBack", true, true, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "cnRightBack", true, false, true);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enLeftBack", true, true, false);
        renderDirectionText(matrices, vertexConsumers, light, facing, "enRightBack", true, false, false);
    }

    private void renderRoadName(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String chineseText, String englishText, boolean backTF) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        if (backTF) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
        matrices.translate(0.0, 0.0, 0.1);
        matrices.scale(0.035f, -0.035f, 0.035f);

        int chineseTextWidth = this.textRenderer.getWidth(chineseText);
        int chineseTextHeight = this.textRenderer.fontHeight;
        float cx = -chineseTextWidth / 2.0f;
        float cy = -chineseTextHeight / 2.0f;

        this.textRenderer.draw(
                Text.literal(chineseText),
                cx,
                cy - 8,
                0xFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                TextRenderer.TextLayerType.NORMAL,
                0,
                light
        );

        matrices.scale(0.4f, 0.4f, 0.4f);

        int englishTextWidth = this.textRenderer.getWidth(englishText);
        int englishTextHeight = this.textRenderer.fontHeight;
        float ex = -englishTextWidth / 2.0f;
        float ey = -englishTextHeight / 2.0f;

        int color = 0X000000;

        if(this.currentBlock == MunicipalBlocks.ROAD_NAME_SIGN_RA){
            color = 0xFFFFFF;
        }

        this.textRenderer.draw(
                Text.literal(englishText),
                ex,
                ey + 2,
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

    private void renderDirectionText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String directionKey, boolean backTF, boolean leftTF,  boolean cnTF) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        if (backTF) {
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180));
        }
        matrices.translate(0.0, 0.0, 0.1);
        matrices.scale(0.02f, -0.02f, 0.02f);

        int x = 45;
        int y;
        int color;
        if (!leftTF) {
            x = -x;
        }

        if (cnTF) {
            y = -15;
            color = 0xFFFFFF;
        }else{
            y = -3;
            color = 0x000000;
        }

        if(this.currentBlock == MunicipalBlocks.ROAD_NAME_SIGN_RA){
            color = 0xFFFFFF;
        }

        String directionText = DIRECTION_MAP.get(facing).get(directionKey);
        if (directionText != null) {
            this.textRenderer.draw(
                    Text.literal(directionText),
                    x,
                    y,
                    color,
                    false,
                    matrices.peek().getPositionMatrix(),
                    vertexConsumers,
                    TextRenderer.TextLayerType.NORMAL,
                    0,
                    light
            );
        }

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