package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.ZonesBoard1Entity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ZonesBoard1 extends AbstractEditableSignBlockWithTooltip {
    public ZonesBoard1(Settings settings) {
        super(settings, "block.yunbeiuc.zones_board.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ZonesBoard1Entity(pos, state);
    }
}
