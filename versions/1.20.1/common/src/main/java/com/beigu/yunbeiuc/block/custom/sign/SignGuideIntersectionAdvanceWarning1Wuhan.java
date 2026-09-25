package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarning1WuhanEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarning1Wuhan extends AbstractEditableSignBlockWithTooltip {
    public SignGuideIntersectionAdvanceWarning1Wuhan(Settings settings) {
        super(settings, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignGuideIntersectionAdvanceWarning1WuhanEntity(pos, state);
    }
}
