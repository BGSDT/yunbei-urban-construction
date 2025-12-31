package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.block.custom.data.GantryFrameConnectionType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class GantryFrameConnectionBlock extends Block {
    public static final EnumProperty<GantryFrameConnectionType> CONNECTION_TYPE =
            EnumProperty.of("connection_type", GantryFrameConnectionType.class);

    // 添加一个标记来防止循环更新
    private boolean isManualUpdate = false;

    public GantryFrameConnectionBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(CONNECTION_TYPE, GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(CONNECTION_TYPE);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        // 如果是手动更新，不执行自动逻辑
        if (isManualUpdate) {
            return state;
        }

        if (direction == Direction.DOWN) {
            return this.updateConnectionType(state, world, pos);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        super.onBlockAdded(state, world, pos, oldState, notify);
        if (!isManualUpdate) {
            world.setBlockState(pos, updateConnectionType(state, world, pos), Block.NOTIFY_ALL);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = player.getStackInHand(hand).getItem();

        // 检查是否使用木棍点击
        if (item == Items.STICK) {
            if (!world.isClient) {
                // 设置手动更新标记
                isManualUpdate = true;

                GantryFrameConnectionType currentType = state.get(CONNECTION_TYPE);
                GantryFrameConnectionType newType = switch (currentType) {
                    case GANTRY_FRAME_CONNECTION_1 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_3;
                    case GANTRY_FRAME_CONNECTION_2 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_4;
                    case GANTRY_FRAME_CONNECTION_3 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1;
                    case GANTRY_FRAME_CONNECTION_4 -> GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_2;
                };

                world.setBlockState(pos, state.with(CONNECTION_TYPE, newType), Block.NOTIFY_ALL);

                // 重置手动更新标记
                isManualUpdate = false;
            }
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    private BlockState updateConnectionType(BlockState state, WorldAccess world, BlockPos pos) {
        BlockPos belowPos = pos.down();
        BlockState belowState = world.getBlockState(belowPos);

        // 如果下方不是 GantryFrameSideBlock，默认为 CONNECTION_1
        if (!(belowState.getBlock() instanceof GantryFrameSideBlock)) {
            return state.with(CONNECTION_TYPE, GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1);
        }

        // 获取下方方块的枚举值
        GantryFrameConnectionType newType;
        if (belowState.get(GantryFrameSideBlock.FRAME_TYPE).asString().equals("gantry_frame_side_1")) {
            newType = GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_2;
        } else if (belowState.get(GantryFrameSideBlock.FRAME_TYPE).asString().equals("gantry_frame_side_2")) {
            newType = GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1;
        } else {
            newType = GantryFrameConnectionType.GANTRY_FRAME_CONNECTION_1;
        }

        return state.with(CONNECTION_TYPE, newType);
    }
}