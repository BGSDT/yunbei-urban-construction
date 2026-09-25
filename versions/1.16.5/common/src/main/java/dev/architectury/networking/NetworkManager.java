package dev.architectury.networking;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/** 1.16.5 bridge for Architectury's pre-dev package name. */
public final class NetworkManager {
    private NetworkManager() {}

    public enum Side { S2C, C2S }

    public interface PacketContext {
        Player getPlayer();
        void queue(Runnable runnable);
    }

    @FunctionalInterface
    public interface NetworkReceiver {
        void receive(FriendlyByteBuf buffer, PacketContext context);
    }

    public static void registerReceiver(Side side, ResourceLocation id, NetworkReceiver receiver) {
        me.shedaniel.architectury.networking.NetworkManager.registerReceiver(toOldSide(side), id,
                (buffer, context) -> receiver.receive(buffer, new PacketContext() {
                    @Override public Player getPlayer() { return context.getPlayer(); }
                    @Override public void queue(Runnable runnable) { context.queue(runnable); }
                }));
    }

    public static void sendToServer(ResourceLocation id, FriendlyByteBuf buffer) {
        me.shedaniel.architectury.networking.NetworkManager.sendToServer(id, buffer);
    }

    public static void sendToPlayer(ServerPlayer player, ResourceLocation id, FriendlyByteBuf buffer) {
        me.shedaniel.architectury.networking.NetworkManager.sendToPlayer(player, id, buffer);
    }

    private static me.shedaniel.architectury.networking.NetworkManager.Side toOldSide(Side side) {
        return side == Side.C2S
                ? me.shedaniel.architectury.networking.NetworkManager.Side.C2S
                : me.shedaniel.architectury.networking.NetworkManager.Side.S2C;
    }
}
