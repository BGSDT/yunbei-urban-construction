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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public abstract class EntityBlockMapper extends BaseEntityBlock implements BlockCompatHooks {
    protected EntityBlockMapper(BlockBehaviour.Properties properties) { super(properties); }
    @Nullable @Override public final BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return newBlockEntityCompat(pos, state); }
    @Nullable protected abstract BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state);

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

    // 1.20.2 起 BaseEntityBlock 仍是抽象 codec()，必须实现（与 1.21.1 同款 fallback）。
    private static final MapCodec<EntityBlockMapper.Fallback> FALLBACK_CODEC = simpleCodec(EntityBlockMapper.Fallback::new);

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return FALLBACK_CODEC;
    }

    private static final class Fallback extends BaseEntityBlock {
        private Fallback(BlockBehaviour.Properties properties) {
            super(properties);
        }

        @Override
        protected MapCodec<? extends BaseEntityBlock> codec() {
            return FALLBACK_CODEC;
        }

        @Nullable
        @Override
        public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
            return null;
        }
    }
}
