package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.block.custom.sign.data.SignGuideIntersectionAdvanceWarning;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.block.BlockState;
import net.minecraft.block.Block;

public class UpdateWarningTypePacket {
    private final BlockPos pos;
    private final SignGuideIntersectionAdvanceWarning warningType;

    public UpdateWarningTypePacket(BlockPos pos, SignGuideIntersectionAdvanceWarning warningType) {
        this.pos = pos;
        this.warningType = warningType;
    }

    public UpdateWarningTypePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        String typeName = buf.readString();
        SignGuideIntersectionAdvanceWarning tempType;
        try {
            tempType = SignGuideIntersectionAdvanceWarning.valueOf(typeName);
        } catch (IllegalArgumentException e) {
            tempType = SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1;
        }
        this.warningType = tempType;
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeString(warningType.name());
    }

    public void apply(ServerPlayerEntity player) {
        if (player == null || !player.isAlive()) {
            return;
        }

        ServerWorld world = player.getServerWorld();
        if (world.isChunkLoaded(pos)) {
            var blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof com.beigu.yunbeiuc.entity.SignGuideIntersectionAdvanceWarningBlockEntity warningBlockEntity) {
                warningBlockEntity.setWarningType(warningType);
                BlockState state = world.getBlockState(pos);
                world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
            }
        }
    }
}