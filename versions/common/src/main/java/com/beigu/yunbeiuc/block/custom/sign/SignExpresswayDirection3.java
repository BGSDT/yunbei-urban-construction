package com.beigu.yunbeiuc.block.custom.sign;

import com.beigu.yunbeiuc.api.mapper.EntityBlockCompat;

import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.beigu.yunbeiuc.block.custom.pole.RoadPoleHorizontal;
import com.beigu.yunbeiuc.block.custom.pole.RoadPoleLongitudinal;
import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import com.beigu.yunbeiuc.entity.SignExpresswayDirection3Entity;
import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.util.*;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class  SignExpresswayDirection3 extends EntityBlockCompat {
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<Type> TYPE = EnumProperty.create("type", Type.class);

    private static final VoxelShape SHAPE_POLE_L_N = Block.box(0, 0, 19.1, 16, 16, 20);
    private static final VoxelShape SHAPE_POLE_L_E = Block.box(-2.1, 0, 0, -1, 16, 16);
    private static final VoxelShape SHAPE_POLE_L_S = Block.box(0, 0, -2.1, 16, 16, -1);
    private static final VoxelShape SHAPE_POLE_L_W = Block.box(20, 0, 0, 21.1, 16, 16);

    private static final VoxelShape SHAPE_POLE_H_N = Block.box(0, 0, 20.1, 16, 16, 21);
    private static final VoxelShape SHAPE_POLE_H_E = Block.box(-3.1, 0, 0, -2, 16, 16);
    private static final VoxelShape SHAPE_POLE_H_S = Block.box(0, 0, -3.1, 16, 16, -2);
    private static final VoxelShape SHAPE_POLE_H_W = Block.box(20, 0, 0, 21.1, 16, 16);

    private static final VoxelShape SHAPE_NORMAL_N = Block.box(0, 0, 15.1, 16, 16, 16);
    private static final VoxelShape SHAPE_NORMAL_S = Block.box(0, 0, 0, 16, 16, 0.9);
    private static final VoxelShape SHAPE_NORMAL_E = Block.box(0, 0, 0, 0.9, 16, 16);
    private static final VoxelShape SHAPE_NORMAL_W = Block.box(15.1, 0, 0, 16, 16, 16);

    public SignExpresswayDirection3(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(TYPE, Type.NORMAL));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("block.yunbeiuc.sign_text.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, TYPE);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos,
                              Player player, InteractionHand hand, BlockHitResult hit) {
        Item item = player.getItemInHand(hand).getItem();
        if (item == ModItems.WAND.get()) {
            if (world .isClientSide) {
                openTextDisplayScreen(pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @net.fabricmc.api.Environment(net.fabricmc.api.EnvType.CLIENT)
    private void openTextDisplayScreen(BlockPos pos) {
        BlockEntity blockEntity = Minecraft.getInstance().level.getBlockEntity(pos);
        if (blockEntity instanceof CustomSignBlockEntity signEntity) {
            Minecraft.getInstance().setScreen(new com.beigu.yunbeiuc.screen.TextDisplayScreen(signEntity));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        Direction facing = state.getValue(FACING);
        Type type = state.getValue(TYPE);
        return switch (type) {
            case POLE_L -> switch (facing) {
                case SOUTH -> SHAPE_POLE_L_S;
                case EAST -> SHAPE_POLE_L_E;
                case WEST -> SHAPE_POLE_L_W;
                default -> SHAPE_POLE_L_N;
            };
            case POLE_H -> switch (facing) {
                case SOUTH -> SHAPE_POLE_H_S;
                case EAST -> SHAPE_POLE_H_E;
                case WEST -> SHAPE_POLE_H_W;
                default -> SHAPE_POLE_H_N;
            };
            case NORMAL -> switch (facing) {
                case SOUTH -> SHAPE_NORMAL_S;
                case EAST -> SHAPE_NORMAL_E;
                case WEST -> SHAPE_NORMAL_W;
                default -> SHAPE_NORMAL_N;
            };
        };
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        Level world = ctx.getLevel();
        BlockPos pos = ctx.getClickedPos();
        Direction facing = ctx.getHorizontalDirection().getOpposite();

        BlockPos behindPos = pos.relative(facing.getOpposite());
        BlockState behindState = world.getBlockState(behindPos);

        Type type = determineType(behindState);

        return this.defaultBlockState().setValue(FACING, facing).setValue(TYPE, type);
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
    public BlockState updateShape(BlockState state, Direction direction,
                                                BlockState neighborState, LevelAccessor world,
                                                BlockPos pos, BlockPos neighborPos) {
        Direction facing = state.getValue(FACING);
        if (direction == facing.getOpposite()) {
            Type newType = determineType(neighborState);
            if (newType != state.getValue(TYPE)) {
                return state.setValue(TYPE, newType);
            }
        }
        return super.updateShape(state, direction, neighborState, world, pos, neighborPos);
    }

    private Type determineType(BlockState behindState) {
        Block behindBlock = behindState.getBlock();
        if (behindBlock instanceof RoadPoleHorizontal) return Type.POLE_H;
        else if (behindBlock instanceof RoadPoleLongitudinal) return Type.POLE_L;
        else return Type.NORMAL;
    }

    @Override
    protected BlockEntity newBlockEntityCompat(BlockPos pos, BlockState state) {
        return new SignExpresswayDirection3Entity(pos, state);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    public enum Type implements StringRepresentable {
        POLE_L("pole_l"),
        POLE_H("pole_h"),
        NORMAL("normal");

        private final String name;
        Type(String name) { this.name = name; }
        @Override
        public String getSerializedName() { return this.name; }
    }
}
