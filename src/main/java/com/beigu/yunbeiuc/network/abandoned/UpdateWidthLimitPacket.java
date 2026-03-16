package com.beigu.yunbeiuc.network.abandoned;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignWidthLimit;
import com.beigu.yunbeiuc.entity.abandoned.SignWidthLimitBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateWidthLimitPacket {
    private final BlockPos pos;
    private final SignWidthLimit widthLimit;

    public UpdateWidthLimitPacket(BlockPos pos, SignWidthLimit widthLimit) {
        this.pos = pos;
        this.widthLimit = widthLimit;
    }

    public UpdateWidthLimitPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String limitName = buf.readString();
        SignWidthLimit tempLimit;
        try {
            tempLimit = SignWidthLimit.valueOf(limitName);
        } catch (IllegalArgumentException e) {
            tempLimit = SignWidthLimit.SIGN_WIDTH_LIMIT_20; // 默认值
        }
        this.widthLimit = tempLimit; // 确保在所有路径上都初始化
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(widthLimit.name());
    }

    public void apply(ServerPlayerEntity player) {

        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof SignWidthLimitBlockEntity widthLimitBlockEntity) {
                widthLimitBlockEntity.setWidthLimit(widthLimit);

                // 强制同步到所有客户端
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}