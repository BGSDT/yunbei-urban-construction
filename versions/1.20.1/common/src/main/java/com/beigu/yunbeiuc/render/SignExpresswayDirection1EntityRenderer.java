package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignExpresswayDirection1;
import com.beigu.yunbeiuc.entity.SignExpresswayDirection1Entity;
import com.beigu.yunbeiuc.render.base.BaseSignRenderer;
import com.beigu.yunbeiuc.render.base.SignTypeConverter;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignExpresswayDirection1EntityRenderer extends BaseSignRenderer<SignExpresswayDirection1Entity> {

    public SignExpresswayDirection1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignExpresswayDirection1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Direction facing = entity.getCachedState().get(SignExpresswayDirection1.FACING);
        SignExpresswayDirection1.Type type = entity.getCachedState().get(SignExpresswayDirection1.TYPE);
        Block currentBlock = entity.getCachedState().getBlock();

        SignType signType = SignTypeConverter.convert(type);

        if (currentBlock == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_1.get()){
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 4.5f, 0f, 0.05f, 0xFFFFFF);
        } else if (currentBlock == SignBlocks.SIGN_EXPRESSWAY_DIRECTION_2.get()) {
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -4.5f, 0f, 0.05f, 0xFFFFFF);
        }
    }
}