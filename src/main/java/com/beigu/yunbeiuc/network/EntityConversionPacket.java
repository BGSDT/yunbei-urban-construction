package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.TransformableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.registry.Registries;

public class EntityConversionPacket {
    private final BlockPos pos;
    private final boolean toEntity;
    private final BlockState originalState;

    public EntityConversionPacket(BlockPos pos, boolean toEntity, BlockState originalState) {
        this.pos = pos;
        this.toEntity = toEntity;
        this.originalState = originalState;
    }

    public EntityConversionPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.toEntity = buf.readBoolean();

        NbtCompound stateNbt = buf.readNbt();
        if (stateNbt != null && stateNbt.contains("BlockId")) {
            Identifier blockId = new Identifier(stateNbt.getString("BlockId"));
            Block block = Registries.BLOCK.get(blockId);
            this.originalState = block.getDefaultState();
        } else {
            this.originalState = null;
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeBoolean(toEntity);

        if (originalState != null) {
            NbtCompound stateNbt = new NbtCompound();
            stateNbt.putString("BlockId", Registries.BLOCK.getId(originalState.getBlock()).toString());
            buf.writeNbt(stateNbt);
        } else {
            buf.writeNbt(null);
        }
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            if (toEntity) {
                convertBlockToEntity(player, pos, originalState);
            } else {
                revertEntityToBlock(player, pos, originalState);
            }
        }
    }

    private void convertBlockToEntity(ServerPlayerEntity player, BlockPos pos, BlockState originalState) {
        var world = player.getWorld();

        // 1. 先创建并添加方块实体到原方块位置
        TransformableBlockEntity blockEntity = new TransformableBlockEntity(pos, originalState);
        blockEntity.setOriginalState(originalState);
        blockEntity.setEntityMode(true);

        // 2. 关键：直接添加到世界，不改变原方块
        world.addBlockEntity(blockEntity);

        // 3. 强制同步给客户端
        blockEntity.markDirty();
        world.updateListeners(pos, originalState, originalState, 3);

        player.sendMessage(net.minecraft.text.Text.literal("✓ 已转换为实体模式"), false);
    }

    private void revertEntityToBlock(ServerPlayerEntity player, BlockPos pos, BlockState originalState) {
        var world = player.getWorld();

        // 只移除方块实体，原方块保持不变
        world.removeBlockEntity(pos);
        world.updateListeners(pos, world.getBlockState(pos), world.getBlockState(pos), 3);

        player.sendMessage(net.minecraft.text.Text.literal("✓ 已恢复为方块模式"), false);
    }

    public static void send(BlockPos pos, boolean toEntity, BlockState originalState) {
        EntityConversionPacket packet = new EntityConversionPacket(pos, toEntity, originalState);
        PacketByteBuf buf = new PacketByteBuf(io.netty.buffer.Unpooled.buffer());
        packet.write(buf);
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(ModMessages.ENTITY_CONVERSION, buf);
    }
}