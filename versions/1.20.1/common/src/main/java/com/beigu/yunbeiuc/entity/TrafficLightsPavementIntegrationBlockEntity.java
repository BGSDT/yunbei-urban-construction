package com.beigu.yunbeiuc.entity;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;

/**
 * 人行道一体化红绿灯BlockEntity
 * 继承原本的TrafficLightsBlockEntity，复用所有功能
 */
public class TrafficLightsPavementIntegrationBlockEntity extends TrafficLightsBlockEntity {

    public TrafficLightsPavementIntegrationBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
        // 人行道一体化红绿灯默认为人行道模式（显示人行图标）
        this.setDirectionType(DirectionType.NON_MOTOR_VEHICLES);
    }

    @Override
    public net.minecraft.block.entity.BlockEntityType<?> getType() {
        return ModBlockEntities.TRAFFIC_LIGHTS_PAVEMENT_INTEGRATION_BLOCK_ENTITY.get();
    }
}
