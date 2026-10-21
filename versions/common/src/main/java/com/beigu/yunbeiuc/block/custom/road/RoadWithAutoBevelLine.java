package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;
public class RoadWithAutoBevelLine extends RoadWithAutoLine {

    public RoadWithAutoBevelLine(BlockBehaviour.Properties properties) {
        super(properties, RoadAutoLineType.BEVEL);
    }
}
