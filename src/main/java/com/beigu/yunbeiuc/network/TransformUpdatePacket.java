package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.entity.TransformableBlockEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class TransformUpdatePacket {
    private final BlockPos pos;
    private final float posX, posY, posZ;
    private final float rotX, rotY, rotZ;
    private final float scale;

    public TransformUpdatePacket(BlockPos pos, float posX, float posY, float posZ,
                                 float rotX, float rotY, float rotZ, float scale) {
        this.pos = pos;
        this.posX = posX;
        this.posY = posY;
        this.posZ = posZ;
        this.rotX = rotX;
        this.rotY = rotY;
        this.rotZ = rotZ;
        this.scale = scale;
    }

    public TransformUpdatePacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.posX = buf.readFloat();
        this.posY = buf.readFloat();
        this.posZ = buf.readFloat();
        this.rotX = buf.readFloat();
        this.rotY = buf.readFloat();
        this.rotZ = buf.readFloat();
        this.scale = buf.readFloat();
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeFloat(posX);
        buf.writeFloat(posY);
        buf.writeFloat(posZ);
        buf.writeFloat(rotX);
        buf.writeFloat(rotY);
        buf.writeFloat(rotZ);
        buf.writeFloat(scale);
    }

    public void apply(ServerPlayerEntity player) {
        if (player.getWorld().isChunkLoaded(pos)) {
            var blockEntity = player.getWorld().getBlockEntity(pos);
            if (blockEntity instanceof TransformableBlockEntity transformable) {
                transformable.updateTransform(posX, posY, posZ, rotX, rotY, rotZ, scale);

                // 立即同步给所有客户端
                player.getWorld().getPlayers().forEach(p -> {
                    if (p instanceof ServerPlayerEntity serverPlayer) {
                        serverPlayer.networkHandler.sendPacket(
                                ((TransformableBlockEntity)blockEntity).toUpdatePacket()
                        );
                    }
                });
            }
        }
    }

    public static void send(BlockPos pos, float posX, float posY, float posZ,
                            float rotX, float rotY, float rotZ, float scale) {
        TransformUpdatePacket packet = new TransformUpdatePacket(pos, posX, posY, posZ, rotX, rotY, rotZ, scale);
        PacketByteBuf buf = new PacketByteBuf(io.netty.buffer.Unpooled.buffer());
        packet.write(buf);
        net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking.send(ModMessages.TRANSFORM_UPDATE, buf);
    }
}