package com.beigu.yunbeiuc.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

public class RoadPoleLedEntity extends CustomSignBlockEntity {

    public RoadPoleLedEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return ModBlockEntities.ROAD_POLE_LED_ENTITY.get();
    }
}