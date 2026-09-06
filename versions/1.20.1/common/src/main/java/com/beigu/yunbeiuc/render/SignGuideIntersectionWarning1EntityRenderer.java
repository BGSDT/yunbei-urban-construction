package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.block.SignBlocks;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionWarning1;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning1Entity;
import net.minecraft.block.Block;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignGuideIntersectionWarning1EntityRenderer extends BaseSignRenderer<SignGuideIntersectionWarning1Entity> {

    public SignGuideIntersectionWarning1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignGuideIntersectionWarning1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        String text1 = entity.getText1();

        if (text1 == null || text1.isEmpty()) text1 = " ";

        Block currentBlock = entity.getCachedState().getBlock();

        Direction facing = entity.getCachedState().get(SignGuideIntersectionWarning1.FACING);
        SignGuideIntersectionWarning1.Type type = entity.getCachedState().get(SignGuideIntersectionWarning1.TYPE);

        SignType signType = SignTypeConverter.convert(type);

        if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_1.get() || currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_6.get()){
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 0f, 0.035f, 0xFFFFFF);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_2.get()){
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, -2.5f, 0f, 0.035f, 0xFFFFFF);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_INTERSECTION_WARNING_3.get()){
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 0f, 0.035f, 0xFFFFFF);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_1.get() || currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_2.get() || currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_3.get()){
            renderRightAlignedText(matrices, vertexConsumers, light, facing, text1, signType, 5f, 0f, 0.045f, 0x275aa8);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 9f, -0.5f, 0.03f, 0x275aa8);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_4.get() || currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_5.get() || currentBlock == SignBlocks.SIGN_GUIDE_DISTANCE_TO_TUNNEL_EXIT_6.get()){
            renderRightAlignedText(matrices, vertexConsumers, light, facing, text1, signType, -0.5f, -4f, 0.045f, 0x275aa8);
            renderRightAlignedText(matrices, vertexConsumers, light, facing, "km", signType, 3.5f, -4.5f, 0.03f, 0x275aa8);
        }else if(currentBlock == SignBlocks.SIGN_GUIDE_ODOMETER.get()){
            renderCenteredText(matrices, vertexConsumers, light, facing, text1, signType, 0f, 3f, 0.055f, 0xFFFFFF);
        }
    }
}
