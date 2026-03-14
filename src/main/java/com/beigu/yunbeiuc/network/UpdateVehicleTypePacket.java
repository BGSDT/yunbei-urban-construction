package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignNoEntryForVehicles;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateVehicleTypePacket {
    private final BlockPos pos;
    private final SignNoEntryForVehicles vehicleType;

    public UpdateVehicleTypePacket(BlockPos pos, SignNoEntryForVehicles vehicleType) {
        this.pos = pos;
        this.vehicleType = vehicleType;
    }

    public UpdateVehicleTypePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String typeName = buf.readString();
        SignNoEntryForVehicles tempType;
        try {
            tempType = SignNoEntryForVehicles.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            tempType = SignNoEntryForVehicles.SIGN_NO_ENTRY; // 默认值
        }
        this.vehicleType = tempType; // 确保在所有路径上都初始化
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(vehicleType.name());
    }

    public void apply(ServerPlayerEntity player) {

        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof com.beigu.yunbeiuc.entity.SignNoEntryForVehiclesBlockEntity vehicleBlockEntity) {
                vehicleBlockEntity.setVehicleType(vehicleType);

                // 强制同步到所有客户端
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}