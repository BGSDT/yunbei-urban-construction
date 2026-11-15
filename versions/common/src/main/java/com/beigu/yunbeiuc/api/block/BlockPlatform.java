package com.beigu.yunbeiuc.api.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public interface BlockPlatform {
    boolean isWater(FluidState state);

    void scheduleBlockTick(LevelAccessor level, BlockPos pos, Block block, int delay);

    void scheduleFluidTick(LevelAccessor level, BlockPos pos, Fluid fluid, int delay);

    boolean growOakTree(ServerLevel level, BlockPos pos);

    BlockBehaviour.Properties color(BlockBehaviour.Properties properties, BlockColor color);

    BlockBehaviour.Properties copyProperties(Block source);

    int updateAll();

    int updateImmediate();

    int updateNeighbors();

    int updateClients();

    void sendBlockEntityUpdate(ServerPlayer player, ServerLevel level, BlockPos pos,
                               ClientboundBlockEntityDataPacket packet);

    default void beginRegistration(String name) {
    }

    default void endRegistration() {
    }
}
