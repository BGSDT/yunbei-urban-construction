package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SignGuideIntersectionAdvanceWarning1Wuhan extends AbstractEditableSignBlockWithTooltip {
    public SignGuideIntersectionAdvanceWarning1Wuhan(BlockBehaviour.Properties properties) {
        super(properties, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new SignGuideIntersectionAdvanceWarning1WuhanEntity(pos, state);
    }
}
