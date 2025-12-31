package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.block.custom.data.CrashBarrierConcreteType;
import com.beigu.yunbeiuc.block.custom.data.SignCancelSpeedLimit;
import com.beigu.yunbeiuc.entity.CrashBarrierConcreteEntity;
import com.beigu.yunbeiuc.entity.SignCancelSpeedLimitBlockEntity;
import com.beigu.yunbeiuc.screen.SignCancelSpeedLimitScreen;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.Items;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
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
import org.jetbrains.annotations.Nullable;

public class CrashBarrierConcrete extends BlockWithEntity {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<CrashBarrierConcreteType> CRASH_BARRIER_CONCRETE = EnumProperty.of("crash_barrier_concrete", CrashBarrierConcreteType.class);

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 16);

    public CrashBarrierConcrete(Settings settings) {
        super(settings);
        this.setDefaultState(this.getStateManager().getDefaultState().with(FACING, Direction.NORTH)
                .with(CRASH_BARRIER_CONCRETE, CrashBarrierConcreteType.CRASH_BARRIER_CONCRETE));
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CrashBarrierConcreteEntity(pos,state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, CRASH_BARRIER_CONCRETE);
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
        Item item = player.getStackInHand(hand).getItem();
        if (item == Items.STICK && !world.isClient) {
            CrashBarrierConcreteType current = state.get(CRASH_BARRIER_CONCRETE);
            CrashBarrierConcreteType next = current.next();
            world.setBlockState(pos, state.with(CRASH_BARRIER_CONCRETE, next));
            return ActionResult.SUCCESS;
        }
        return ActionResult.SUCCESS;
    }
}
