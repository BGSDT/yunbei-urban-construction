package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;
public class RoadSlabWithAutoBevelLine extends RoadSlabWithAutoLine {

    public RoadSlabWithAutoBevelLine(BlockBehaviour.Properties properties) {
        super(properties, RoadWithAutoLine.RoadAutoLineType.BEVEL);
    }
}
