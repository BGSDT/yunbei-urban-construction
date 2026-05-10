package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1Entity;
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

public class SignGuideIntersectionAdvanceWarning1EntityRenderer implements BlockEntityRenderer<SignGuideIntersectionAdvanceWarning1Entity> {
    private final TextRenderer textRenderer;

    public SignGuideIntersectionAdvanceWarning1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SignGuideIntersectionAdvanceWarning1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();

        if (text1 == null || text1.isEmpty()) {
            text1 = " ";
        }
        if (text2 == null || text2.isEmpty()) {
            text2 = " ";
        }
        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1.FACING);
        SignGuideIntersectionAdvanceWarning1.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1.TYPE);

        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2){
            renderText(matrices, vertexConsumers, light, facing, text1, type, -30, -18);
            renderText(matrices, vertexConsumers, light, facing, text2, type, 5,  3);
        }else {
            renderText(matrices, vertexConsumers, light, facing, text1, type, -20, -20);
            renderText(matrices, vertexConsumers, light, facing, text2, type, -10, 15);
        }
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionAdvanceWarning1.Type type, int andX, int andY) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };
        matrices.translate(0.0, 0.0, zOffset);
        float scale = 0.035f;
        matrices.scale(scale, -scale, scale);
        int color = 0xFFFFFF;

        int textHeight = this.textRenderer.fontHeight;
        float cy = -textHeight / 2.0f;

        this.textRenderer.draw(
                Text.literal(text).setStyle(Style.EMPTY.withBold(true)),
                andX,
                cy + andY,
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
    public boolean rendersOutsideBoundingBox(SignGuideIntersectionAdvanceWarning1Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}