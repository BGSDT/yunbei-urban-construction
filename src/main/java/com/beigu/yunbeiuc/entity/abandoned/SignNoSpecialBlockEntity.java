package com.beigu.yunbeiuc.entity.abandoned;

import com.beigu.yunbeiuc.block.custom.sign.abandoned.SignNoSpecialBlock;
import com.beigu.yunbeiuc.block.custom.sign.abandoned.data.SignNoSpecial;
import com.beigu.yunbeiuc.entity.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignNoSpecialBlockEntity extends BlockEntity {
    private SignNoSpecial noSpecialType = SignNoSpecial.SIGN_STOP;

    public SignNoSpecialBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_NO_SPECIAL_BLOCK_ENTITY, pos, state);
    }

    public SignNoSpecial getNoSpecialType() {
        return noSpecialType;
    }

    public void setNoSpecialType(SignNoSpecial noSpecialType) {
        this.noSpecialType = noSpecialType;
        this.markDirty();

        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignNoSpecialBlock) {
                BlockState newState = currentState.with(SignNoSpecialBlock.NO_SPECIAL_TYPE, noSpecialType);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("NoSpecialType")) {
            String typeName = nbt.getString("NoSpecialType");
            try {
                this.noSpecialType = SignNoSpecial.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                this.noSpecialType = SignNoSpecial.SIGN_STOP;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("NoSpecialType", this.noSpecialType.name());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}