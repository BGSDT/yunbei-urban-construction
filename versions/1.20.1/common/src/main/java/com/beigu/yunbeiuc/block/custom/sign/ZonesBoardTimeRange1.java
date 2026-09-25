package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange1Entity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ZonesBoardTimeRange1 extends AbstractEditableSignBlockWithTooltip {
    public ZonesBoardTimeRange1(Settings settings) {
        super(settings, "block.yunbeiuc.zones_board.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ZonesBoardTimeRange1Entity(pos, state);
    }
}
