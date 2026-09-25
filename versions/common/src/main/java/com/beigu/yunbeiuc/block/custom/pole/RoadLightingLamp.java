package com.beigu.yunbeiuc.block.custom.pole;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionResult;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Stream;

public class RoadLightingLamp extends Block {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("block.yunbeiuc.road_lighting_lamp.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    private static final VoxelShape SHAPE_N = Shapes.join(Block.box(5.5, 5.5, 0, 10.5, 10, 16), Block.box(4.75, 10, 5, 11.25, 18, 11), BooleanOp.OR);
    private static final VoxelShape SHAPE_S = Shapes.join(Block.box(5.5, 5.5, 0, 10.5, 10, 16), Block.box(4.75, 10, 5, 11.25, 18, 11), BooleanOp.OR);
    private static final VoxelShape SHAPE_E = Shapes.join(Block.box(0, 5.5, 5.5, 16, 10.5, 10.5), Block.box(5, 10, 4.75, 11, 18, 11.25), BooleanOp.OR);
    private static final VoxelShape SHAPE_W = Shapes.join(Block.box(0, 5.5, 5.5, 16, 10.5, 10.5), Block.box(5, 10, 4.75, 11, 18, 11.25), BooleanOp.OR);

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<LightTFState> LIGHT_TF_STATE = EnumProperty.create("light_tf_state", LightTFState.class);

    public RoadLightingLamp(BlockBehaviour.Properties properties) {
        super(properties.lightLevel(state -> state.getValue(LIT) ? 15 : 0));
        this.registerDefaultState(
                stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, true).setValue(LIGHT_TF_STATE, LightTFState.TRUE)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case WEST -> SHAPE_W;
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING,LIT,LIGHT_TF_STATE);
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
        return defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        // 直接使用 ModItems.WAND 判断是否为魔杖
        if (!world .isClientSide) {
            if (heldItem.getItem() == ModItems.WAND.get()) {
                boolean newLitState = !state.getValue(LIT);
                LightTFState newLightState = state.getValue(LIGHT_TF_STATE).next();
                world.setBlock(pos, state.setValue(LIT, newLitState).setValue(LIGHT_TF_STATE, newLightState), VersionServices.blocks().updateAll());
            }
        }
        return InteractionResult.SUCCESS;
    }

    public enum LightTFState implements StringRepresentable {
        TRUE("true"),
        FALSE("false");

        private final String name;

        LightTFState(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public LightTFState next() {
            return switch (this) {
                case TRUE -> FALSE;
                case FALSE -> TRUE;
            };
        }
    }
}
