package com.beigu.yunbeiuc.block.custom;

import com.beigu.yunbeiuc.block.custom.pole.RoadLight;
import com.beigu.yunbeiuc.entity.RoadNameSignBlockEntity;
import com.beigu.yunbeiuc.entity.RoadPoleTextDisplayEntity;
import com.beigu.yunbeiuc.item.ModItems;
import com.beigu.yunbeiuc.screen.RoadNameSignScreen;
import com.beigu.yunbeiuc.screen.RoadPoleTextDisplayScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.enums.BedPart;
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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RoadNameSignBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<BGType> BG_TYPE = EnumProperty.of("bg_type", BGType.class);

    private static final VoxelShape SHAPE_N = Block.createCuboidShape(-10, 0, 6.25, 26, 16, 9.75);
    private static final VoxelShape SHAPE_E = Block.createCuboidShape(6.25, 0, -10, 9.75, 16, 26);
    private static final VoxelShape SHAPE_S = Block.createCuboidShape(-10, 0, 6.25, 26, 16, 9.75);
    private static final VoxelShape SHAPE_W = Block.createCuboidShape(6.25, 0, -10, 9.75, 16, 26);

    public RoadNameSignBlock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(BG_TYPE, BGType.BLUE));
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.road_name_sign.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, BG_TYPE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos,
                              PlayerEntity player, Hand hand, BlockHitResult hit) {
        Item item = player.getStackInHand(hand).getItem();
        if (item == ModItems.WAND.get()) {
            if (world.isClient()) {
                openTextDisplayScreen(pos);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Environment(EnvType.CLIENT)
    private void openTextDisplayScreen(BlockPos pos) {
        MinecraftClient.getInstance().setScreen(new RoadNameSignScreen(pos));
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
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        Direction facing = ctx.getHorizontalPlayerFacing().getOpposite();
        BGType bgType = switch (facing) {
            case EAST, WEST -> BGType.BLUE;
            case NORTH, SOUTH -> BGType.GREEN;
            default -> BGType.BLUE;
        };
        return this.getDefaultState().with(FACING, facing).with(BG_TYPE, bgType);
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
        return new RoadNameSignBlockEntity(pos, state);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    public enum BGType implements StringIdentifiable {
        BLUE("blue"),
        GREEN("green");

        private final String name;

        BGType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
            return this.name;
        }
    }
}
