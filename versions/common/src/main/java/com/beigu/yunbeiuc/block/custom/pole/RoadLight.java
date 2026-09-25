package com.beigu.yunbeiuc.block.custom.pole;

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
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
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

public class RoadLight extends Block {
    public static final BooleanProperty LIT = BlockStateProperties.LIT;
    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final EnumProperty<TFType> TF_TYPE = EnumProperty.create("tf_type", TFType.class);

    private static final VoxelShape SHAPE_N = Block.box(4.5, 2.5, 1.5, 11.5, 8.5, 13.5);
    private static final VoxelShape SHAPE_E = Block.box(1.5, 2.5, 4.5, 13.5, 8.5, 11.5);
    private static final VoxelShape SHAPE_S = Block.box(4.5, 2.5, 1.5, 11.5, 8.5, 13.5);
    private static final VoxelShape SHAPE_W = Block.box(1.5, 2.5, 4.5, 13.5, 8.5, 11.5);

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("block.yunbeiuc.road_light.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }

    public RoadLight(BlockBehaviour.Properties properties) {
        super(properties.lightLevel(state -> state.getValue(LIT) ? 15 : 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(LIT, true).setValue(TF_TYPE, TFType.ON));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case SOUTH -> SHAPE_S;
            case EAST -> SHAPE_E;
            case WEST -> SHAPE_W;
            default -> SHAPE_N;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING, TF_TYPE);
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
        return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        // 直接使用 ModItems.WAND 判断是否为魔杖
        if (!world .isClientSide) {
            if (heldItem.is(ModItems.WAND.get())) {
                boolean newLitState = !state.getValue(LIT);
                TFType newLightState = state.getValue(TF_TYPE).next();
                world.setBlock(pos, state.setValue(LIT, newLitState).setValue(TF_TYPE, newLightState), Block.UPDATE_ALL);
            }
        }
        return InteractionResult.SUCCESS;
    }

    public enum TFType implements StringRepresentable {
        ON("on"),
        OFF("off");

        private final String name;

        TFType(String name) {
            this.name = name;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }

        public TFType next() {
            return switch (this) {
                case ON -> OFF;
                case OFF -> ON;
            };
        }
    }
}
