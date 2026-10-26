package com.beigu.yunbeiuc.api.mapper;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;
public abstract class DirectionalBlockCompat extends DirectionalBlock implements BlockCompatHooks {
    protected DirectionalBlockCompat(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
        appendHoverTextCompat(stack, tooltip, flag);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        return useCompat(state, level, pos, player, hand, hit);
    }

    // 1.20.2 起 playerWillDestroy 返回值由 void 改为 BlockState。
    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        playerWillDestroyCompat(level, pos, state, player);
        return state;
    }

    // 1.20.2 起 Block 新增 codec()（Block 有默认实现，但 DirectionalBlock 仍是抽象，必须实现）。
    private static final MapCodec<DirectionalBlockCompat.Fallback> FALLBACK_CODEC =
            simpleCodec(DirectionalBlockCompat.Fallback::new);

    @Override
    protected MapCodec<? extends DirectionalBlock> codec() {
        return FALLBACK_CODEC;
    }

    private static final class Fallback extends DirectionalBlock {
        private Fallback(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        protected MapCodec<? extends DirectionalBlock> codec() {
            return FALLBACK_CODEC;
        }
    }
}
