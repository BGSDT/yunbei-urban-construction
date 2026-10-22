package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class RoadFacingSlabBlock extends AbstractRoadSlabBlock {
    public RoadFacingSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected Kind kind() {
        return Kind.FACING;
    }
}
