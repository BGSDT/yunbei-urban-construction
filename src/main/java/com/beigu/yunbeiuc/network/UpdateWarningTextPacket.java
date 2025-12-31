package com.beigu.yunbeiuc.network;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

/**
 * Stubbed UpdateWarningTextPacket — text rendering feature removed.
 * Keeps read/write signatures but apply() is a no-op to avoid referencing removed BE APIs.
 */
public class UpdateWarningTextPacket {
    private final BlockPos pos;
    private final List<String> texts;
    private final List<Integer> colors;
    private final List<Integer> fontSizes;

    public UpdateWarningTextPacket(BlockPos pos, List<String> texts, List<Integer> colors, List<Integer> fontSizes) {
        this.pos = pos;
        this.texts = texts == null ? new ArrayList<>() : texts;
        this.colors = colors == null ? new ArrayList<>() : colors;
        this.fontSizes = fontSizes == null ? new ArrayList<>() : fontSizes;
    }

    public UpdateWarningTextPacket(PacketByteBuf buf) {
        this.pos = buf.readBlockPos();
        int size = buf.readInt();
        this.texts = new ArrayList<>();
        this.colors = new ArrayList<>();
        this.fontSizes = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.texts.add(buf.readString());
            this.colors.add(buf.readInt());
            this.fontSizes.add(buf.readInt());
        }
    }

    public void write(PacketByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeInt(texts.size());
        for (int i = 0; i < texts.size(); i++) {
            buf.writeString(texts.get(i));
            buf.writeInt(i < colors.size() ? colors.get(i) : 0);
            buf.writeInt(i < fontSizes.size() ? fontSizes.get(i) : 25);
        }
    }

    public void apply(ServerPlayerEntity player) {
        // No-op: text rendering/storage feature was removed. Keep a debug log in case this packet is still sent.
        System.out.println("[UpdateWarningTextPacket] received but text feature is removed. pos=" + (pos == null ? "null" : pos.toShortString()) + " size=" + texts.size());
    }
}
