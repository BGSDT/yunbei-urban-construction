package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayRoadName;
import com.beigu.yunbeiuc.entity.SignExpresswayRoadNameEntity;
import com.beigu.yunbeiuc.render.base.BaseSignRenderer;
import com.beigu.yunbeiuc.render.base.SignTypeConverter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignExpresswayRoadNameEntityRenderer extends BaseSignRenderer<SignExpresswayRoadNameEntity> {

    public SignExpresswayRoadNameEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayRoadNameEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayRoadName.FACING);
        SignExpresswayRoadName.Type type = entity.getCachedState().get(SignExpresswayRoadName.TYPE);

        SignType signType = SignTypeConverter.convert(type);
        renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 0f, 0.045f, 0xFFFFFF);
    }
}