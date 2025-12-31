package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.block.custom.data.TFType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.random.Random;

public class EntryGateBlock extends Block {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<TFType> TF_TYPE =
            EnumProperty.of("tf_type", TFType.class);

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    public EntryGateBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(TF_TYPE, TFType.FALSE));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, TF_TYPE);
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
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // 仅在服务器端处理状态变更逻辑并发送聊天提示
        ItemStack held = player.getStackInHand(hand);

        // 如果不是木棍点击，给出提示（在客户端和服务端都能看到提示）
        if (!held.isOf(Items.STICK)) {
            if (!world.isClient) {
                player.sendMessage(Text.literal("请使用木棍点击以激活闸门"), false);
            } else {
                // 客户端也显示提示，避免服务端未同步时无提示
                player.sendMessage(Text.literal("请使用木棍点击以激活闸门"), true);
            }
            return ActionResult.SUCCESS;
        }

        // 如果已经处于 TRUE，则表示在 5 秒倒计时中，拒绝再次激活
        TFType cur = state.get(TF_TYPE);
        if (cur == TFType.TRUE) {
            if (!world.isClient) {
                player.sendMessage(Text.literal("闸门正在冷却中，无法再次激活。请稍候。"), false);
            } else {
                player.sendMessage(Text.literal("闸门正在冷却中，无法再次激活。请稍候。"), true);
            }
            return ActionResult.SUCCESS;
        }

        // 到这里为木棍点击且当前为 FALSE，执行变为 TRUE 并在 5 秒后回退
        if (!world.isClient) {
            world.setBlockState(pos, state.with(TF_TYPE, TFType.TRUE), 3);
            // 发送聊天提示给玩家
            player.sendMessage(Text.literal("已激活闸门：5秒后自动关闭"), false);
            // 安排 100 tick 后触发 scheduled tick 来将状态复原为 FALSE
            try {
                world.scheduleBlockTick(pos, this, 100);
            } catch (NoSuchMethodError | UnsupportedOperationException e) {
                // 兼容性：如果运行时没有 scheduleBlockTick 方法，使用 ServerWorld 的 tick scheduler 不是可行的（访问权限限制），因此退回为不安排
                // 这会导致无法自动回退；日志提示（仅服务端）
                System.err.println("无法为方块安排延迟 tick：" + e.getMessage());
            }
        }

        return ActionResult.SUCCESS;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (state.get(TF_TYPE) == TFType.TRUE) {
            world.setBlockState(pos, state.with(TF_TYPE, TFType.FALSE), 3);
        }
    }
}