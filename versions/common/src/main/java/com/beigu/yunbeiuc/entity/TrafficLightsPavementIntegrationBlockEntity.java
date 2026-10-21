package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;

// 人行道一体化红绿灯方块实体：继承 TrafficLightsBlockEntity，复用全部功能。

public class TrafficLightsPavementIntegrationBlockEntity extends TrafficLightsBlockEntity {

    public TrafficLightsPavementIntegrationBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        this.setDirectionType(DirectionType.NON_MOTOR_VEHICLES);
    }

    @Override
    public net.minecraft.world.level.block.entity.BlockEntityType<?> getType() {
        return ModBlockEntities.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLOCK_ENTITY.get();
    }
}
