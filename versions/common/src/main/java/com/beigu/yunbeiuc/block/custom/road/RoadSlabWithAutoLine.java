package com.beigu.yunbeiuc.block.custom.road;

import com.beigu.yunbeiuc.block.RoadBlocks;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.world.level.block.AirBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

public abstract class RoadSlabWithAutoLine extends AbstractRoadSlabBlock {

    private final RoadWithAutoLine.RoadAutoLineType type;

    public RoadSlabWithAutoLine(BlockBehaviour.Properties properties, RoadWithAutoLine.RoadAutoLineType type) {
        super(properties);
        this.type = type;
    }

    @Override
    protected Kind kind() {
        return Kind.NONE;
    }

    @SuppressWarnings("deprecation")
    @Override
    public InteractionResult useCompat(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        final InteractionResult result = super.useCompat(state, world, pos, player, hand, hit);
        if (result == InteractionResult.FAIL) {
            return result;
        }
        final Item item = player.getItemInHand(hand).getItem();
        if (item instanceof BlockItem blockItem
            && (blockItem.getBlock() instanceof RoadWithAutoLine || blockItem.getBlock() instanceof RoadSlabWithAutoLine)
            && !Direction.Plane.VERTICAL.test(hit.getDirection())) {
            return InteractionResult.PASS;
        }
        world.setBlock(pos, convertToSlab(state, RoadWithAutoLine.tryMakeState(type, getConnectionStateMap(world, pos), state, pos)), 2);
        return InteractionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborChanged(state, world, pos, sourceBlock, sourcePos, notify);
        if (!sourcePos.equals(pos.above())
            && !sourcePos.equals(pos.below())
            && !(world.getBlockState(sourcePos).getBlock() instanceof AirBlock)) {
            world.setBlock(pos, convertToSlab(state, RoadWithAutoLine.tryMakeState(type, getConnectionStateMap(world, pos), state, pos)), 2);
        }
    }

