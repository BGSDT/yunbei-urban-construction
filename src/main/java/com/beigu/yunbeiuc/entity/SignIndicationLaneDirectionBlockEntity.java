package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.SignIndicationLaneDirectionBlock;
import com.beigu.yunbeiuc.block.custom.data.SignIndicationLaneDirection;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignIndicationLaneDirectionBlockEntity extends BlockEntity {
    private SignIndicationLaneDirection laneDirectionType = SignIndicationLaneDirection.SIGN_INDICATION_LANE_DIRECTION_STRAIGHT;

    public SignIndicationLaneDirectionBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_INDICATION_LANE_DIRECTION_BLOCK_ENTITY, pos, state);
    }

    public SignIndicationLaneDirection getLaneDirectionType() {
        return laneDirectionType;
    }

    public void setLaneDirectionType(SignIndicationLaneDirection laneDirectionType) {
        this.laneDirectionType = laneDirectionType;
        this.markDirty();

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignIndicationLaneDirectionBlock) {
                BlockState newState = currentState.with(SignIndicationLaneDirectionBlock.LANE_DIRECTION_TYPE, laneDirectionType);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("LaneDirectionType")) {
            String typeName = nbt.getString("LaneDirectionType");
            try {
                this.laneDirectionType = SignIndicationLaneDirection.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                this.laneDirectionType = SignIndicationLaneDirection.SIGN_INDICATION_LANE_DIRECTION_STRAIGHT;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("LaneDirectionType", this.laneDirectionType.name());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}