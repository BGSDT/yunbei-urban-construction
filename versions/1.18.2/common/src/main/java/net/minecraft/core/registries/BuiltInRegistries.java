package net.minecraft.core.registries;

import net.minecraft.core.Registry;
import net.minecraft.world.level.block.Block;

/**
 * 1.16.5 ~ 1.19.2 没有原版的 {@code net.minecraft.core.registries.BuiltInRegistries}
 * （1.19.3 才加入），这里提供一个最小兼容层，让共享代码里的
 * {@code BuiltInRegistries.BLOCK.getKey(block)} 能在这几个版本编译并工作。
 */
public final class BuiltInRegistries {
    private BuiltInRegistries() {
    }

    public static final Registry<Block> BLOCK = Registry.BLOCK;
}