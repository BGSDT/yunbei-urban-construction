package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;
public class RoadWithAutoRightangleLine extends RoadWithAutoLine {

    public RoadWithAutoRightangleLine(BlockBehaviour.Properties properties) {
        super(properties, RoadAutoLineType.RIGHT_ANGLE);
    }
}
