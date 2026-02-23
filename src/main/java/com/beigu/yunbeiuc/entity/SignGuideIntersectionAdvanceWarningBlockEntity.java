package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.data.SignGuideIntersectionAdvanceWarning;
import com.beigu.yunbeiuc.block.custom.sign.SignGuideIntersectionAdvanceWarningBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignGuideIntersectionAdvanceWarningBlockEntity extends BlockEntity {
    private SignGuideIntersectionAdvanceWarning warningType = SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1;

    public SignGuideIntersectionAdvanceWarningBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_BLOCK_ENTITY, pos, state);
    }

    public SignGuideIntersectionAdvanceWarning getWarningType() {
        return warningType;
    }

    public void setWarningType(SignGuideIntersectionAdvanceWarning warningType) {
        this.warningType = warningType;
        this.markDirty();

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignGuideIntersectionAdvanceWarningBlock) {
                BlockState newState = currentState.with(SignGuideIntersectionAdvanceWarningBlock.WARNING_TYPE, warningType);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("WarningType")) {
            String typeName = nbt.getString("WarningType");
            try {
                this.warningType = SignGuideIntersectionAdvanceWarning.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                this.warningType = SignGuideIntersectionAdvanceWarning.SIGN_GUIDE_INTERSECTION_ADVANCE_WARNING_1;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("WarningType", this.warningType.name());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
