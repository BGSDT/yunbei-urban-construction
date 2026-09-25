package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation4Entity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayDistanceFromLocation4 extends AbstractEditableSignBlockWithTooltip {
    public SignExpresswayDistanceFromLocation4(Settings settings) {
        super(settings, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignExpresswayDistanceFromLocation4Entity(pos, state);
    }
}
