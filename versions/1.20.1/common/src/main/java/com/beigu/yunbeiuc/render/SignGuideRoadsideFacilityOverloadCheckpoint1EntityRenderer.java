package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideRoadsideFacilityOverloadCheckpoint1;
import com.beigu.yunbeiuc.entity.SignGuideRoadsideFacilityOverloadCheckpoint1Entity;
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

public class SignGuideRoadsideFacilityOverloadCheckpoint1EntityRenderer implements BlockEntityRenderer<SignGuideRoadsideFacilityOverloadCheckpoint1Entity> {
    private final TextRenderer textRenderer;

    public SignGuideRoadsideFacilityOverloadCheckpoint1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SignGuideRoadsideFacilityOverloadCheckpoint1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit unit1 = entity.getUnit1();
        String length1 = entity.getLength1();

        if (length1 == null || length1.isEmpty()) length1 = " ";

        String unit1_number = switch (unit1){
            case KILOMETRE -> "km";
            case METRE -> "m";
        };

        Direction facing = entity.getCachedState().get(SignGuideRoadsideFacilityOverloadCheckpoint1.FACING);
        SignGuideRoadsideFacilityOverloadCheckpoint1.Type type = entity.getCachedState().get(SignGuideRoadsideFacilityOverloadCheckpoint1.TYPE);

        renderText(matrices, vertexConsumers, light, facing, length1, type, -1.5f, -13f, false);
        renderText(matrices, vertexConsumers, light, facing, unit1_number, type, 2.5f, -13.5f, true);
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideRoadsideFacilityOverloadCheckpoint1.Type type, float andX, float andY, boolean isSmallScale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));

        float scaleValue = isSmallScale ? 0.03f : 0.045f;

        Text styledText = Text.literal(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        float centeredX = andX / 16f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                -textWidth,
                -textHeight / 2.0f,
                0XFFFFFF,
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
    public boolean rendersOutsideBoundingBox(SignGuideRoadsideFacilityOverloadCheckpoint1Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}