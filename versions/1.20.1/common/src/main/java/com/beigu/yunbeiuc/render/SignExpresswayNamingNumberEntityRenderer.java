package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayNamingNumber;
import com.beigu.yunbeiuc.entity.SignExpresswayNamingNumberEntity;
import com.beigu.yunbeiuc.render.base.BaseSignRenderer;
import com.beigu.yunbeiuc.render.base.SignTypeConverter;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignExpresswayNamingNumberEntityRenderer extends BaseSignRenderer<SignExpresswayNamingNumberEntity> {

    public SignExpresswayNamingNumberEntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayNamingNumberEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String expresswayNumber = entity.getExpresswayNumber();
        String expresswayName = entity.getExpresswayName();

        if (expresswayNumber == null || expresswayNumber.isEmpty()) expresswayNumber = " ";
        if (expresswayName == null || expresswayName.isEmpty()) expresswayName = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayNamingNumber.FACING);
        SignExpresswayNamingNumber.Type type = entity.getCachedState().get(SignExpresswayNamingNumber.TYPE);

        SignType signType = SignTypeConverter.convert(type);
        renderCenteredText(matrices, vertexConsumers, light, facing, expresswayNumber, signType, 0f, 1.5f, 0.08f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, insertSpaceBetweenChars(expresswayName), signType, 0f, -6.5f, 0.02f, 0xFFFFFF);
    }

    public static String insertSpaceBetweenChars(String str) {
        if (str == null || str.isEmpty() || str.equals(" ")) {
            return str;
        }
        return str.replaceAll(".(?!$)", "$0 ");
    }
}