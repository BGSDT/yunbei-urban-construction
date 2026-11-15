package com.beigu.yunbeiuc.api.network;

import com.beigu.yunbeiuc.api.resource.Id;
import com.beigu.yunbeiuc.api.resource.IdAdapter;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;

// 26.3 网络适配：对外保持「频道 Id + FriendlyByteBuf」旧接口，对内翻译成 Architectury 22 的强类型 RawC2SPayload，按频道分发。
public final class NetworkCompat {
    private NetworkCompat() {
    }

    private static final Map<Identifier, Receiver> HANDLERS = new HashMap<>();
    private static boolean payloadRegistered = false;

    @FunctionalInterface
    public interface Receiver {
        void receive(FriendlyByteBuf buf, NetworkManager.PacketContext context);
    }

    public static FriendlyByteBuf newBuffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess());
    }

    public static void registerC2S(Id id, Receiver receiver) {
        HANDLERS.put(IdAdapter.mc(id), receiver);
        if (payloadRegistered) {
            return;
        }
        payloadRegistered = true;
        NetworkManager.registerC2S(RawC2SPayload.TYPE, RawC2SPayload.CODEC, (payload, context) -> {
            Receiver handler = HANDLERS.get(payload.channel());
            if (handler == null) {
                return;
            }
            FriendlyByteBuf buf = new RegistryFriendlyByteBuf(
                    Unpooled.wrappedBuffer(payload.data()), context.registryAccess());
            handler.receive(buf, context);
        });
    }

    public static void sendToServer(Id id, FriendlyByteBuf buf) {
        NetworkManager.sendToServer(new RawC2SPayload(IdAdapter.mc(id), copyBytes(buf)));
    }

    public static void sendToPlayer(ServerPlayer player, Id id, FriendlyByteBuf buf) {
        NetworkManager.sendToPlayer(player, new RawC2SPayload(IdAdapter.mc(id), copyBytes(buf)));
    }

    private static byte[] copyBytes(FriendlyByteBuf buf) {
        ByteBuf copy = buf.copy();
        try {
            byte[] out = new byte[copy.readableBytes()];
            copy.readBytes(out);
            return out;
        } finally {
            copy.release();
        }
    }

    private static RegistryAccess registryAccess() {
        Minecraft client = Minecraft.getInstance();
        if (client.level != null) {
            return client.level.registryAccess();
        }
        if (client.getConnection() != null) {
            return client.getConnection().registryAccess();
        }
        return RegistryAccess.EMPTY;
    }
}
