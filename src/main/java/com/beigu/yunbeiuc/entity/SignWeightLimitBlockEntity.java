package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.SignWeightLimitBlock;
import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignWeightLimit;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignWeightLimitBlockEntity extends BlockEntity {
    private SignWeightLimit weightLimit = SignWeightLimit.SIGN_WEIGHT_LIMIT_10; // 默认重量限制

    public SignWeightLimitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_WEIGHT_LIMIT_BLOCK_ENTITY, pos, state);
    }

    public SignWeightLimit getWeightLimit() {
        return weightLimit;
    }

    public void setWeightLimit(SignWeightLimit weightLimit) {
        this.weightLimit = weightLimit;
        this.markDirty(); // 标记为脏数据以触发保存[citation:1]

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignWeightLimitBlock) {
                BlockState newState = currentState.with(SignWeightLimitBlock.WEIGHT_LIMIT, weightLimit);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("WeightLimit")) {
            String limitName = nbt.getString("WeightLimit");
            try {
                this.weightLimit = SignWeightLimit.valueOf(limitName);
            } catch (IllegalArgumentException e) {
                this.weightLimit = SignWeightLimit.SIGN_WEIGHT_LIMIT_10;
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