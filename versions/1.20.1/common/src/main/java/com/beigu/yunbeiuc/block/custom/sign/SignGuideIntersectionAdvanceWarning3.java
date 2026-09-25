package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning3Entity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning3 extends AbstractEditableSignBlockWithTooltip {
    public SignGuideIntersectionAdvanceWarning3(Settings settings) {
        super(settings, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignGuideIntersectionAdvanceWarning3Entity(pos, state);
    }
}
