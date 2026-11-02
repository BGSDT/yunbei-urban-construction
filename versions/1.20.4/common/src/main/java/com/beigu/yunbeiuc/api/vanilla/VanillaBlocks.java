package com.beigu.yunbeiuc.api.vanilla;

import net.minecraft.world.level.block.Block;

/**
 * Version-neutral access to vanilla blocks whose constant names changed.
 *
 * <p>26.3 replaced the per-colour block constants with {@code ColorCollection}, so
 * {@code Blocks.CYAN_TERRACOTTA} no longer exists there.</p>
 */
public final class VanillaBlocks {
    private VanillaBlocks() {
    }

    public static final Block CYAN_TERRACOTTA = net.minecraft.world.level.block.Blocks.CYAN_TERRACOTTA;
}
