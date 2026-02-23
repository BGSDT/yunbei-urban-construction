package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.block.custom.sign.data.SignNoEntryForVehicles;
import com.beigu.yunbeiuc.entity.SignNoEntryForVehiclesBlockEntity;
import com.beigu.yunbeiuc.screen.SignNoEntryForVehiclesScreen;
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

public class SignNoEntryForVehiclesBlock extends Block implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<SignNoEntryForVehicles> VEHICLE_TYPE = EnumProperty.of("vehicle_type", SignNoEntryForVehicles.class);

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(0, 0, 14.6, 16, 16, 16);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(0, 0, 0, 1.4, 16, 16);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(0, 0, 0, 16, 16, 1.4);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(14.6, 0, 0, 16, 16, 16);

    public SignNoEntryForVehiclesBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH)
                .with(VEHICLE_TYPE, SignNoEntryForVehicles.SIGN_NO_ENTRY));
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
        builder.add(FACING, VEHICLE_TYPE);
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
        return new SignNoEntryForVehiclesBlockEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignNoEntryForVehiclesBlockEntity vehicleBlockEntity) {
                // 只在客户端打开界面
                MinecraftClient.getInstance().setScreen(
                        new SignNoEntryForVehiclesScreen(Text.empty(), pos, vehicleBlockEntity.getVehicleType())
                );
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (!state.isOf(newState.getBlock())) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof SignNoEntryForVehiclesBlockEntity) {
                // 如果方块被破坏，掉落物品时可以包含NBT数据
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
        if (blockEntity instanceof SignNoEntryForVehiclesBlockEntity vehicleBlockEntity) {
            return vehicleBlockEntity.getVehicleType().ordinal() + 1;
        }
        return 0;
    }
}