    private EnumMap<Direction, RoadWithAutoLine.RoadConnectionState> getConnectionStateMap(LevelAccessor world, BlockPos pos0) {
        final EnumMap<Direction, RoadWithAutoLine.RoadConnectionState> connectionStateMap = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            RoadWithAutoLine.RoadConnectionState state = null;
            for (BlockPos pos : new BlockPos[]{pos0, pos0.above(), pos0.below()}) {
                final BlockState nextState = world.getBlockState(pos.relative(direction, 1));
                final RoadWithAutoLine.RoadLineInfo info = RoadWithAutoLine.getBlockInfo().get(getBaseVariant(nextState.getBlock()));
                if (info != null) {
                    state = info.getValue(nextState, direction.getOpposite());
                    break;
                }
            }
            connectionStateMap.put(direction, state == null ? RoadWithAutoLine.RoadConnectionState.empty() : state);
        }
        return connectionStateMap;
    }

    private BlockState convertToSlab(BlockState currentState, BlockState resultState) {
        final Block slab = SlabMapHolder.SLAB.get(resultState.getBlock());
        if (slab == null) {
            return resultState;
        }
        BlockState newState = RoadOrientation.carryOver(resultState, slab.defaultBlockState());
        newState = newState.setValue(TYPE, currentState.getValue(TYPE));
        if (currentState.getValue(WATERLOGGED)) {
            newState = newState.setValue(WATERLOGGED, true);
        }
        return newState;
    }

    private static Block getBaseVariant(Block block) {
        final Block base = SlabMapHolder.BASE.get(block);
        return base == null ? block : base;
    }

    private static final class SlabMapHolder {
        static final Map<Block, Block> SLAB = new HashMap<>();
        static final Map<Block, Block> BASE = new HashMap<>();

        static {
            put(RoadBlocks.ROAD_BLOCK, RoadBlocks.ROAD_SLAB_BLOCK);
            put(RoadBlocks.ROAD_WITH_WHITE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_DOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_DOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_THICK_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_THICK_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_DOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_DOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_THICK_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_THICK_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_YELLOW_DOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_YELLOW_DOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_HALF_DOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_HALF_DOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_HALF_DOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_HALF_DOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_OFFSET_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_OFFSET_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_OFFSET_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_YELLOW_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_YELLOW_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITETHICK_NORMAL_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITETHICK_YELLOW_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITETHICK_YELLOWDOUBLE_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_YELLOWDOUBLE_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_BEVEL_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_BEVEL_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_BEVEL_DOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_BEVEL_DOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_BEVEL_THICK_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_BEVEL_THICK_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_BEVEL_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_BEVEL_DOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_THICK_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_BEVEL_THICK_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_OFFSET_OUT_BEVEL_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_OFFSET_IN_BEVEL_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT, RoadBlocks.ROAD_SLAB_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_OUT);
            put(RoadBlocks.ROAD_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN, RoadBlocks.ROAD_SLAB_WITH_WHITE_RIGHTANGLE_LINE_OFFSET_IN);
            put(RoadBlocks.ROAD_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_OFFSET_OUT_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_OFFSET_IN_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT, RoadBlocks.ROAD_SLAB_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_OUT);
            put(RoadBlocks.ROAD_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN, RoadBlocks.ROAD_SLAB_WITH_YELLOW_RIGHTANGLE_LINE_OFFSET_IN);
            put(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_OFFSET_OUT_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_OFFSET_IN_RIGHTANGLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_TSHAPE_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_TSHAPE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_DOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_TSHAPE_DOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_THICK_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_TSHAPE_THICK_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_DOUBLE_TSHAPE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_DOUBLE_TSHAPE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_THICK_TSHAPE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOW_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_TSHAPE_YELLOW_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_WHITE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_TSHAPE_WHITE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_TSHAPE_YELLOWDOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_THICK_TSHAPE_YELLOW_LINE);
            put(RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITETHICK_TSHAPE_YELLOWDOUBLE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_TSHAPE_OFFSET_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_TSHAPE_OFFSET_LINE);
            put(RoadBlocks.ROAD_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITEDOUBLE_TSHAPE_OFFSET_LINE);
            put(RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITETHICK_TSHAPE_OFFSET_LINE);
            put(RoadBlocks.ROAD_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITETHICK_TSHAPE_OFFSET_YELLOW_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_TSHAPE_OFFSET_WHITE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_TSHAPE_OFFSET_YELLOW_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_BEVEL_DB_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_BEVEL_DB_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_BEVEL_DB_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_BEVEL_DB_LINE);
            put(RoadBlocks.ROAD_WITH_WHITENORMAL_AND_BEVEL_DB_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITENORMAL_AND_BEVEL_DB_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOWNORMAL_AND_BEVEL_DB_LINE);
            put(RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITENORMAL_BEVEL_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOWNORMAL_BEVEL_LINE);
            put(RoadBlocks.ROAD_WITH_WHITENORMAL_BEVEL_YELLOW_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITENORMAL_BEVEL_YELLOW_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOWNORMAL_BEVEL_WHITE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITETHICK_BEVEL_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOWTHICK_BEVEL_LINE);
            put(RoadBlocks.ROAD_WITH_WHITETHICK_BEVEL_YELLOW_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITETHICK_BEVEL_YELLOW_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOWTHICK_BEVEL_WHITE_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOWTHICK_BEVEL_WHITE_LINE);
            put(RoadBlocks.ROAD_WITH_WHITE_CROSS_LINE, RoadBlocks.ROAD_SLAB_WITH_WHITE_CROSS_LINE);
            put(RoadBlocks.ROAD_WITH_YELLOW_CROSS_LINE, RoadBlocks.ROAD_SLAB_WITH_YELLOW_CROSS_LINE);
        }

        private static void put(RegistrySupplier<Block> base, RegistrySupplier<Block> slab) {
            final Block baseBlock = base.get();
            final Block slabBlock = slab.get();
            SLAB.put(baseBlock, slabBlock);
            BASE.put(slabBlock, baseBlock);
        }
    }
}
