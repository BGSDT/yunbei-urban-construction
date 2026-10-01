package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.ZonesBoardOverWeightEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ZonesBoardOverWeight extends AbstractEditableSignBlockWithTooltip {
    public ZonesBoardOverWeight(BlockBehaviour.Properties properties) {
        super(properties, "block.yunbeiuc.zones_board.tooltip");
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new ZonesBoardOverWeightEntity(pos, state);
    }
}
