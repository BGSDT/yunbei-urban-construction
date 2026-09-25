package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.ZonesBoardImageEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;

public class ZonesBoardImage extends AbstractEditableSignBlockReflective {
    public ZonesBoardImage(Settings settings) {
        super(settings, "block.yunbeiuc.zones_board.tooltip");
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ZonesBoardImageEntity(pos, state);
    }
}
