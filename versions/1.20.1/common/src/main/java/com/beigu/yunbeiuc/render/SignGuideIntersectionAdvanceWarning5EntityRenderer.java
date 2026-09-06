package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning5;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning5Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignGuideIntersectionAdvanceWarning5EntityRenderer extends BaseSignRenderer<SignGuideIntersectionAdvanceWarning5Entity> {

    public SignGuideIntersectionAdvanceWarning5EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignGuideIntersectionAdvanceWarning5Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();
        String text3 = entity.getText3();
        String text4 = entity.getText4();
        Float text1AndY = entity.getText1AndY();
        Float text2AndY = entity.getText2AndY();
        Float text3AndY = entity.getText3AndY();
        Float text4AndY = entity.getText4AndY();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        if (text3 == null || text3.isEmpty()) text3 = " ";
        if (text4 == null || text4.isEmpty()) text4 = " ";

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning5.FACING);
        SignGuideIntersectionAdvanceWarning5.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning5.TYPE);

        SignType signType = SignTypeConverter.convert(type);
        renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -10f, 10f + text1AndY, 0.03f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text2, signType, 10f, 10f + text2AndY, 0.03f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text3, signType, -10f, -10f + text3AndY, 0.03f, 0xFFFFFF);
        renderCenteredText(matrices, vertexConsumers, light, facing, text4, signType, 10f, -10f + text4AndY, 0.03f, 0xFFFFFF);
    }
}