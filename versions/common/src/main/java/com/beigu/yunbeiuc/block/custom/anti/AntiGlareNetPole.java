package com.beigu.yunbeiuc.block.custom.anti;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
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
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.stream.Stream;

public class AntiGlareNetPole extends Block {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<AntiGlareNetPoleType> POLE_TYPE =
            EnumProperty.create("pole_type", AntiGlareNetPoleType.class);

    private static final VoxelShape SHAPE_N = Stream.of(
            Block.box(0, 1, 7.75, 6.75, 15, 8.25),
            Block.box(9.25, 1, 7.75, 16, 15, 8.25),
            Block.box(6.75, 0, 6.75, 9.25, 16, 9.25)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_S = Stream.of(
            Block.box(0, 1, 7.75, 6.75, 15, 8.25),
            Block.box(9.25, 1, 7.75, 16, 15, 8.25),
            Block.box(6.75, 0, 6.75, 9.25, 16, 9.25)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_E = Stream.of(
            Block.box(7.75, 1, 0, 8.25, 15, 6.75),
            Block.box(7.75, 1, 9.25, 8.25, 15, 16),
            Block.box(6.75, 0, 6.75, 9.25, 16, 9.25)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape SHAPE_W = Stream.of(
            Block.box(7.75, 1, 0, 8.25, 15, 6.75),
            Block.box(7.75, 1, 9.25, 8.25, 15, 16),
            Block.box(6.75, 0, 6.75, 9.25, 16, 9.25)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    public AntiGlareNetPole(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(POLE_TYPE, AntiGlareNetPoleType.ANTI_GLARE_NET_POLE));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POLE_TYPE);
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
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        if (direction == getRelativeLeft(state.getValue(FACING)) || direction == getRelativeRight(state.getValue(FACING))) {
            return this.updatePoleType(state, world, pos);
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onPlace(state, world, pos, oldState, notify);
        world.setBlock(pos, updatePoleType(state, world, pos), Block.UPDATE_ALL);
    }

    private BlockState updatePoleType(BlockState state, LevelAccessor world, BlockPos pos) {
        Direction facing = state.getValue(FACING);
        Direction leftDir = getRelativeLeft(facing);
        Direction rightDir = getRelativeRight(facing);

        boolean hasNetOnRight = hasAntiGlareNet(world, pos.relative(rightDir));
        boolean hasNetOnLeft = hasAntiGlareNet(world, pos.relative(leftDir));

        AntiGlareNetPoleType newType;

        // 修正逻辑：
        // 右边有防眩网 → right 状态（防眩网在右边，支柱右边有连接）
        // 左边有防眩网 → left 状态（防眩网在左边，支柱左边有连接）
        if (hasNetOnRight && !hasNetOnLeft) {
            newType = AntiGlareNetPoleType.ANTI_GLARE_NET_POLE_RIGHT;
        } else if (hasNetOnLeft && !hasNetOnRight) {
            newType = AntiGlareNetPoleType.ANTI_GLARE_NET_POLE_LEFT;
        } else {
            newType = AntiGlareNetPoleType.ANTI_GLARE_NET_POLE;
        }

        return state.setValue(POLE_TYPE, newType);
    }

    private Direction getRelativeLeft(Direction facing) {
        return facing.getCounterClockWise(); // 左侧方向
    }

    private Direction getRelativeRight(Direction facing) {
        return facing.getClockWise(); // 右侧方向
    }

    private boolean hasAntiGlareNet(LevelAccessor world, BlockPos pos) {
        // 替换 ModBlocks.ANTI_GLARE_NET 为您的实际防眩网方块
        return world.getBlockState(pos).getBlock() == MunicipalBlocks.ANTI_GLARE_NET;
    }

    public enum AntiGlareNetPoleType implements StringRepresentable {
        ANTI_GLARE_NET_POLE("anti_glare_net_pole"),
        ANTI_GLARE_NET_POLE_LEFT("anti_glare_net_pole_left"),
        ANTI_GLARE_NET_POLE_RIGHT("anti_glare_net_pole_right");

        private final String name;

        AntiGlareNetPoleType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
