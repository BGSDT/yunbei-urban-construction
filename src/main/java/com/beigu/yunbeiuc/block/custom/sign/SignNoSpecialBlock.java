package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.block.custom.sign.data.SignNoSpecial;
import com.beigu.yunbeiuc.entity.SignNoSpecialBlockEntity;
import com.beigu.yunbeiuc.screen.SignNoSpecialScreen;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class SignNoSpecialBlock extends Block implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<SignNoSpecial> NO_SPECIAL_TYPE = EnumProperty.of("no_special_type", SignNoSpecial.class);

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(0, 0, 14.6, 16, 16, 16);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(0, 0, 0, 1.4, 16, 16);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(0, 0, 0, 16, 16, 1.4);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(14.6, 0, 0, 16, 16, 16);

    public SignNoSpecialBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(NO_SPECIAL_TYPE, SignNoSpecial.SIGN_STOP));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, NO_SPECIAL_TYPE);
    }

    @Override
    public BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignNoSpecialBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignNoSpecialBlockEntity specialBlockEntity) {
                MinecraftClient.getInstance().setScreen(
                        new SignNoSpecialScreen(Text.empty(), pos, specialBlockEntity.getNoSpecialType())
                );
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignNoSpecialBlockEntity) {
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    public int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        BlockEntity blockEntity = world.getBlockEntity(pos);
        if (blockEntity instanceof SignNoSpecialBlockEntity specialBlockEntity) {
            return specialBlockEntity.getNoSpecialType().ordinal() + 1;
        }
        return 0;
    }
}