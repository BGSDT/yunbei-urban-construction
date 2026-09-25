package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignExpresswayNamingNumberEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class SignExpresswayNamingNumber extends AbstractEditableSignBlockWithTooltip {
    public SignExpresswayNamingNumber(Settings settings) {
        super(settings, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignExpresswayNamingNumberEntity(pos, state);
    }
}
