package com.beigu.yunbeiuc.api.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkCompat {
    private NetworkCompat() {
    }

    public static FriendlyByteBuf newBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    @SuppressWarnings("removal")
    public static void sendToServer(ResourceLocation id, FriendlyByteBuf buf) {
        NetworkManager.sendToServer(id, buf);
    }

    @SuppressWarnings("removal")
    public static void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        NetworkManager.sendToPlayer(player, id, buf);
    }
}
