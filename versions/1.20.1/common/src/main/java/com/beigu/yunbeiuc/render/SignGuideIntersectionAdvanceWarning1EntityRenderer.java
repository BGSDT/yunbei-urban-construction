package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarning1;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1Entity;
import com.beigu.yunbeiuc.render.base.BaseSignRenderer;
import com.beigu.yunbeiuc.render.base.SignTypeConverter;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignGuideIntersectionAdvanceWarning1EntityRenderer extends BaseSignRenderer<SignGuideIntersectionAdvanceWarning1Entity> {

    public SignGuideIntersectionAdvanceWarning1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignGuideIntersectionAdvanceWarning1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();
        String text2 = entity.getText2();

        if (text1 == null || text1.isEmpty()) text1 = " ";
        if (text2 == null || text2.isEmpty()) text2 = " ";
        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1.FACING);
        SignGuideIntersectionAdvanceWarning1.Type type = entity.getCachedState().get(SignGuideIntersectionAdvanceWarning1.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_2.get()){
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -17f, 10f, 0.035f, 0xFFFFFF);
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text2, signType, 3f, -1f, 0.035f, 0xFFFFFF);
        }else {
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -12f, 12f, 0.035f, 0xFFFFFF);
            renderLeftAlignedText(matrices, vertexConsumers, light, facing, text2, signType, -5f, -7f, 0.035f, 0xFFFFFF);
        }
    }
}