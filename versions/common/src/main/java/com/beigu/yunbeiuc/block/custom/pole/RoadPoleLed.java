package com.beigu.yunbeiuc.block.custom.pole;

import com.beigu.yunbeiuc.block.custom.sign.CustomTextDisplayBlock;
import com.beigu.yunbeiuc.entity.RoadPoleLedEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoadPoleLed extends CustomTextDisplayBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape SHAPE_N = Block.box(0, 0, 5.5, 16, 16, 10.5);
    private static final VoxelShape SHAPE_S = Block.box(0, 0, 5.5, 16, 16, 10.5);
    private static final VoxelShape SHAPE_E = Block.box(5.5, 0, 0, 10.5, 16, 16);
    private static final VoxelShape SHAPE_W = Block.box(5.5, 0, 0, 10.5, 16, 16);

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("block.yunbeiuc.road_pole_led.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }
    public RoadPoleLed(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.SINGLE));
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

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RoadPoleLedEntity(pos, state);
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

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return this.getRelatedBlockState(state, world, pos, state.getValue(FACING));
    }

    private BlockState getRelatedBlockState(BlockState state, LevelAccessor world, BlockPos pos, Direction facing) {
        return state.setValue(TYPE, determineType(world, pos, facing));
    }

    private Type determineType(LevelAccessor world, BlockPos pos, Direction facing) {
        Direction left = facing.getClockWise();
        Direction right = facing.getCounterClockWise();

        int leftSteps = countInDirection(world, pos, facing, left);
        int rightSteps = countInDirection(world, pos, facing, right);
        int upSteps = countInDirection(world, pos, facing, Direction.UP);
        int downSteps = countInDirection(world, pos, facing, Direction.DOWN);

        int width = leftSteps + 1 + rightSteps;
        int height = upSteps + 1 + downSteps;

        boolean leftEdge = leftSteps == 0;
        boolean rightEdge = rightSteps == 0;
        boolean topEdge = upSteps == 0;
        boolean bottomEdge = downSteps == 0;

        // 3x3 完整网格（高>=3 且 宽>=3）: 1-9
        if (height >= 3 && width >= 3) {
            int col = leftEdge ? 0 : (rightEdge ? 2 : 1);
            int row = topEdge ? 0 : (bottomEdge ? 2 : 1);
            return getGridType(row, col);
        }

        // 2xN：高度=2，宽度>=2（取第1行和第3行）: 1-9
        if (height == 2 && width >= 2) {
            int row = topEdge ? 0 : 2;
            int col = leftEdge ? 0 : (rightEdge ? 2 : 1);
            return getGridType(row, col);
        }

        // Nx2：高度>=2，宽度=2（取第1列和第3列）: 1-9
        if (height >= 2 && width == 2) {
            int row = topEdge ? 0 : (bottomEdge ? 2 : 1);
            int col = leftEdge ? 0 : 2;
            return getGridType(row, col);
        }

        // 1xN 横向条（高=1 宽>=2）: 10-12
        // [10] [11] [11] ... [12]
        if (height == 1 && width >= 2) {
            int col = leftEdge ? 0 : (rightEdge ? 2 : 1);
            return getBarType(col);
        }

        // Nx1 纵向条（高>=2 宽=1）: 13-15
        // [13] [14] [14] ... [15]
        if (height >= 2 && width == 1) {
            int row = topEdge ? 0 : (bottomEdge ? 2 : 1);
            return getColumnType(row);
        }

        // 单个或其他：16
        return Type.SINGLE;
    }

    private Type getGridType(int row, int col) {
        int index = row * 3 + col;
        return switch (index) {
            case 0 -> Type.POS_1;
            case 1 -> Type.POS_2;
            case 2 -> Type.POS_3;
            case 3 -> Type.POS_4;
            case 4 -> Type.POS_5;
            case 5 -> Type.POS_6;
            case 6 -> Type.POS_7;
            case 7 -> Type.POS_8;
            case 8 -> Type.POS_9;
            default -> Type.SINGLE;
        };
    }

    private Type getBarType(int col) {
        return switch (col) {
            case 0 -> Type.BAR_LEFT;
            case 1 -> Type.BAR_CENTER;
            case 2 -> Type.BAR_RIGHT;
            default -> Type.SINGLE;
        };
    }

    private Type getColumnType(int row) {
        return switch (row) {
            case 0 -> Type.COLUMN_TOP;
            case 1 -> Type.COLUMN_CENTER;
            case 2 -> Type.COLUMN_BOTTOM;
            default -> Type.SINGLE;
        };
    }

    private int countInDirection(LevelAccessor world, BlockPos pos, Direction facing, Direction dir) {
        int count = 0;
        BlockPos current = pos.relative(dir);
        while (isRelatedBlock(world, current, facing)) {
            count++;
            current = current.relative(dir);
        }
        return count;
    }

    private boolean isRelatedBlock(LevelAccessor world, BlockPos pos, Direction facing) {
        BlockState state = world.getBlockState(pos);
        return state.getBlock() == this && state.getValue(FACING) == facing;
    }

    public enum Type implements StringRepresentable {
        // 九宫格 1-9
        POS_1("1"), POS_2("2"), POS_3("3"),
        POS_4("4"), POS_5("5"), POS_6("6"),
        POS_7("7"), POS_8("8"), POS_9("9"),
        // 横向条 10-12: [10] [11] [12]
        BAR_LEFT("10"), BAR_CENTER("11"), BAR_RIGHT("12"),
        // 纵向条 13-15: [13] [14] [15]
        COLUMN_TOP("13"), COLUMN_CENTER("14"), COLUMN_BOTTOM("15"),
        // 单个
        SINGLE("16");

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
