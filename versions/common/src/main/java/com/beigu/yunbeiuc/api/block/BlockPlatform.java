package com.beigu.yunbeiuc.api.block;

import com.beigu.yunbeiuc.api.mapper.EntityBlockCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

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

    @Nullable
    default BlockEntity ensureBlockEntity(Level level, BlockPos pos) {
        BlockEntity existing = level.getBlockEntity(pos);
        if (existing != null) {
            return existing;
        }
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof EntityBlockCompat entityBlock)) {
            return null;
        }
        BlockEntity created = entityBlock.createBlockEntityCompat(pos, state);
        if (created != null) {
            setLevelBlockEntity(level, pos, created);
        }
        return created;
    }

    void setLevelBlockEntity(Level level, BlockPos pos, BlockEntity blockEntity);

    void setProtoChunkBlockEntity(ProtoChunk chunk, BlockEntity blockEntity);

    int chunkMinY(ChunkAccess chunk);

    boolean isSectionEmpty(LevelChunkSection section);
}
