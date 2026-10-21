package com.beigu.yunbeiuc.api.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkCompat {
    private NetworkCompat() {
    }

    public static FriendlyByteBuf newBuffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess());
    }

    @SuppressWarnings("removal")
    public static void sendToServer(ResourceLocation id, FriendlyByteBuf buf) {
        if (buf instanceof RegistryFriendlyByteBuf registryBuffer) {
            NetworkManager.sendToServer(id, registryBuffer);
        } else {
            NetworkManager.sendToServer(id, new RegistryFriendlyByteBuf(buf.copy(), registryAccess()));
        }
    }

    @SuppressWarnings("removal")
    public static void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buf) {
        RegistryFriendlyByteBuf registryBuffer = buf instanceof RegistryFriendlyByteBuf existing
                ? existing
                : new RegistryFriendlyByteBuf(buf.copy(), player.serverLevel().registryAccess());
        NetworkManager.sendToPlayer(player, id, registryBuffer);
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
