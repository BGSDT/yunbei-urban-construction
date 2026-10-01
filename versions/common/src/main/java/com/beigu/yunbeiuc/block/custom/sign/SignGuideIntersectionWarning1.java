package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning1Entity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SignGuideIntersectionWarning1 extends AbstractEditableSignBlockWithTooltip {
    public SignGuideIntersectionWarning1(BlockBehaviour.Properties properties) {
        super(properties, "block.yunbeiuc.sign_text.tooltip");
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new SignGuideIntersectionWarning1Entity(pos, state);
    }
}
