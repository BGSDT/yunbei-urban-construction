package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideRoadsideFacilityOverloadCheckpoint1Entity;
import com.beigu.yunbeiuc.entity.SignGuideRoadsideFacilityOverloadCheckpoint1Entity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket {
    private final BlockPos pos;
    private final SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit unit1;
    private final String length1;

    public SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket(BlockPos pos, SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit unit1, String length1) {
        this.pos = pos;
        this.unit1 = unit1;
        this.length1 = length1;
    }

    public SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.unit1 = buf.readEnumConstant(SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit.class);
        this.length1 = buf.readString();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnumConstant(unit1);
        buf.writeString(length1);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            BlockEntity blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof SignGuideRoadsideFacilityOverloadCheckpoint1Entity signEntity) {
                signEntity.setUnit1(unit1);
                signEntity.setLength1(length1);

                signEntity.markDirty();
            }
        }
    }
}