package com.beigu.yunbeiuc.api.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

// 26.3 的 Architectury 22 把 Networking 改成强类型 payload；共享树仍按「频道 Id + 裸 buf」书写，
public record RawC2SPayload(Identifier channel, byte[] data) implements CustomPacketPayload {

    public static final Type<RawC2SPayload> TYPE =
            new Type<>(Identifier.fromNamespaceAndPath("yunbeiuc", "raw_c2s"));

    public static final StreamCodec<RegistryFriendlyByteBuf, RawC2SPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeIdentifier(payload.channel());
                buf.writeByteArray(payload.data());
            },
            buf -> new RawC2SPayload(buf.readIdentifier(), buf.readByteArray()));

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
