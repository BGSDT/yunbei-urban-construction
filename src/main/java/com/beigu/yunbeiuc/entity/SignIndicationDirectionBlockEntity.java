package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.SignIndicationDirectionBlock;
import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignIndicationDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignIndicationDirectionBlockEntity extends BlockEntity {
    private SignIndicationDirection directionType = SignIndicationDirection.SIGN_INDICATION_STRAIGHT;

    public SignIndicationDirectionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_INDICATION_DIRECTION_BLOCK_ENTITY, pos, state);
    }

    public SignIndicationDirection getDirectionType() {
        return directionType;
    }

    public void setDirectionType(SignIndicationDirection directionType) {
        this.directionType = directionType;
        this.markDirty();

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignIndicationDirectionBlock) {
                BlockState newState = currentState.with(SignIndicationDirectionBlock.DIRECTION_TYPE, directionType);
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
                this.directionType = SignIndicationDirection.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                this.directionType = SignIndicationDirection.SIGN_INDICATION_STRAIGHT;
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