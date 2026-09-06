package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayEntranceAdvance7;
import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance7Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignExpresswayEntranceAdvance7EntityRenderer extends BaseSignRenderer<SignExpresswayEntranceAdvance7Entity> {

    public SignExpresswayEntranceAdvance7EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayEntranceAdvance7Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayEntranceAdvance7.FACING);
        SignExpresswayEntranceAdvance7.Type type = entity.getCachedState().get(SignExpresswayEntranceAdvance7.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 8f, 0.035f, 0x2D9B47);
        renderCenteredText(matrices, vertexConsumers, light, facing, text2, signType, -7f, -2f, 0.035f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text3, signType, 7f, -2f, 0.035f, 0xFFFFFF);
    }
}
