package com.beigu.yunbeiuc.entity.abandoned;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.SignHeightLimitBlock;
import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignHeightLimit;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignHeightLimitBlockEntity extends BlockEntity {
    private SignHeightLimit heightLimit = SignHeightLimit.HEIGHT_LIMIT_20;

    public SignHeightLimitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_HEIGHT_LIMIT_BLOCK_ENTITY, pos, state);
    }

    public SignHeightLimit getHeightLimit() {
        return heightLimit;
    }

    public void setHeightLimit(SignHeightLimit heightLimit) {
        this.heightLimit = heightLimit;
        this.markDirty();

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignHeightLimitBlock) {
                BlockState newState = currentState.with(SignHeightLimitBlock.HEIGHT_LIMIT, heightLimit);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("HeightLimit")) {
            String limitName = nbt.getString("HeightLimit");
            try {
                this.heightLimit = SignHeightLimit.valueOf(limitName);
            } catch (IllegalArgumentException e) {
                this.heightLimit = SignHeightLimit.HEIGHT_LIMIT_20;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}