package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideConfirmation1;
import com.beigu.yunbeiuc.entity.SignGuideConfirmation1Entity;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignGuideConfirmation1EntityRenderer extends BaseSignRenderer<SignGuideConfirmation1Entity> {

    public SignGuideConfirmation1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignGuideConfirmation1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideConfirmation1Entity.Unit unit1 = entity.getUnit1();
        SignGuideConfirmation1Entity.Unit unit2 = entity.getUnit2();
        SignGuideConfirmation1Entity.Unit unit3 = entity.getUnit3();
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();
        String length1 = entity.getLength1();
        String length2 = entity.getLength2();
        String length3 = entity.getLength3();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";
        if (length1 == null || length1.isEmpty()) length1 = " ";
        if (length2 == null || length2.isEmpty()) length2 = " ";
        if (length3 == null || length3.isEmpty()) length3 = " ";

        String unit1_number = unit1 == SignGuideConfirmation1Entity.Unit.KILOMETRE ? "km" : "m";
        String unit2_number = unit2 == SignGuideConfirmation1Entity.Unit.KILOMETRE ? "km" : "m";
        String unit3_number = unit3 == SignGuideConfirmation1Entity.Unit.KILOMETRE ? "km" : "m";

        Direction facing = entity.getCachedState().get(SignGuideConfirmation1.FACING);
        SignGuideConfirmation1.Type type = entity.getCachedState().get(SignGuideConfirmation1.TYPE);
        Block currentBlock = entity.getCachedState().getBlock();

        SignType signType = SignTypeConverter.convert(type);

        if(currentBlock == SignBlocks.SIGN_GUIDE_CONFIRMATION_1.get()){
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -17f, 9f, 0.04f, 0xFFFFFF);
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text2, signType, -17f, 0f, 0.04f, 0xFFFFFF);
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text3, signType, -17f, -9f, 0.04f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, length1, signType, 13f, 9f, 0.04f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, length2, signType, 13f, 0f, 0.04f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, length3, signType, 13f, -9f, 0.04f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, unit1_number, signType, 17f, 8.5f, 0.025f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, unit2_number, signType, 17f, -0.5f, 0.025f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, unit3_number, signType, 17f, -9.5f, 0.025f, 0xFFFFFF);
        }else{
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -15f, 9f, 0.04f, 0xFFFFFF);
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text2, signType, -15f, 0f, 0.04f, 0xFFFFFF);
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text3, signType, -15f, -9f, 0.04f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, length1, signType, 11f, 9f, 0.04f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, length2, signType, 11f, 0f, 0.04f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, length3, signType, 11f, -9f, 0.04f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, unit1_number, signType, 15f, 8.5f, 0.025f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, unit2_number, signType, 15f, -0.5f, 0.025f, 0xFFFFFF);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, unit3_number, signType, 15f, -9.5f, 0.025f, 0xFFFFFF);
        }
    }
}