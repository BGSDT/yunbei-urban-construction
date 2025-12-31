package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.block.ModBlocks;
import com.beigu.yunbeiuc.block.custom.data.AntiGlareNetPoleType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class AntiGlareNetPoleBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<AntiGlareNetPoleType> POLE_TYPE =
            EnumProperty.of("pole_type", AntiGlareNetPoleType.class);

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    public AntiGlareNetPoleBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POLE_TYPE, AntiGlareNetPoleType.ANTI_GLARE_NET_POLE));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POLE_TYPE);
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
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == getRelativeLeft(state.get(FACING)) || direction == getRelativeRight(state.get(FACING))) {
            return this.updatePoleType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        world.setBlockState(pos, updatePoleType(state, world, pos), Block.NOTIFY_ALL);
    }

    private BlockState updatePoleType(BlockState state, WorldAccess world, BlockPos pos) {
        Direction facing = state.get(FACING);
        Direction leftDir = getRelativeLeft(facing);
        Direction rightDir = getRelativeRight(facing);

        boolean hasNetOnRight = hasAntiGlareNet(world, pos.offset(rightDir));
        boolean hasNetOnLeft = hasAntiGlareNet(world, pos.offset(leftDir));

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

        return state.with(POLE_TYPE, newType);
    }

    private Direction getRelativeLeft(Direction facing) {
        return facing.rotateYCounterclockwise(); // 左侧方向
    }

    private Direction getRelativeRight(Direction facing) {
        return facing.rotateYClockwise(); // 右侧方向
    }

    private boolean hasAntiGlareNet(WorldAccess world, BlockPos pos) {
        // 替换 ModBlocks.ANTI_GLARE_NET 为您的实际防眩网方块
        return world.getBlockState(pos).getBlock() == ModBlocks.ANTI_GLARE_NET;
    }
}