package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class RoadPlainSlabBlock extends AbstractRoadSlabBlock {
    public RoadPlainSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected Kind kind() {
        return Kind.NONE;
    }
}
