package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignCancelSpeedLimit;
import com.beigu.yunbeiuc.block.custom.sign.abandoned.SignCancelSpeedLimitBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SignCancelSpeedLimitBlockEntity extends BlockEntity {
    private SignCancelSpeedLimit cancelSpeedLimit = SignCancelSpeedLimit.CANCEL_SPEED_LIMIT_005;

    public SignCancelSpeedLimitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_CANCEL_SPEED_LIMIT_BLOCK_ENTITY, pos, state);
    }

    public SignCancelSpeedLimit getCancelSpeedLimit() {
        return cancelSpeedLimit;
    }

    public void setCancelSpeedLimit(SignCancelSpeedLimit cancelSpeedLimit) {
        this.cancelSpeedLimit = cancelSpeedLimit;
        this.markDirty();

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignCancelSpeedLimitBlock) {
                BlockState newState = currentState.with(SignCancelSpeedLimitBlock.CANCEL_SPEED_LIMIT, cancelSpeedLimit);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        String limitName = nbt.getString("CancelSpeedLimit");
        try {
            this.cancelSpeedLimit = SignCancelSpeedLimit.valueOf(limitName);
        } catch (IllegalArgumentException e) {
            this.cancelSpeedLimit = SignCancelSpeedLimit.CANCEL_SPEED_LIMIT_005;
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("CancelSpeedLimit", this.cancelSpeedLimit.name());
    }

    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}