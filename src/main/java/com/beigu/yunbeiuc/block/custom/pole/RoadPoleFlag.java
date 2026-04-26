package com.beigu.yunbeiuc.block.custom.pole;

import com.beigu.yunbeiuc.block.MunicipalBlocks;
import com.beigu.yunbeiuc.entity.FlagBlockEntity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.screen.FlagSelectionScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoadPoleFlag extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<PoleType> POLE_TYPE = EnumProperty.of("pole_type", PoleType.class);

    private static final VoxelShape SHAPE_N = VoxelShapes.combineAndSimplify(Block.createCuboidShape(5, 0, 5, 11, 16, 11), Block.createCuboidShape(-9.75, -5.25, 7.75, 25.75, 21.25, 8.25), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_S = VoxelShapes.combineAndSimplify(Block.createCuboidShape(5, 0, 5, 11, 16, 11), Block.createCuboidShape(-9.75, -5.25, 7.75, 25.75, 21.25, 8.25), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_E = VoxelShapes.combineAndSimplify(Block.createCuboidShape(5, 0, 5, 11, 16, 11), Block.createCuboidShape(7.75, -5.25, -9.75, 8.25, 21.25, 25.75), BooleanBiFunction.OR);
    private static final VoxelShape SHAPE_W = VoxelShapes.combineAndSimplify(Block.createCuboidShape(5, 0, 5, 11, 16, 11), Block.createCuboidShape(7.75, -5.25, -9.75, 8.25, 21.25, 25.75), BooleanBiFunction.OR);

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.road_pole_flag.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    public RoadPoleFlag(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(POLE_TYPE, PoleType.ROAD_POLE));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> stateManager) {
        stateManager.add(FACING, POLE_TYPE);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        PoleType poleType = getPoleTypeForPosition(world, pos);

        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(POLE_TYPE, poleType);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
                                                WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (direction == Direction.DOWN) {
            PoleType correctType = getPoleTypeForPosition((World) world, pos);
            if (state.get(POLE_TYPE) != correctType) {
                return state.with(POLE_TYPE, correctType);
            }
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }

    private PoleType getPoleTypeForPosition(World world, BlockPos pos) {
        Block blockBelow = world.getBlockState(pos.down()).getBlock();
        if (blockBelow == MunicipalBlocks.ROAD_POLE_LIGHT_FOUNDATIONS ||
                blockBelow == MunicipalBlocks.ROAD_POLE_LIGHT_FOUNDATIONS_SLAB ||
                blockBelow == MunicipalBlocks.ROAD_POLE_LIGHT_LONGITUDINAL) {
            return PoleType.ROAD_POLE_LIGHT;
        }
        return PoleType.ROAD_POLE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = player.getStackInHand(hand).getItem();
        if (item == ModItems.WAND) {
            if (world.isClient()) {
                openFlagScreen(pos);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void openFlagScreen(BlockPos pos) {
        MinecraftClient.getInstance().setScreen(new FlagSelectionScreen(Text.literal("选择旗帜"), pos));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return switch (state.get(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
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
        return new FlagBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }


    public enum PoleType implements StringIdentifiable {
        ROAD_POLE("road_pole"),
        ROAD_POLE_LIGHT("road_pole_light");

        private final String name;

        PoleType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return name;
        }
    }
}