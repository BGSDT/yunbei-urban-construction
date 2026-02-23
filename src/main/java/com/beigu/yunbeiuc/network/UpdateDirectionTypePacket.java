package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.sign.data.SignNoDirection;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateDirectionTypePacket {
    private final BlockPos pos;
    private final SignNoDirection directionType;

    public UpdateDirectionTypePacket(BlockPos pos, SignNoDirection directionType) {
        this.pos = pos;
        this.directionType = directionType;
    }

    public UpdateDirectionTypePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String typeName = buf.readString();
        SignNoDirection tempType;
        try {
            tempType = SignNoDirection.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            tempType = SignNoDirection.SIGN_NO_LEFT_TURN; // 默认值
        }
        this.directionType = tempType; // 确保在所有路径上都初始化
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(directionType.name());
    }

    public void apply(ServerPlayerEntity player) {

        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof com.beigu.yunbeiuc.entity.SignNoDirectionBlockEntity directionBlockEntity) {
                directionBlockEntity.setDirectionType(directionType);

                // 强制同步到所有客户端
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}