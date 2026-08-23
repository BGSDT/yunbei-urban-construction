package com.beigu.yunbeiuc.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
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