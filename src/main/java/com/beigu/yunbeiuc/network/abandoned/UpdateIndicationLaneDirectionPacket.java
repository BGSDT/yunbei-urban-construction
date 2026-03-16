package com.beigu.yunbeiuc.network.abandoned;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignIndicationLaneDirection;
import com.beigu.yunbeiuc.entity.abandoned.SignIndicationLaneDirectionBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateIndicationLaneDirectionPacket {
    private final BlockPos pos;
    private final SignIndicationLaneDirection laneDirectionType;

    public UpdateIndicationLaneDirectionPacket(BlockPos pos, SignIndicationLaneDirection laneDirectionType) {
        this.pos = pos;
        this.laneDirectionType = laneDirectionType;
    }

    public UpdateIndicationLaneDirectionPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String typeName = buf.readString();
        SignIndicationLaneDirection tempType;
        try {
            tempType = SignIndicationLaneDirection.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            tempType = SignIndicationLaneDirection.SIGN_INDICATION_LANE_DIRECTION_STRAIGHT; // 默认值
        }
        this.laneDirectionType = tempType;
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(laneDirectionType.name());
    }

    public void apply(ServerPlayerEntity player) {
        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof SignIndicationLaneDirectionBlockEntity laneDirectionBlockEntity) {
                laneDirectionBlockEntity.setLaneDirectionType(laneDirectionType);

                // 强制同步到所有客户端
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}