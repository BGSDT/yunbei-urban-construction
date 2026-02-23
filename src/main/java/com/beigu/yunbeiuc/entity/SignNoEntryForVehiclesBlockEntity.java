package com.beigu.yunbeiuc.entity;

import com.beigu.yunbeiuc.block.custom.sign.SignNoEntryForVehiclesBlock;
import com.beigu.yunbeiuc.block.custom.sign.data.SignNoEntryForVehicles;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;

public class SignNoEntryForVehiclesBlockEntity extends BlockEntity {
    private SignNoEntryForVehicles vehicleType = SignNoEntryForVehicles.SIGN_NO_ENTRY;

    public SignNoEntryForVehiclesBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SIGN_NO_ENTRY_FOR_VEHICLES_BLOCK_ENTITY, pos, state);
    }

    public SignNoEntryForVehicles getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(SignNoEntryForVehicles vehicleType) {
        this.vehicleType = vehicleType;
        this.markDirty();

        // 更新方块状态
        if (this.world != null) {
            BlockState currentState = this.world.getBlockState(this.pos);
            if (currentState.getBlock() instanceof SignNoEntryForVehiclesBlock) {
                BlockState newState = currentState.with(SignNoEntryForVehiclesBlock.VEHICLE_TYPE, vehicleType);
                this.world.setBlockState(this.pos, newState, Block.NOTIFY_ALL);
            }
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        if (nbt.contains("VehicleType")) {
            String typeName = nbt.getString("VehicleType");
            try {
                this.vehicleType = SignNoEntryForVehicles.valueOf(typeName);
            } catch (IllegalArgumentException e) {
                this.vehicleType = SignNoEntryForVehicles.SIGN_NO_ENTRY;
            }
        }
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putString("VehicleType", this.vehicleType.name());
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}