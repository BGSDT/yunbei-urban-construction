package com.beigu.yunbeiuc.entity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class TransformableBlockEntity extends BlockEntity {
    // 变换数据
    private float posX = 0f;
    private float posY = 0f;
    private float posZ = 0f;
    private float rotX = 0f;
    private float rotY = 0f;
    private float rotZ = 0f;
    private float scale = 1.0f;

    // 原始方块状态
    private BlockState originalState = Blocks.STONE.getDefaultState();
    private boolean isEntityMode = false;

    public TransformableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.TRANSFORMABLE_BLOCK_ENTITY, pos, state);
        // 保存当前位置的方块状态作为原始状态
        this.originalState = state;
    }

    // 更新变换数据
    public void updateTransform(float posX, float posY, float posZ,
                                float rotX, float rotY, float rotZ, float scale) {
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = Math.max(0.1f, scale); // 防止缩放为0或负数

        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    // 设置原始方块状态（可选，构造函数已设置）
    public void setOriginalState(BlockState state) {
        this.originalState = state;
        this.isEntityMode = true;
        markDirty();
    }

    // 启用实体模式
    public void setEntityMode(boolean entityMode) {
        this.isEntityMode = entityMode;
        markDirty();
        if (world != null && !world.isClient) {
            world.updateListeners(pos, getCachedState(), getCachedState(), 3);
        }
    }

    // 重置变换
    public void resetTransform() {
        updateTransform(0f, 0f, 0f, 0f, 0f, 0f, 1.0f);
    }

    // 保存数据到NBT
    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        nbt.putFloat("PosX", posX);
        nbt.putFloat("PosY", posY);
        nbt.putFloat("PosZ", posZ);
        nbt.putFloat("RotX", rotX);
        nbt.putFloat("RotY", rotY);
        nbt.putFloat("RotZ", rotZ);
        nbt.putFloat("Scale", scale);
        nbt.putBoolean("IsEntityMode", isEntityMode);

        // 保存原始方块状态
        if (originalState != null) {
            NbtCompound stateNbt = new NbtCompound();
            stateNbt.putString("BlockId", Registries.BLOCK.getId(originalState.getBlock()).toString());
            nbt.put("OriginalState", stateNbt);
        }
    }

    // 从NBT加载数据
    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        posX = nbt.getFloat("PosX");
        posY = nbt.getFloat("PosY");
        posZ = nbt.getFloat("PosZ");
        rotX = nbt.getFloat("RotX");
        rotY = nbt.getFloat("RotY");
        rotZ = nbt.getFloat("RotZ");
        scale = nbt.getFloat("Scale");
        isEntityMode = nbt.getBoolean("IsEntityMode");

        if (nbt.contains("OriginalState")) {
            NbtCompound stateNbt = nbt.getCompound("OriginalState");
            Identifier blockId = new Identifier(stateNbt.getString("BlockId"));
            originalState = Registries.BLOCK.get(blockId).getDefaultState();
        } else {
            // 如果没有保存的原始状态，使用当前位置的方块状态
            if (world != null) {
                originalState = world.getBlockState(pos);
            }
        }
    }

    // 同步到客户端
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    // Getter 方法
    public float getPosX() { return posX; }
    public float getPosY() { return posY; }
    public float getPosZ() { return posZ; }
    public float getRotX() { return rotX; }
    public float getRotY() { return rotY; }
    public float getRotZ() { return rotZ; }
    public float getScale() { return scale; }
    public BlockState getOriginalState() { return originalState; }
    public boolean isEntityMode() { return isEntityMode; }
}