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

    /**
     * 把某个方块实体的最新状态推给一个玩家。
     *
     * <p>1.16.5 / 1.17.1 的 {@code ClientboundBlockEntityDataPacket} 在客户端只对原版白名单
     * 类型调用 {@code load}，模组方块实体的数据会被整包丢弃，所以这两个版本改为重发所在区块，
     * 让客户端重建方块实体；1.18.2+ 直接发更新包即可。
     */
    void sendBlockEntityUpdate(ServerPlayer player, ServerLevel level, BlockPos pos,
                               ClientboundBlockEntityDataPacket packet);

}
