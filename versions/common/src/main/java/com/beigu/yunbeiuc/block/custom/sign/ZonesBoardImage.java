package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.entity.ZonesBoardImageEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class ZonesBoardImage extends AbstractEditableSignBlockReflective {
    public ZonesBoardImage(BlockBehaviour.Properties properties) {
        super(properties, "block.yunbeiuc.zones_board.tooltip");
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new ZonesBoardImageEntity(pos, state);
    }
}
