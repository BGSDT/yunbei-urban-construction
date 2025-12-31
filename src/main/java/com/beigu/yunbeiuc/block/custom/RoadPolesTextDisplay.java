package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.entity.RoadPolesTextDisplayEntity;
import com.beigu.yunbeiuc.screen.RoadPolesTextDisplayScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.data.client.VariantSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class RoadPolesTextDisplay extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    
    // 根据朝向旋转碰撞箱
    private static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(-8, 12.75, 5, 24, 22.75, 11);
    private static final VoxelShape EAST_SHAPE = Block.createCuboidShape(5, 12.75, -8, 11, 22.75, 24);
    private static final VoxelShape SOUTH_SHAPE = Block.createCuboidShape(-8, 12.75, 5, 24, 22.75, 11);
    private static final VoxelShape WEST_SHAPE = Block.createCuboidShape(5, 12.75, -8, 11, 22.75, 24);

    public RoadPolesTextDisplay() {
        super(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)
            .hardness(2.0f)
            .nonOpaque());
        setDefaultState(getStateManager().getDefaultState().with(FACING, Direction.NORTH));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (world.isClient()) {
            openTextDisplayScreen(pos);
        }
        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void openTextDisplayScreen(BlockPos pos) {
        MinecraftClient.getInstance().setScreen(new RoadPolesTextDisplayScreen(pos));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getVoxelShape(state);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return getVoxelShape(state);
    }

    private VoxelShape getVoxelShape(BlockState state) {
        Direction direction = state.get(FACING);
        switch (direction) {
            case NORTH:
                return NORTH_SHAPE;
            case SOUTH:
                return SOUTH_SHAPE;
            case EAST:
                return EAST_SHAPE;
            case WEST:
                return WEST_SHAPE;
            default:
                return NORTH_SHAPE;
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite());
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
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new RoadPolesTextDisplayEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }
}