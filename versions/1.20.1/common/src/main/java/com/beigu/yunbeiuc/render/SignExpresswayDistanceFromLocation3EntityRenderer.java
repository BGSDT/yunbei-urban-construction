package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDistanceFromLocation3;
import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation3Entity;
import com.beigu.yunbeiuc.render.base.BaseSignRenderer;
import com.beigu.yunbeiuc.render.base.SignTypeConverter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignExpresswayDistanceFromLocation3EntityRenderer extends BaseSignRenderer<SignExpresswayDistanceFromLocation3Entity> {

    public SignExpresswayDistanceFromLocation3EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayDistanceFromLocation3Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayDistanceFromLocation3.FACING);
        SignExpresswayDistanceFromLocation3.Type type = entity.getCachedState().get(SignExpresswayDistanceFromLocation3.TYPE);

        SignType signType = SignTypeConverter.convert(type);
        renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 5.5f, 0.045f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text2 + "个出口", signType, 0f, -5.5f, 0.045f, 0xFFFFFF);
    }
}