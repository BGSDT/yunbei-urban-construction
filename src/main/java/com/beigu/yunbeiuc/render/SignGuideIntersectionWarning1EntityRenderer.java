package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning1;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning4;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning1Entity;
import net.minecraft.block.Block;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3f;

import java.util.Map;

public class SignGuideIntersectionWarning1EntityRenderer implements BlockEntityRenderer<SignGuideIntersectionWarning1Entity> {
    private final TextRenderer textRenderer;

    public SignGuideIntersectionWarning1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        this.textRenderer = ctx.getTextRenderer();
    }

    @Override
    public void render(SignGuideIntersectionWarning1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionWarning1.FACING);
        SignGuideIntersectionWarning1.Type type = entity.getCachedState().get(SignGuideIntersectionWarning1.TYPE);
        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_1 || currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_6){
            renderText(matrices, vertexConsumers, light, facing, text1, type, 0f, 0f);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_2){
            renderText(matrices, vertexConsumers, light, facing, text1, type, -2.5f, 0f);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_3){
            renderText(matrices, vertexConsumers, light, facing, text1, type, 0f, 0f);
        }
    }

    private void renderText(MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, Direction facing, String text, SignGuideIntersectionWarning1.Type type, float andX, float andY) {
        matrices.push();

        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(new Quaternion(Vec3f.POSITIVE_Y, -facing.asRotation(), true));

        float scaleValue = 0.035f;

        Text styledText = new LiteralText(text).setStyle(Style.EMPTY.withBold(true));
        int textWidth = this.textRenderer.getWidth(styledText);
        int textHeight = this.textRenderer.fontHeight;

        float zOffset = switch (type) {
            case POLE_L -> -0.75f;
            case POLE_H -> -0.79f;
            case NORMAL -> -0.43f;
        };

        float centeredX = andX / 16f - (textWidth * scaleValue) / 2f;
        float centeredY = andY / 16f;
        matrices.translate(centeredX, centeredY, zOffset);

        matrices.scale(scaleValue, -scaleValue, scaleValue);

        this.textRenderer.draw(
                styledText,
                0,
                -textHeight / 2.0f,
                0xFFFFFF,
                false,
                matrices.peek().getPositionMatrix(),
                vertexConsumers,
                false,
                0,
                light
        );

        matrices.pop();
    }

    @Override
    public boolean rendersOutsideBoundingBox(SignGuideIntersectionWarning1Entity blockEntity) {
        return true;
    }

    @Override
    public int getRenderDistance() {
        return 256;
    }
}