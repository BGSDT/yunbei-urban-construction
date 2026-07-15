package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.block.custom.pole.RoadPoleHorizontal;
import com.beigu.yunbeiuc.block.custom.pole.RoadPoleLongitudinal;
import com.beigu.yunbeiuc.entity.SignGuideIntersectionWarning4Entity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.screen.SignGuideIntersectionWarning4Screen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SignGuideIntersectionWarning4 extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<Type> TYPE = EnumProperty.of("type", Type.class);

    private static final VoxelShape SHAPE_POLE_L_N = Block.createCuboidShape(0, 0, 19.1, 16, 16, 20);
    private static final VoxelShape SHAPE_POLE_L_E = Block.createCuboidShape(-2.1, 0, 0, -1, 16, 16);
    private static final VoxelShape SHAPE_POLE_L_S = Block.createCuboidShape(0, 0, -2.1, 16, 16, -1);
    private static final VoxelShape SHAPE_POLE_L_W = Block.createCuboidShape(20, 0, 0, 21.1, 16, 16);

    private static final VoxelShape SHAPE_POLE_H_N = Block.createCuboidShape(0, 0, 20.1, 16, 16, 21);
    private static final VoxelShape SHAPE_POLE_H_E = Block.createCuboidShape(-3.1, 0, 0, -2, 16, 16);
    private static final VoxelShape SHAPE_POLE_H_S = Block.createCuboidShape(0, 0, -3.1, 16, 16, -2);
    private static final VoxelShape SHAPE_POLE_H_W = Block.createCuboidShape(20, 0, 0, 21.1, 16, 16);

    private static final VoxelShape SHAPE_NORMAL_N = Block.createCuboidShape(0, 0, 15.1, 16, 16, 16);
    private static final VoxelShape SHAPE_NORMAL_S = Block.createCuboidShape(0, 0, 0, 16, 16, 0.9);
    private static final VoxelShape SHAPE_NORMAL_E = Block.createCuboidShape(0, 0, 0, 0.9, 16, 16);
    private static final VoxelShape SHAPE_NORMAL_W = Block.createCuboidShape(15.1, 0, 0, 16, 16, 16);

    public SignGuideIntersectionWarning4(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TYPE, Type.NORMAL));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.sign_text.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = player.getStackInHand(hand).getItem();
        if (item == ModItems.WAND.get()) {
            if (world.isClient()) {
                openTextDisplayScreen(pos);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void openTextDisplayScreen(BlockPos pos) {
        MinecraftClient.getInstance().setScreen(new SignGuideIntersectionWarning4Screen(pos));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        Direction facing = state.get(FACING);
        Type type = state.get(TYPE);
        return switch (type) {
            case POLE_L -> switch (facing) {
                case SOUTH -> SHAPE_POLE_L_S;
                case EAST -> SHAPE_POLE_L_E;
                case WEST -> SHAPE_POLE_L_W;
                default -> SHAPE_POLE_L_N;
            };
            case POLE_H -> switch (facing) {
                case SOUTH -> SHAPE_POLE_H_S;
                case EAST -> SHAPE_POLE_H_E;
                case WEST -> SHAPE_POLE_H_W;
                default -> SHAPE_POLE_H_N;
            };
            case NORMAL -> switch (facing) {
                case SOUTH -> SHAPE_NORMAL_S;
                case EAST -> SHAPE_NORMAL_E;
                case WEST -> SHAPE_NORMAL_W;
                default -> SHAPE_NORMAL_N;
            };
        };
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();

        // 获取后面的方块位置
        BlockPos behindPos = pos.offset(facing.getOpposite());
        BlockState behindState = world.getBlockState(behindPos);

        // 根据后面方块的类型决定当前方块的类型
        Type type = determineType(behindState);

        return this.getDefaultState()
                .with(FACING, facing)
                .with(TYPE, type);
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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction,
                                                BlockState neighborState, WorldAccess world,
                                                BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.get(FACING);

        // 检查是否是后面的方块发生了变化
        if (direction == facing.getOpposite()) {
            Type newType = determineType(neighborState);
            if (newType != state.get(TYPE)) {
                return state.with(TYPE, newType);
            }
        }

        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private Type determineType(BlockState behindState) {
        Block behindBlock = behindState.getBlock();

        if (behindBlock instanceof RoadPoleHorizontal) {
            return Type.POLE_H;
        } else if (behindBlock instanceof RoadPoleLongitudinal) {
            return Type.POLE_L;
        } else {
            return Type.NORMAL;
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SignGuideIntersectionWarning4Entity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public enum Type implements StringIdentifiable {
        POLE_L("pole_l"),
        POLE_H("pole_h"),
        NORMAL("normal");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}