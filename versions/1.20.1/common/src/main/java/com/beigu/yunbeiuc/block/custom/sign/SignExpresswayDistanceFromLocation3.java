package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignExpresswayDistanceFromLocation3Entity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayDistanceFromLocation3 extends AbstractEditableSignBlockWithTooltip {
    public SignExpresswayDistanceFromLocation3(Settings settings) {
        super(settings, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignExpresswayDistanceFromLocation3Entity(pos, state);
    }
}
