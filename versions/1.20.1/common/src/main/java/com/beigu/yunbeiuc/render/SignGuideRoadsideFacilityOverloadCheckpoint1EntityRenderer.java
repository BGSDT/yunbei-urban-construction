package com.beigu.yunbeiuc.render;

import com.beigu.yunbeiuc.block.custom.sign.SignGuideRoadsideFacilityOverloadCheckpoint1;
import com.beigu.yunbeiuc.entity.SignGuideRoadsideFacilityOverloadCheckpoint1Entity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;

public class SignGuideRoadsideFacilityOverloadCheckpoint1EntityRenderer extends BaseSignRenderer<SignGuideRoadsideFacilityOverloadCheckpoint1Entity> {

    public SignGuideRoadsideFacilityOverloadCheckpoint1EntityRenderer(BlockEntityRendererFactory.Context ctx) {
        super(ctx.getTextRenderer());
    }

    @Override
    public void render(SignGuideRoadsideFacilityOverloadCheckpoint1Entity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit unit1 = entity.getUnit1();
        String length1 = entity.getLength1();

        if (length1 == null || length1.isEmpty()) length1 = " ";

        String unit1_number = switch (unit1){
            case KILOMETRE -> "km";
            case METRE -> "m";
        };

        Direction facing = entity.getCachedState().get(SignGuideRoadsideFacilityOverloadCheckpoint1.FACING);
        SignGuideRoadsideFacilityOverloadCheckpoint1.Type type = entity.getCachedState().get(SignGuideRoadsideFacilityOverloadCheckpoint1.TYPE);

        SignType signType = SignTypeConverter.convert(type);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, length1, signType, -1.5f, -13f, 0.045f, 0xFFFFFF);
        renderRightAlignedText(matrices, vertexConsumers, light, facing, unit1_number, signType, 2.5f, -13.5f, 0.03f, 0xFFFFFF);
    }
}