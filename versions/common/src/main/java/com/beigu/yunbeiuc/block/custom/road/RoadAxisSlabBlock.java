package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class RoadAxisSlabBlock extends AbstractRoadSlabBlock {
    public RoadAxisSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected Kind kind() {
        return Kind.AXIS;
    }
}
