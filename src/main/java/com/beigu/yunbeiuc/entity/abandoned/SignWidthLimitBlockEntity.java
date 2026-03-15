package com.beigu.yunbeiuc.entity.abandoned;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.SignWidthLimitBlock;
import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignWidthLimit;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignWidthLimitBlockEntity extends BlockEntity {
    private SignWidthLimit widthLimit = SignWidthLimit.SIGN_WIDTH_LIMIT_20;

    public SignWidthLimitBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_WIDTH_LIMIT_BLOCK_ENTITY, pos, state);
    }

    public SignWidthLimit getWidthLimit() {
        return widthLimit;
    }

    public void setWidthLimit(SignWidthLimit widthLimit) {
        this.widthLimit = widthLimit;
        this.markDirty();

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignWidthLimitBlock) {
                BlockState newState = currentState.with(SignWidthLimitBlock.WIDTH_LIMIT, widthLimit);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        if (nbt.contains("WidthLimit")) {
            String limitName = nbt.getString("WidthLimit");
            try {
                this.widthLimit = SignWidthLimit.valueOf(limitName);
            } catch (IllegalArgumentException e) {
                this.widthLimit = SignWidthLimit.SIGN_WIDTH_LIMIT_20;
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