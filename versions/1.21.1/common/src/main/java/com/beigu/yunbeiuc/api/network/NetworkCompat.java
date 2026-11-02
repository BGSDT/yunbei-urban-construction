package com.beigu.yunbeiuc.api.network;

import com.beigu.yunbeiuc.api.resource.Id;
import com.beigu.yunbeiuc.api.resource.IdAdapter;
import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkCompat {
    private NetworkCompat() {
    }

    public static FriendlyByteBuf newBuffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess());
    }

    @SuppressWarnings("removal")
    public static void sendToServer(Id id, FriendlyByteBuf buf) {
        if (buf instanceof RegistryFriendlyByteBuf registryBuffer) {
            NetworkManager.sendToServer(IdAdapter.mc(id), registryBuffer);
        } else {
            NetworkManager.sendToServer(IdAdapter.mc(id), new RegistryFriendlyByteBuf(buf.copy(), registryAccess()));
        }
    }

    @SuppressWarnings("removal")
    public static void sendToPlayer(ServerPlayer player, Id id, FriendlyByteBuf buf) {
        RegistryFriendlyByteBuf registryBuffer = buf instanceof RegistryFriendlyByteBuf existing
                ? existing
                : new RegistryFriendlyByteBuf(buf.copy(), player.serverLevel().registryAccess());
        NetworkManager.sendToPlayer(player, IdAdapter.mc(id), registryBuffer);
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
