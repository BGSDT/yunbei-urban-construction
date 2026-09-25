package com.beigu.yunbeiuc.entity;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.NotNull;

public class GantryFrameLedEntity extends CustomSignBlockEntity {

    public GantryFrameLedEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return ModBlockEntities.GANTRY_FRAME_LED_ENTITY.get();
    }
}