package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignGuideRoadsideFacilityOverloadCheckpoint1Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SignGuideRoadsideFacilityOverloadCheckpoint1 extends AbstractEditableSignBlockWithTooltip {
    public SignGuideRoadsideFacilityOverloadCheckpoint1(BlockBehaviour.Properties properties) {
        super(properties, "block.yunbeiuc.sign_guide_roadside_facility_overload_checkpoint_1.tooltip");
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new SignGuideRoadsideFacilityOverloadCheckpoint1Entity(pos, state);
    }
}
