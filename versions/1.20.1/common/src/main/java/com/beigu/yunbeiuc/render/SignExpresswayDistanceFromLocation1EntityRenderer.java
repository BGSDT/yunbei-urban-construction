package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation1;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation1Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignExpresswayDistanceFromLocation1EntityRenderer extends BaseSignRenderer<SignExpresswayDistanceFromLocation1Entity> {

    public SignExpresswayDistanceFromLocation1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayDistanceFromLocation1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
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

        Direction facing = entity.getCachedState().get(SignExpresswayDistanceFromLocation1.FACING);
        SignExpresswayDistanceFromLocation1.Type type = entity.getCachedState().get(SignExpresswayDistanceFromLocation1.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        // 左侧文本
        renderLeftAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -15f, 9f, 0.04f, 0xFFFFFF);
        renderLeftAlignedText(matrices, vertexConsumers, light, facing, text2, signType, -15f, 0f, 0.04f, 0xFFFFFF);
        renderLeftAlignedText(matrices, vertexConsumers, light, facing, text3, signType, -15f, -9f, 0.04f, 0xFFFFFF);

        // 右侧数字
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length1, signType, 11f, 9f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length2, signType, 11f, 0f, 0.04f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length3, signType, 11f, -9f, 0.04f, 0xFFFFFF);

        // km 单位
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 15f, 8.5f, 0.025f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 15f, -0.5f, 0.025f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 15f, -9.5f, 0.025f, 0xFFFFFF);
    }
}