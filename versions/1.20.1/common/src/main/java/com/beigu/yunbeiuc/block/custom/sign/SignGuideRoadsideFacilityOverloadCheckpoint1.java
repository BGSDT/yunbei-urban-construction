package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignGuideRoadsideFacilityOverloadCheckpoint1Entity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideRoadsideFacilityOverloadCheckpoint1 extends AbstractEditableSignBlockWithTooltip {
    public SignGuideRoadsideFacilityOverloadCheckpoint1(Settings settings) {
        super(settings, "block.yunbeiuc.sign_guide_roadside_facility_overload_checkpoint_1.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignGuideRoadsideFacilityOverloadCheckpoint1Entity(pos, state);
    }
}
