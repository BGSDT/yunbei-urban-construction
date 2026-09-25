package com.beigu.yunbeiuc.block.custom;

import net.minecraft.world.InteractionHand;
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

public class RisingBollard extends Block {
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("block.yunbeiuc.rising_bollard.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }
    private static final VoxelShape SHAPE_ON = Block.box(2, 0, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_OFF = Block.box(2, 0, 2, 14, 0.1, 14);

    public static final EnumProperty<LightTFState> LIGHT_TF_STATE = EnumProperty.create("light_tf_state", LightTFState.class);

    public RisingBollard(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(
                stateDefinition.any().setValue(LIGHT_TF_STATE, LightTFState.TRUE)
        );
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(LIGHT_TF_STATE)) {
            case TRUE -> SHAPE_ON;
            case FALSE -> SHAPE_OFF;
        };
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIGHT_TF_STATE);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        // 直接使用 ModItems.WAND 判断是否为魔杖
        if (!world .isClientSide) {
            if (heldItem.is(ModItems.WAND.get())) {
                LightTFState newLightState = state.getValue(LIGHT_TF_STATE).next();
                world.setBlock(pos, state.setValue(LIGHT_TF_STATE, newLightState), Block.UPDATE_ALL);
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

        // 状态循环： TRUE -> FALSE -> TRUE
        public LightTFState next() {
            return switch (this) {
                case TRUE -> FALSE;
                case FALSE -> TRUE;
            };
        }
    }
}
