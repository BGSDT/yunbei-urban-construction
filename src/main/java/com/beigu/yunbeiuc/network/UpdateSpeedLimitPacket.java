package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.data.SignSpeedLimit;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateSpeedLimitPacket {
    private final BlockPos pos;
    private final SignSpeedLimit speedLimit;

    public UpdateSpeedLimitPacket(BlockPos pos, SignSpeedLimit speedLimit) {
        this.pos = pos;
        this.speedLimit = speedLimit;
    }

    public UpdateSpeedLimitPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String limitName = buf.readString();
        SignSpeedLimit tempLimit;
        try {
            tempLimit = SignSpeedLimit.valueOf(limitName);
        } catch (IllegalArgumentException e) {
            tempLimit = SignSpeedLimit.SPEED_LIMIT_005; // 默认值
        }
        this.speedLimit = tempLimit; // 确保在所有路径上都初始化
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(speedLimit.name());
    }

    public void apply(ServerPlayerEntity player) {

        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof com.beigu.yunbeiuc.entity.SignSpeedLimitBlockEntity speedLimitBlockEntity) {
                speedLimitBlockEntity.setSpeedLimit(speedLimit);

                // 强制同步到所有客户端
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}