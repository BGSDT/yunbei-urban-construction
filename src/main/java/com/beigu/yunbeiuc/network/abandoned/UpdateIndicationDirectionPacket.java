package com.beigu.yunbeiuc.network.abandoned;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignIndicationDirection;
import com.beigu.yunbeiuc.entity.abandoned.SignIndicationDirectionBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateIndicationDirectionPacket {
    private final BlockPos pos;
    private final SignIndicationDirection directionType;

    public UpdateIndicationDirectionPacket(BlockPos pos, SignIndicationDirection directionType) {
        this.pos = pos;
        this.directionType = directionType;
    }

    public UpdateIndicationDirectionPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String typeName = buf.readString();
        SignIndicationDirection tempType;
        try {
            tempType = SignIndicationDirection.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            tempType = SignIndicationDirection.SIGN_INDICATION_STRAIGHT; // 默认值
        }
        this.directionType = tempType;
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

            if (blockEntity instanceof SignIndicationDirectionBlockEntity directionBlockEntity) {
                directionBlockEntity.setDirectionType(directionType);

                // 强制同步到所有客户端
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}