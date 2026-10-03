package com.beigu.yunbeiuc.api.network;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;

/**
 * 1.21.1 network bridge: Architectury 13 carries a {@link RegistryFriendlyByteBuf} in its
 * resource-location packet API, while the shared screens build plain buffers.
 */
public final class NetworkCompat {
    private NetworkCompat() {
    }

    /** Buffer used by the shared screens to write a packet payload. */
    public static FriendlyByteBuf newBuffer() {
        return new RegistryFriendlyByteBuf(Unpooled.buffer(), registryAccess());
    }

    public static void sendToServer(ResourceLocation id, FriendlyByteBuf buf) {
        if (buf instanceof RegistryFriendlyByteBuf registryBuffer) {
            NetworkManager.sendToServer(id, registryBuffer);
        } else {
            NetworkManager.sendToServer(id, new RegistryFriendlyByteBuf(buf.copy(), registryAccess()));
        }
    }

    /**
     * Server -> client push. Only actually used on 1.16.5 / 1.17.1 (see {@code BlockEntityMapper});
     * it still has to exist here so the shared code compiles on every version.
     */
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