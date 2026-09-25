package com.beigu.yunbeiuc.block.custom.rubbish;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionResult;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RubbishBinGrayGreen extends Block {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("block.yunbeiuc.rubbish_bin.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }

    private static final VoxelShape SHAPE_N = Shapes.join(
            Block.box(0, 0, 4.25, 16, 14.5, 11.75),
            Block.box(7, 14.5, 4.25, 9, 16, 11.75),
            BooleanOp.OR
    );

    private static final VoxelShape SHAPE_E = Shapes.join(
            Block.box(4.25, 0, 0, 11.75, 14.5, 16),
            Block.box(4.25, 14.5, 7, 11.75, 16, 9),
            BooleanOp.OR
    );

    private static final VoxelShape SHAPE_S = Shapes.join(
            Block.box(0, 0, 4.25, 16, 14.5, 11.75),
            Block.box(7, 14.5, 4.25, 9, 16, 11.75),
            BooleanOp.OR
    );

    private static final VoxelShape SHAPE_W = Shapes.join(
            Block.box(4.25, 0, 0, 11.75, 14.5, 16),
            Block.box(4.25, 14.5, 7, 11.75, 16, 9),
            BooleanOp.OR
    );
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public RubbishBinGrayGreen(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                stateDefinition.any().setValue(FACING, Direction.NORTH)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case WEST -> SHAPE_W;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty()) {
            player.setItemInHand(hand, ItemStack.EMPTY);
            return InteractionResult.sidedSuccess(world .isClientSide);
        }
        return super .use(state, world, pos, player, hand, hit);
    }
}
