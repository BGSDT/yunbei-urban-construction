package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.SignNoDirectionBlock;
import com.beigu.yunbeiuc.block.custom.sign.data.SignNoDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignNoDirectionBlockEntity extends BlockEntity {
    private SignNoDirection directionType = SignNoDirection.SIGN_NO_LEFT_TURN;

    public SignNoDirectionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_NO_DIRECTION_BLOCK_ENTITY, pos, state);
    }

    public SignNoDirection getDirectionType() {
        return directionType;
    }

    public void setDirectionType(SignNoDirection directionType) {
        this.directionType = directionType;
        this.markDirty();

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignNoDirectionBlock) {
                BlockState newState = currentState.with(SignNoDirectionBlock.DIRECTION_TYPE, directionType);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("DirectionType")) {
            String typeName = nbt.getString("DirectionType");
            try {
                this.directionType = SignNoDirection.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                this.directionType = SignNoDirection.SIGN_NO_LEFT_TURN;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("DirectionType", this.directionType.name());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}