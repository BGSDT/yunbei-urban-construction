package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignHeightLimit;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateHeightLimitPacket {
    private final BlockPos pos;
    private final SignHeightLimit heightLimit;

    public UpdateHeightLimitPacket(BlockPos pos, SignHeightLimit heightLimit) {
        this.pos = pos;
        this.heightLimit = heightLimit;
    }

    public UpdateHeightLimitPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String limitName = buf.readString();
        SignHeightLimit tempLimit;
        try {
            tempLimit = SignHeightLimit.valueOf(limitName);
        } catch (IllegalArgumentException e) {
            tempLimit = SignHeightLimit.HEIGHT_LIMIT_20; // 默认值
        }
        this.heightLimit = tempLimit; // 确保在所有路径上都初始化
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(heightLimit.name());
    }

    public void apply(ServerPlayerEntity player) {

        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof com.beigu.yunbeiuc.entity.SignHeightLimitBlockEntity heightLimitBlockEntity) {
                heightLimitBlockEntity.setHeightLimit(heightLimit);

                // 强制同步到所有客户端
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}