package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.sign.data.SignNoSpecial;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateNoSpecialTypePacket {
    private final BlockPos pos;
    private final SignNoSpecial noSpecialType;

    public UpdateNoSpecialTypePacket(BlockPos pos, SignNoSpecial noSpecialType) {
        this.pos = pos;
        this.noSpecialType = noSpecialType;
    }

    public UpdateNoSpecialTypePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String typeName = buf.readString();
        SignNoSpecial tempType;
        try {
            tempType = SignNoSpecial.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            tempType = SignNoSpecial.SIGN_STOP;
        }
        this.noSpecialType = tempType;
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(noSpecialType.name());
    }

    public void apply(ServerPlayerEntity player) {
        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);

            if (blockEntity instanceof com.beigu.yunbeiuc.entity.SignNoSpecialBlockEntity noSpecialBlockEntity) {
                noSpecialBlockEntity.setNoSpecialType(noSpecialType);

                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}