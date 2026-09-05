package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning3;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning3Entity;
import com.beigu.yunbeiuc.render.base.BaseSignRenderer;
import com.beigu.yunbeiuc.render.base.SignTypeConverter;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignGuideIntersectionAdvanceWarning3EntityRenderer extends BaseSignRenderer<SignGuideIntersectionAdvanceWarning3Entity> {

    public SignGuideIntersectionAdvanceWarning3EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
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

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (cnText2 == null || cnText2.isEmpty()) cnText2 = " ";
        if (enText2 == null || enText2.isEmpty()) enText2 = " ";
        if (cnText3 == null || cnText3.isEmpty()) cnText3 = " ";
        if (enText3 == null || enText3.isEmpty()) enText3 = " ";
        if (cnText4 == null || cnText4.isEmpty()) cnText4 = " ";
        if (enText4 == null || enText4.isEmpty()) enText4 = " ";
        if (cnText5 == null || cnText5.isEmpty()) cnText5 = " ";
        if (enText5 == null || enText5.isEmpty()) enText5 = " ";
        if (cnText6 == null || cnText6.isEmpty()) cnText6 = " ";
        if (enText6 == null || enText6.isEmpty()) enText6 = " ";
        if (cnText7 == null || cnText7.isEmpty()) cnText7 = " ";
        if (enText7 == null || enText7.isEmpty()) enText7 = " ";

        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning3.FACING);
        SignGuideIntersectionAdvanceWarning3.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning3.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_3.get()) {
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText2, signType, 0f, 8f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText2, signType, 0f, 4f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText4, signType, -14f, 4f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText4, signType, -14f, 0f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText5, signType, -14f, -4f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText5, signType, -14f, -8f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText6, signType, 14f, 4f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText6, signType, 14f, 0f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText7, signType, 14f, -4f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText7, signType, 14f, -8f, 0.023f, 0xFFFFFF);
        }else {
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, -9f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText2, signType, 0f, 6f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText2, signType, 0f, 3f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText3, signType, 0f, 13f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText3, signType, 0f, 10f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText4, signType, -14f, 8f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText4, signType, -14f, 4f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText5, signType, -14f, 0f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText5, signType, -14f, -4f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText6, signType, 14f, 8f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText6, signType, 14f, 4f, 0.023f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, cnText7, signType, 14f, 0f, 0.03f, 0xFFFFFF);
            renderCenteredText(matrices, vertexConsumers, light, facing, enText7, signType, 14f, -4f, 0.023f, 0xFFFFFF);
        }
    }
}