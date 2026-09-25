package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.ZonesBoardTimeRange2Entity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ZonesBoardTimeRange2 extends AbstractEditableSignBlockWithTooltip {
    public ZonesBoardTimeRange2(Settings settings) {
        super(settings, "block.yunbeiuc.zones_board.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ZonesBoardTimeRange2Entity(pos, state);
    }
}
