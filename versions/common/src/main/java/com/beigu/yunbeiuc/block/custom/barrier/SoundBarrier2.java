package com.beigu.yunbeiuc.block.custom.barrier;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class SoundBarrier2 extends DirectionalBlock {
    public static final IntegerProperty LAYER = IntegerProperty.create("layer", 0, 2); // 0=底, 1=中, 2=顶

    private static final VoxelShape SHAPE_N = Block.box(0, 0, 6.5, 16, 16, 9.5);
    private static final VoxelShape SHAPE_S = Block.box(0, 0, 6.5, 16, 16, 9.5);
    private static final VoxelShape SHAPE_E = Block.box(6.5, 0, 0, 9.5, 16, 16);
    private static final VoxelShape SHAPE_W = Block.box(6.5, 0, 0, 9.5, 16, 16);

    public SoundBarrier2(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LAYER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, LAYER);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        BlockPos pos = ctx.getClickedPos();
        Level world = ctx.getLevel();

        if (!world.getBlockState(pos).canBeReplaced(ctx) ||
                !world.getBlockState(pos.above()).canBeReplaced(ctx) ||
                !world.getBlockState(pos.above(2)).canBeReplaced(ctx)) {
            return null;
        }

        if (pos.getY() > world.getMaxBuildHeight() - 3) {
            return null;
        }

        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite()).setValue(LAYER, 0);
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state,
                         @Nullable LivingEntity placer, ItemStack itemStack) {
        super.setPlacedBy(world, pos, state, placer, itemStack);

        if (!world.isClientSide) {
            Direction direction = state.getValue(FACING);

            world.setBlock(pos.above(),
                    this.defaultBlockState().setValue(FACING, direction).setValue(LAYER, 1),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());

            world.setBlock(pos.above(2),
                    this.defaultBlockState().setValue(FACING, direction).setValue(LAYER, 2),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateImmediate());
        }
    }

    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        int layer = state.getValue(LAYER);

        if (!world.isClientSide) {
            breakOtherLayers(world, pos, layer, player);
        }

        super.playerWillDestroy(world, pos, state, player);
    }

    private void breakOtherLayers(Level world, BlockPos pos, int currentLayer, Player player) {
        for (int i = 0; i < 3; i++) {
            if (i != currentLayer) {
                BlockPos otherPos = pos.relative(Direction.UP, i - currentLayer);
                breakBlockIfExists(world, otherPos, player);
            }
        }
    }

    private void breakBlockIfExists(Level world, BlockPos pos, Player player) {
        BlockState state = world.getBlockState(pos);
        if (state.is(this)) {
            world.setBlock(pos, Blocks.AIR.defaultBlockState(),
                    VersionServices.blocks().updateAll() | VersionServices.blocks().updateNeighbors());
            world.levelEvent(player, 2001, pos,
                    Block.getId(state));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world,
                                      BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public PushReaction getPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos,
                               Block block, BlockPos fromPos, boolean notify) {
        if (!world.isClientSide) {
            int layer = state.getValue(LAYER);

            if (fromPos.getY() == pos.getY() + 1 - layer ||
                    fromPos.getY() == pos.getY() + 2 - layer ||
                    fromPos.getY() == pos.getY() - layer) {
                if (world.getBlockState(fromPos).is(this)) {
                    return;
                }
            }

            for (int i = 0; i < 3; i++) {
                if (i != layer) {
                    BlockPos otherPos = pos.relative(Direction.UP, i - layer);
                    if (!world.getBlockState(otherPos).is(this)) {
                        world.setBlock(pos, Blocks.AIR.defaultBlockState(),
                                VersionServices.blocks().updateAll() | VersionServices.blocks().updateNeighbors());
                        break;
                    }
                }
            }
        }
    }
}
