package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning3Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SignGuideIntersectionAdvanceWarning3 extends AbstractEditableSignBlockWithTooltip {
    public SignGuideIntersectionAdvanceWarning3(BlockBehaviour.Properties properties) {
        super(properties, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new SignGuideIntersectionAdvanceWarning3Entity(pos, state);
    }
}
