package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning7Entity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning7 extends AbstractEditableSignBlockWithTooltip {
    public SignGuideIntersectionAdvanceWarning7(Settings settings) {
        super(settings, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignGuideIntersectionAdvanceWarning7Entity(pos, state);
    }
}
