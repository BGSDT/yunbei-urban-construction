package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;
public class RoadSlabWithAutoRightangleLine extends RoadSlabWithAutoLine {

    public RoadSlabWithAutoRightangleLine(BlockBehaviour.Properties properties) {
        super(properties, RoadWithAutoLine.RoadAutoLineType.RIGHT_ANGLE);
    }
}
