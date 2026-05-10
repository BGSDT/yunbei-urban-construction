package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning3;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning3Entity;
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

public class SignGuideIntersectionAdvanceWarning3EntityRenderer implements BlockEntityRenderer<SignGuideIntersectionAdvanceWarning3Entity> {
    private final TextRenderer textRenderer;

    public SignGuideIntersectionAdvanceWarning3EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SignGuideIntersectionAdvanceWarning3Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String cnText2 = entity.getCnText2();
        String enText2 = entity.getEnText2();
        String cnText3 = entity.getCnText3();
        String enText3 = entity.getEnText3();
        String cnText4 = entity.getCnText4();
        String enText4 = entity.getEnText4();
        String cnText5 = entity.getCnText5();
        String enText5 = entity.getEnText5();
        String cnText6 = entity.getCnText6();
        String enText6 = entity.getEnText6();
        String cnText7 = entity.getCnText7();
        String enText7 = entity.getEnText7();

        if (text1 == null || text1.isEmpty()) {
            text1 = " ";
        }
        if (cnText2 == null || cnText2.isEmpty()) {
            cnText2 = " ";
        }
        if (enText2 == null || enText2.isEmpty()) {
            enText2 = " ";
        }
        if (cnText3 == null || cnText3.isEmpty()) {
            cnText3 = " ";
        }
        if (enText3 == null || enText3.isEmpty()) {
            enText3 = " ";
        }
        if (cnText4 == null || cnText4.isEmpty()) {
            cnText4 = " ";
        }
        if (enText4 == null || enText4.isEmpty()) {
            enText4 = " ";
        }
        if (cnText5 == null || cnText5.isEmpty()) {
            cnText5 = " ";
        }
        if (enText5 == null || enText5.isEmpty()) {
            enText5 = " ";
        }
        if (cnText6 == null || cnText6.isEmpty()) {
            cnText6 = " ";
        }
        if (enText6 == null || enText6.isEmpty()) {
            enText6 = " ";
        }
        if (cnText7 == null || cnText7.isEmpty()) {
            cnText7 = " ";
        }
        if (enText7 == null || enText7.isEmpty()) {
            enText7 = " ";
        }

        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning3.FACING);
        SignGuideIntersectionAdvanceWarning3.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning3.TYPE);
        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3) {
            renderText(matrices, vertexConsumers, light, facing, cnText2, type, 0, -18, false);
            renderText(matrices, vertexConsumers, light, facing, enText2, type, 0, -15, true);
            renderText(matrices, vertexConsumers, light, facing, cnText4, type, -30, 10, false);
            renderText(matrices, vertexConsumers, light, facing, enText4, type, -42, 22, true);
            renderText(matrices, vertexConsumers, light, facing, cnText5, type, -30, -5, false);
            renderText(matrices, vertexConsumers, light, facing, enText5, type, -42, 3, true);
            renderText(matrices, vertexConsumers, light, facing, cnText6, type, 30, 10, false);
            renderText(matrices, vertexConsumers, light, facing, enText6, type, 36, 22, true);
            renderText(matrices, vertexConsumers, light, facing, cnText7, type, 30, -5, false);
            renderText(matrices, vertexConsumers, light, facing, enText7, type, 36, 3, true);
        }else {
            renderText(matrices, vertexConsumers, light, facing, text1, type, 0, 9, false);
            renderText(matrices, vertexConsumers, light, facing, cnText2, type, 0, -24, false);
            renderText(matrices, vertexConsumers, light, facing, enText2, type, 0, -21, true);
            renderText(matrices, vertexConsumers, light, facing, cnText3, type, 0, -28, false);
            renderText(matrices, vertexConsumers, light, facing, enText3, type, 0, -27, true);
            renderText(matrices, vertexConsumers, light, facing, cnText4, type, -30, 0, false);
            renderText(matrices, vertexConsumers, light, facing, enText4, type, -42, 8, true);
            renderText(matrices, vertexConsumers, light, facing, cnText5, type, -30, -15, false);
            renderText(matrices, vertexConsumers, light, facing, enText5, type, -42, -9, true);
            renderText(matrices, vertexConsumers, light, facing, cnText6, type, 30, 0, false);
            renderText(matrices, vertexConsumers, light, facing, enText6, type, 36, 8, true);
            renderText(matrices, vertexConsumers, light, facing, cnText7, type, 30, -15, false);
            renderText(matrices, vertexConsumers, light, facing, enText7, type, 36, -9, true);
        }
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionAdvanceWarning3.Type type, int andX, int andY, boolean scale) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(-facing.asRotation()));
        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };
        matrices.translate(0.0, 0.0, zOffset);
        float scale1;
        if(scale) {
            scale1 = 0.023f;
        } else {
            scale1 = 0.03f;
        }
        matrices.scale(scale1, -scale1, scale1);

        int textWidth = this.textRenderer.getWidth(text);
        int textHeight = this.textRenderer.fontHeight;
        float cx = -textWidth / 2.0f;
        float cy = -textHeight / 2.0f;

        this.textRenderer.draw(
                Text.literal(text).setStyle(Style.EMPTY.withBold(true)),
                cx + andX,
                cy + andY,
                0xFFFFFF,
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
    public boolean rendersOutsideBoundingBox(SignGuideIntersectionAdvanceWarning3Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}