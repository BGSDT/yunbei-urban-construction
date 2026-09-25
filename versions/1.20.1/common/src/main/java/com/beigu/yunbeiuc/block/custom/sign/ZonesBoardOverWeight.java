package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.ZonesBoardOverWeightEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ZonesBoardOverWeight extends AbstractEditableSignBlockWithTooltip {
    public ZonesBoardOverWeight(Settings settings) {
        super(settings, "block.yunbeiuc.zones_board.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ZonesBoardOverWeightEntity(pos, state);
    }
}