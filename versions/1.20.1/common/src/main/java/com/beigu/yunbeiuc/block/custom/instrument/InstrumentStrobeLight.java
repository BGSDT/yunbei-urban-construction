package com.beigu.yunbeiuc.block.custom.instrument;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class InstrumentStrobeLight extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<State> STATE = EnumProperty.of("state", State.class);

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, -0.5, 0, 16, 8.75, 16);

    // 状态持续时间（tick）
    private static final int FLASH_DURATION = 5; // 0.25s
    private static final int FINAL_OFF_DURATION = 20; // 1s

    // 状态序列（使用int数组表示：0=红, 1=蓝, 2=全灭, 3=全亮）
    private static final int[] STATE_SEQUENCE;
    private static final int[] STATE_DURATIONS;

    static {
        java.util.List<Integer> sequence = new java.util.ArrayList<>();
        java.util.List<Integer> durations = new java.util.ArrayList<>();

        // (红0.25s + alloff0.25s) * 4
        for (int i = 0; i < 4; i++) {
            sequence.add(0); // 红
            durations.add(FLASH_DURATION);
            sequence.add(2); // 全灭
            durations.add(FLASH_DURATION);
        }

        // (蓝0.25s + alloff0.25s) * 4
        for (int i = 0; i < 4; i++) {
            sequence.add(1); // 蓝
            durations.add(FLASH_DURATION);
            sequence.add(2); // 全灭
            durations.add(FLASH_DURATION);
        }

        // (all0.25s + alloff0.25s) * 4
        for (int i = 0; i < 4; i++) {
            sequence.add(3); // 全亮
            durations.add(FLASH_DURATION);
            sequence.add(2); // 全灭
            durations.add(FLASH_DURATION);
        }

        // 最后alloff 1s
        sequence.add(2); // 全灭
        durations.add(FINAL_OFF_DURATION);

        STATE_SEQUENCE = sequence.stream().mapToInt(Integer::intValue).toArray();
        STATE_DURATIONS = durations.stream().mapToInt(Integer::intValue).toArray();
    }

    // 序列索引：直接记录当前在 STATE_SEQUENCE 中的位置，避免靠状态反推导致的重复状态无法区分
    public static final IntProperty INDEX = IntProperty.of("index", 0, STATE_SEQUENCE.length - 1);

    public InstrumentStrobeLight(Settings settings) {
        super(settings.luminance(state -> state.get(STATE) == State.ALL_OFF ? 0 : 15));
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(STATE, State.ALL_OFF)
                .with(INDEX, 1));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STATE, INDEX);
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
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            // 从当前序列位置开始，安排下一次 tick
            world.scheduleBlockTick(pos, this, STATE_DURATIONS[state.get(INDEX)]);
        }
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient) {
            // 直接读取当前序列索引，推进到下一步
            int currentIndex = state.get(INDEX);
            int nextIndex = (currentIndex + 1) % STATE_SEQUENCE.length;

            // 更新方块状态
            BlockState nextState = getStateForSequenceIndex(nextIndex, state);
            world.setBlockState(pos, nextState, Block.NOTIFY_ALL);

            // 安排下一个tick
            world.scheduleBlockTick(pos, this, STATE_DURATIONS[nextIndex]);
        }
    }

    // 根据序列索引获取方块状态
    private BlockState getStateForSequenceIndex(int index, BlockState currentState) {
        int seqState = STATE_SEQUENCE[index];
        BlockState state = currentState.with(INDEX, index);
        return switch (seqState) {
            case 0 -> // 红
                    state.with(STATE, State.RED);
            case 1 -> // 蓝
                    state.with(STATE, State.BLUE);
            case 2 -> // 全灭
                    state.with(STATE, State.ALL_OFF);
            case 3 -> // 全亮
                    state.with(STATE, State.ALL);
            default -> state;
        };
    }

    public enum State implements StringIdentifiable {
        RED("red"),
        BLUE("blue"),
        ALL_OFF("all_off"),
        ALL("all");

        private final String name;

        State(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
