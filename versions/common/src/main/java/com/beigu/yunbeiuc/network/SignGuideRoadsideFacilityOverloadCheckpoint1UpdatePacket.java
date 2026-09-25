package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.SignGuideRoadsideFacilityOverloadCheckpoint1Entity;
import com.beigu.yunbeiuc.entity.SignGuideRoadsideFacilityOverloadCheckpoint1Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.core.BlockPos;

public class SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket {
    private final BlockPos pos;
    private final SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit unit1;
    private final String length1;

    public SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket(BlockPos pos, SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit unit1, String length1) {
        this.pos = pos;
        this.unit1 = unit1;
        this.length1 = length1;
    }

    public SignGuideRoadsideFacilityOverloadCheckpoint1UpdatePacket(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.unit1 = buf.readEnum(SignGuideRoadsideFacilityOverloadCheckpoint1Entity.Unit.class);
        this.length1 = buf.readUtf();
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeEnum(unit1);
        buf.writeUtf(length1);
    }

    public void apply(ServerPlayer player) {
        if (player.level.hasChunkAt(pos)) {
            BlockEntity blockEntity = player.level.getBlockEntity(pos);
            if (blockEntity instanceof SignGuideRoadsideFacilityOverloadCheckpoint1Entity signEntity) {
                signEntity.setUnit1(unit1);
                signEntity.setLength1(length1);

                signEntity.setChanged();
            }
        }
    }
}
