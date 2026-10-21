package com.beigu.yunbeiuc.block.custom.instrument;

import com.beigu.yunbeiuc.api.mapper.VersionServices;
import net.minecraft.world.level.block.Block;
import com.beigu.yunbeiuc.api.mapper.TickingBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;

public class InstrumentStrobeLight extends TickingBlock {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<State> STATE = EnumProperty.create("state", State.class);

    private static final VoxelShape SHAPE = Block.box(0, -0.5, 0, 16, 8.75, 16);

    private static final int FLASH_DURATION = 5;
    private static final int FINAL_OFF_DURATION = 20;

    private static final int[] STATE_SEQUENCE;
    private static final int[] STATE_DURATIONS;

    static {
        java.util.List<Integer> sequence = new java.util.ArrayList<>();
        java.util.List<Integer> durations = new java.util.ArrayList<>();

        for (int i = 0; i < 4; i++) {
            sequence.add(0);
            durations.add(FLASH_DURATION);
            sequence.add(2);
            durations.add(FLASH_DURATION);
        }

        for (int i = 0; i < 4; i++) {
            sequence.add(1);
            durations.add(FLASH_DURATION);
            sequence.add(2);
            durations.add(FLASH_DURATION);
        }

        for (int i = 0; i < 4; i++) {
            sequence.add(3);
            durations.add(FLASH_DURATION);
            sequence.add(2);
            durations.add(FLASH_DURATION);
        }

        sequence.add(2);
        durations.add(FINAL_OFF_DURATION);

        STATE_SEQUENCE = sequence.stream().mapToInt(Integer::intValue).toArray();
        STATE_DURATIONS = durations.stream().mapToInt(Integer::intValue).toArray();
    }

    public static final IntegerProperty INDEX = IntegerProperty.create("index", 0, STATE_SEQUENCE.length - 1);

    public InstrumentStrobeLight(BlockBehaviour.Properties properties) {
        super(properties.lightLevel(state -> state.getValue(STATE) == State.ALL_OFF ? 0 : 15));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(STATE, State.ALL_OFF).setValue(INDEX, 1));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, STATE, INDEX);
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
    public void onPlace(BlockState state, Level world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClientSide) {
            VersionServices.blocks().scheduleBlockTick(world, pos, this, STATE_DURATIONS[state.getValue(INDEX)]);
        }
    }

    @Override
    protected void tickCompat(BlockState state, ServerLevel world, BlockPos pos) {
        if (!world.isClientSide) {
            int currentIndex = state.getValue(INDEX);
            int nextIndex = (currentIndex + 1) % STATE_SEQUENCE.length;

            BlockState nextState = getStateForSequenceIndex(nextIndex, state);
            world.setBlock(pos, nextState, VersionServices.blocks().updateAll());

            VersionServices.blocks().scheduleBlockTick(world, pos, this, STATE_DURATIONS[nextIndex]);
        }
    }

    private BlockState getStateForSequenceIndex(int index, BlockState currentState) {
        int seqState = STATE_SEQUENCE[index];
        BlockState state = currentState.setValue(INDEX, index);
        return switch (seqState) {
            case 0 ->
                    state.setValue(STATE, State.RED);
            case 1 ->
                    state.setValue(STATE, State.BLUE);
            case 2 ->
                    state.setValue(STATE, State.ALL_OFF);
            case 3 ->
                    state.setValue(STATE, State.ALL);
            default -> state;
        };
    }

    public enum State implements StringRepresentable {
        RED("red"),
        BLUE("blue"),
        ALL_OFF("all_off"),
        ALL("all");

        private final String name;

        State(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}
