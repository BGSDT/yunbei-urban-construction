package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignExpresswayExit4Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SignExpresswayExit4 extends AbstractEditableSignBlockWithTooltip {
    public SignExpresswayExit4(BlockBehaviour.Properties properties) {
        super(properties, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new SignExpresswayExit4Entity(pos, state);
    }
}
