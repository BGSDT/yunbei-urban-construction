package com.beigu.yunbeiuc.api.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

// Version-neutral NBT readers. 1.21.5 turned CompoundTag getters into Optional-returning methods,
public final class NbtCompat {
    private NbtCompat() {
    }

    public static String getString(CompoundTag tag, String key) { return tag.getStringOr(key, ""); }

    public static int getInt(CompoundTag tag, String key) { return tag.getIntOr(key, 0); }

    public static long getLong(CompoundTag tag, String key) { return tag.getLongOr(key, 0L); }

    public static float getFloat(CompoundTag tag, String key) { return tag.getFloatOr(key, 0.0f); }

    public static double getDouble(CompoundTag tag, String key) { return tag.getDoubleOr(key, 0.0d); }

    public static boolean getBoolean(CompoundTag tag, String key) { return tag.getBooleanOr(key, false); }

    public static byte getByte(CompoundTag tag, String key) { return tag.getByteOr(key, (byte) 0); }

    public static short getShort(CompoundTag tag, String key) { return tag.getShortOr(key, (short) 0); }

    public static CompoundTag getCompound(CompoundTag tag, String key) { return tag.getCompoundOrEmpty(key); }

    public static ListTag getList(CompoundTag tag, String key, int type) { return tag.getListOrEmpty(key); }

    public static int[] getIntArray(CompoundTag tag, String key) { return tag.getIntArray(key).orElse(new int[0]); }

    public static long[] getLongArray(CompoundTag tag, String key) { return tag.getLongArray(key).orElse(new long[0]); }

    public static byte[] getByteArray(CompoundTag tag, String key) { return tag.getByteArray(key).orElse(new byte[0]); }

    public static CompoundTag compoundAt(ListTag list, int index) { return list.getCompoundOrEmpty(index); }

    public static boolean contains(CompoundTag tag, String key, int type) { return tag.contains(key); }
}
