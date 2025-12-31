package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.data.SignWeightLimit;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateWeightLimitPacket {
    private final BlockPos pos;
    private final SignWeightLimit weightLimit;

    public UpdateWeightLimitPacket(BlockPos pos, SignWeightLimit weightLimit) {
        this.pos = pos;
        this.weightLimit = weightLimit;
    }

    public UpdateWeightLimitPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String limitName = buf.readString();
        SignWeightLimit tempLimit;
        try {
            tempLimit = SignWeightLimit.valueOf(limitName);
        } catch (IllegalArgumentException e) {
            tempLimit = SignWeightLimit.SIGN_WEIGHT_LIMIT_10; // 默认值
        }
        this.weightLimit = tempLimit; // 确保在所有路径上都初始化
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(weightLimit.name());
    }

    public void apply(ServerPlayerEntity player) {

        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof com.beigu.yunbeiuc.entity.SignWeightLimitBlockEntity weightLimitBlockEntity) {
                weightLimitBlockEntity.setWeightLimit(weightLimit);

                // 强制同步到所有客户端
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}