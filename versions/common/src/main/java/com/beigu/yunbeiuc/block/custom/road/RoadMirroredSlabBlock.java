package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class RoadMirroredSlabBlock extends AbstractRoadSlabBlock {
    public RoadMirroredSlabBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected Kind kind() {
        return Kind.MIRRORED;
    }
}
