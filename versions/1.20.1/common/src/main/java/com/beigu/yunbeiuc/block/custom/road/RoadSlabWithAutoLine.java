package com.beigu.yunbeiuc.block.custom.road;

import com.beigu.yunbeiuc.block.RoadBlocks;
import com.beigu.yunbeiuc.block.custom.DirectionSlabBlock;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.block.AirBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.enums.SlabType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 自动标线道路台阶方块（Slab 版）的抽象基类。
 * <p>
 * Slab 结构仿照 {@link DirectionSlabBlock}（含 {@code FACING / TYPE / WATERLOGGED} 属性与半格形状），
 * 自动连接逻辑复用 {@link RoadWithAutoLine} 的 {@code makeState} 状态机；与普通版不同的是，
 * 转换结果方块会被替换为对应的道路台阶方块，并保留当前的 {@code TYPE}（上/下/双层）与 {@code WATERLOGGED}。
 * <p>
 * 连接类型（斜线 / 直角）由构造参数 {@link RoadWithAutoLine.RoadAutoLineType} 决定，
 * 具体方块由子类 {@link RoadSlabWithAutoBevelLine} 与 {@link RoadSlabWithAutoRightangleLine} 指定。
 */
public abstract class RoadSlabWithAutoLine extends DirectionSlabBlock {

    private final RoadWithAutoLine.RoadAutoLineType type;

    public RoadSlabWithAutoLine(Settings settings, RoadWithAutoLine.RoadAutoLineType type) {
        super(settings);
        this.type = type;
    }

    // ============================================================
    // 触发转换
    // ============================================================

    @SuppressWarnings("deprecation")
    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        final ActionResult result = super.onUse(state, world, pos, player, hand, hit);
        if (result == ActionResult.FAIL) {
            return result;
        }
        final Item item = player.getStackInHand(hand).getItem();
        if (item instanceof BlockItem blockItem
            && (blockItem.getBlock() instanceof RoadWithAutoLine || blockItem.getBlock() instanceof RoadSlabWithAutoLine)
            && !Direction.Type.VERTICAL.test(hit.getSide())) {
            // 手持自动标线方块时放行，允许继续放置。
            return ActionResult.PASS;
        }
        world.setBlockState(pos, convertToSlab(state, RoadWithAutoLine.tryMakeState(type, getConnectionStateMap(world, pos), state, pos)), 2);
        return ActionResult.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        super.neighborUpdate(state, world, pos, sourceBlock, sourcePos, notify);
        // 屏蔽上下方的更新与因方块消失（变空气）产生的更新。
        if (!sourcePos.equals(pos.up())
            && !sourcePos.equals(pos.down())
            && !(world.getBlockState(sourcePos).getBlock() instanceof AirBlock)) {
            world.setBlockState(pos, convertToSlab(state, RoadWithAutoLine.tryMakeState(type, getConnectionStateMap(world, pos), state, pos)), 2);
        }
    }

    // ============================================================
    // 连接状态检测（把台阶邻居归一化为普通方块后查询共享映射）
    // ============================================================

    private EnumMap<Direction, RoadWithAutoLine.RoadConnectionState> getConnectionStateMap(WorldAccess world, BlockPos pos0) {
        final EnumMap<Direction, RoadWithAutoLine.RoadConnectionState> connectionStateMap = new EnumMap<>(Direction.class);
        for (Direction direction : Direction.Type.HORIZONTAL) {
            RoadWithAutoLine.RoadConnectionState state = null;
            for (BlockPos pos : new BlockPos[]{pos0, pos0.up(), pos0.down()}) {
                final BlockState nextState = world.getBlockState(pos.offset(direction, 1));
                final RoadWithAutoLine.RoadLineInfo info = RoadWithAutoLine.getBlockInfo().get(getBaseVariant(nextState.getBlock()));
                if (info != null) {
                    state = info.get(nextState, direction.getOpposite());
                    break;
                }
            }
            connectionStateMap.put(direction, state == null ? RoadWithAutoLine.RoadConnectionState.empty() : state);
        }
        return connectionStateMap;
    }

    // ============================================================
    // 将自动逻辑产出的普通方块状态替换为对应的台阶方块状态
    // ============================================================

    private BlockState convertToSlab(BlockState currentState, BlockState resultState) {
        final Block slab = SlabMapHolder.SLAB.get(resultState.getBlock());
        if (slab == null) {
            return resultState;
        }
        BlockState newState = slab.getDefaultState();
        if (newState.contains(FACING)) {
            final Direction facing = resultState.contains(FACING) ? resultState.get(FACING)
                : currentState.contains(FACING) ? currentState.get(FACING) : Direction.NORTH;
            newState = newState.with(FACING, facing);
        }
        if (newState.contains(TYPE)) {
            newState = newState.with(TYPE, currentState.contains(TYPE) ? currentState.get(TYPE) : SlabType.BOTTOM);
        }
        if (newState.contains(WATERLOGGED) && currentState.contains(WATERLOGGED)) {
            newState = newState.with(WATERLOGGED, currentState.get(WATERLOGGED));
        }
        return newState;
    }

    /** 把台阶方块映射回对应的普通方块；不是台阶方块时返回自身。 */
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
