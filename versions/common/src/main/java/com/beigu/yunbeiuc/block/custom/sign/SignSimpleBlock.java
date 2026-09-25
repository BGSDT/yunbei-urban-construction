package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.block.custom.pole.RoadPoleHorizontal;
import com.beigu.yunbeiuc.block.custom.pole.RoadPoleLongitudinal;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public class SignSimpleBlock extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape SHAPE_POLE_L_N = Block.box(0, 0, 19.1, 16, 16, 20);
    private static final VoxelShape SHAPE_POLE_L_E = Block.box(-2.1, 0, 0, -1, 16, 16);
    private static final VoxelShape SHAPE_POLE_L_S = Block.box(0, 0, -2.1, 16, 16, -1);
    private static final VoxelShape SHAPE_POLE_L_W = Block.box(20, 0, 0, 21.1, 16, 16);

    private static final VoxelShape SHAPE_POLE_H_N = Block.box(0, 0, 20.1, 16, 16, 21);
    private static final VoxelShape SHAPE_POLE_H_E = Block.box(-3.1, 0, 0, -2, 16, 16);
    private static final VoxelShape SHAPE_POLE_H_S = Block.box(0, 0, -3.1, 16, 16, -2);
    private static final VoxelShape SHAPE_POLE_H_W = Block.box(20, 0, 0, 21.1, 16, 16);

    private static final VoxelShape SHAPE_NORMAL_N = Block.box(0, 0, 15.1, 16, 16, 16);
    private static final VoxelShape SHAPE_NORMAL_S = Block.box(0, 0, 0, 16, 16, 0.9);
    private static final VoxelShape SHAPE_NORMAL_E = Block.box(0, 0, 0, 0.9, 16, 16);
    private static final VoxelShape SHAPE_NORMAL_W = Block.box(15.1, 0, 0, 16, 16, 16);

    public SignSimpleBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.NORMAL));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        Type type = state.getValue(TYPE);
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

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Direction facing = ctx.getHorizontalDirection().getOpposite();

        // 获取后面的方块位置
        BlockPos behindPos = pos.relative(facing.getOpposite());
        BlockState behindState = world.getBlockState(behindPos);

        // 根据后面方块的类型决定当前方块的类型
        Type type = determineType(behindState);

        return this.defaultBlockState().setValue(FACING, facing).setValue(TYPE, type);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction,
                                                BlockState neighborState, LevelAccessor world,
                                                BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);

        // 检查是否是后面的方块发生了变化
        if (direction == facing.getOpposite()) {
            Type newType = determineType(neighborState);
            if (newType != state.getValue(TYPE)) {
                return state.setValue(TYPE, newType);
            }
        }

        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    private Type determineType(BlockState behindState) {
        Block behindBlock = behindState.getBlock();

        // 假设你的方块类名是这样的
        if (behindBlock instanceof RoadPoleHorizontal) {
            return Type.POLE_H;
        } else if (behindBlock instanceof RoadPoleLongitudinal) {
            return Type.POLE_L;
        } else {
            return Type.NORMAL;
        }
    }

    public enum Type implements StringRepresentable {
        POLE_L("pole_l"),
        POLE_H("pole_h"),
        NORMAL("normal");

        private final String name;

        Type(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
