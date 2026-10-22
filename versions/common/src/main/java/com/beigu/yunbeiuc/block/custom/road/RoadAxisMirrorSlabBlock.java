package com.beigu.yunbeiuc.block.custom.road;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class RoadAxisMirrorSlabBlock extends AbstractRoadSlabBlock implements AxisMirrorOriented {
    private final Mode mode;

    public RoadAxisMirrorSlabBlock(BlockBehaviour.Properties properties, Mode mode) {
        super(properties, mode);
        this.mode = mode;
    }

    @Override
    protected Kind kind() {
        return Kind.AXIS_MIRRORED;
    }

    @Override
    public Mode axisMirrorMode() {
        return mode;
    }
}
