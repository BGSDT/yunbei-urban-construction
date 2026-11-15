package com.beigu.yunbeiuc.api.network;

import com.beigu.yunbeiuc.api.resource.Id;
import com.beigu.yunbeiuc.api.resource.IdAdapter;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkCompat {
    private NetworkCompat() {
    }

    @FunctionalInterface
    public interface Receiver {
        void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context);
    }

    @SuppressWarnings("removal")
    public static void registerC2S(Id id, Receiver receiver) {
        NetworkManager.registerReceiver(NetworkManager.Side.C2S, IdAdapter.mc(id), receiver::receive);
    }

    public static FriendlyByteBuf newBuffer() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }

    @SuppressWarnings("removal")
    public static void sendToServer(Id id, FriendlyByteBuf buf) {
        NetworkManager.sendToServer(IdAdapter.mc(id), buf);
    }

    @SuppressWarnings("removal")
    public static void sendToPlayer(ServerPlayer player, Id id, FriendlyByteBuf buf) {
        NetworkManager.sendToPlayer(player, IdAdapter.mc(id), buf);
    }
}
