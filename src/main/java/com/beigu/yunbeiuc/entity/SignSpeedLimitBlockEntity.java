package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.SignSpeedLimitBlock;
import com.beigu.yunbeiuc.block.custom.data.SignSpeedLimit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignSpeedLimitBlockEntity extends BlockEntity {
    private SignSpeedLimit speedLimit = SignSpeedLimit.SPEED_LIMIT_005;

    public SignSpeedLimitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_SPEED_LIMIT_BLOCK_ENTITY, pos, state);
    }

    public SignSpeedLimit getSpeedLimit() {
        return speedLimit;
    }

    public void setSpeedLimit(SignSpeedLimit speedLimit) {
        this.speedLimit = speedLimit;
        this.markDirty();

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignSpeedLimitBlock) {
                BlockState newState = currentState.with(SignSpeedLimitBlock.SPEED_LIMIT, speedLimit);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("SpeedLimit")) {
            String limitName = nbt.getString("SpeedLimit");
            try {
                this.speedLimit = SignSpeedLimit.valueOf(limitName);
            } catch (IllegalArgumentException e) {
                this.speedLimit = SignSpeedLimit.SPEED_LIMIT_005;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("SpeedLimit", this.speedLimit.name());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}