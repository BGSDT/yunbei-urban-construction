package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.sign.data.SignCancelSpeedLimit;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateCancelSpeedLimitPacket {
    private final BlockPos pos;
    private final SignCancelSpeedLimit cancelSpeedLimit;

    public UpdateCancelSpeedLimitPacket(BlockPos pos, SignCancelSpeedLimit cancelSpeedLimit) {
        this.pos = pos;
        this.cancelSpeedLimit = cancelSpeedLimit;
    }

    public UpdateCancelSpeedLimitPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String limitName = buf.readString();
        SignCancelSpeedLimit tempLimit;
        try {
            tempLimit = SignCancelSpeedLimit.valueOf(limitName);
        } catch (IllegalArgumentException e) {
            tempLimit = SignCancelSpeedLimit.CANCEL_SPEED_LIMIT_005; // 默认值
        }
        this.cancelSpeedLimit = tempLimit; // 确保在所有路径上都初始化
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(cancelSpeedLimit.name());
    }

    public void apply(ServerPlayerEntity player) {

        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof com.beigu.yunbeiuc.entity.SignCancelSpeedLimitBlockEntity cancelSpeedLimitBlockEntity) {
                cancelSpeedLimitBlockEntity.setCancelSpeedLimit(cancelSpeedLimit);

                // 强制同步到所有客户端
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}