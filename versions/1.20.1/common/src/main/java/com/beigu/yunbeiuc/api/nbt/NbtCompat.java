package com.beigu.yunbeiuc.api.nbt;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;

/**
 * Version-neutral NBT readers.
 *
 * <p>Minecraft 1.21.5 turned the {@code CompoundTag} getters into {@code Optional} returning
 * methods (and added {@code getXOr(key, default)}), while older versions return the plain value.
 * The shared tree reads NBT through this adapter so the same source compiles on both.</p>
 */
public final class NbtCompat {
    private NbtCompat() {
    }

    public static String getString(CompoundTag tag, String key) { return tag.getString(key); }

    public static int getInt(CompoundTag tag, String key) { return tag.getInt(key); }

    public static long getLong(CompoundTag tag, String key) { return tag.getLong(key); }

    public static float getFloat(CompoundTag tag, String key) { return tag.getFloat(key); }

    public static double getDouble(CompoundTag tag, String key) { return tag.getDouble(key); }

    public static boolean getBoolean(CompoundTag tag, String key) { return tag.getBoolean(key); }

    public static byte getByte(CompoundTag tag, String key) { return tag.getByte(key); }

    public static short getShort(CompoundTag tag, String key) { return tag.getShort(key); }

    public static CompoundTag getCompound(CompoundTag tag, String key) { return tag.getCompound(key); }

    public static ListTag getList(CompoundTag tag, String key, int type) { return tag.getList(key, type); }

    public static int[] getIntArray(CompoundTag tag, String key) { return tag.getIntArray(key); }

    public static long[] getLongArray(CompoundTag tag, String key) { return tag.getLongArray(key); }

    public static byte[] getByteArray(CompoundTag tag, String key) { return tag.getByteArray(key); }

    public static CompoundTag compoundAt(ListTag list, int index) { return list.getCompound(index); }

    public static boolean contains(CompoundTag tag, String key, int type) { return tag.contains(key, type); }
}
