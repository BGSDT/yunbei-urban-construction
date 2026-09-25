package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignExpresswayEntranceAdvance4Entity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayEntranceAdvance4 extends AbstractEditableSignBlockWithTooltip {
    public SignExpresswayEntranceAdvance4(Settings settings) {
        super(settings, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignExpresswayEntranceAdvance4Entity(pos, state);
    }
}
