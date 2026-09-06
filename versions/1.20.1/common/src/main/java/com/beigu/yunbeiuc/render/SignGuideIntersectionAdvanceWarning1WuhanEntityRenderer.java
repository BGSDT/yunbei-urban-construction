package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1Wuhan;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer extends BaseSignRenderer<SignGuideIntersectionAdvanceWarning1WuhanEntity> {

    public SignGuideIntersectionAdvanceWarning1WuhanEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignGuideIntersectionAdvanceWarning1WuhanEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String cnText3 = entity.getCnText3();
        String enText3 = entity.getEnText3();
        String cnText4 = entity.getCnText4();
        String enText4 = entity.getEnText4();
        String cnText5 = entity.getCnText5();
        String enText5 = entity.getEnText5();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (cnText3 == null || cnText3.isEmpty()) cnText3 = " ";
        if (enText3 == null || enText3.isEmpty()) enText3 = " ";
        if (cnText4 == null || cnText4.isEmpty()) cnText4 = " ";
        if (enText4 == null || enText4.isEmpty()) enText4 = " ";
        if (cnText5 == null || cnText5.isEmpty()) cnText5 = " ";
        if (enText5 == null || enText5.isEmpty()) enText5 = " ";
        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1Wuhan.FACING);
        SignGuideIntersectionAdvanceWarning1Wuhan.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1Wuhan.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1_WUHAN_RIGHT.get()){
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 16.5f, 12f, 0.023f, 0x275aa8);
            renderCenteredText(matrices, vertexConsumers, light, facing, text2, signType, 16.5f, -12f, 0.023f, 0x275aa8);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText3, signType, -6f, 12f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText3, signType, -6f, 8f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText4, signType, -6f, 1f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText4, signType, -6f, -3f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText5, signType, -6f, -10f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText5, signType, -6f, -14f, 0.023f, 0xFFFFFF);
        }else {
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -16.5f, 12f, 0.023f, 0x275aa8);
            renderCenteredText(matrices, vertexConsumers, light, facing, text2, signType, -16.5f, -12f, 0.023f, 0x275aa8);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText3, signType, 6f, 12f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText3, signType, 6f, 8f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText4, signType, 6f, 1f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText4, signType, 6f, -3f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText5, signType, 6f, -10f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText5, signType, 6f, -14f, 0.023f, 0xFFFFFF);
        }
    }
}
