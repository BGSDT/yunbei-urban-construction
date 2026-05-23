package com.beigu.yunbeiuc.block.custom.pole;

import com.beigu.yunbeiuc.item.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
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
import java.util.stream.Stream;

public class RoadLightHighMast extends Block {
    public static final EnumProperty<TFType> TF_TYPE = EnumProperty.of("tf_type", TFType.class);
    public static final BooleanProperty LIT = Properties.LIT;

    private static final VoxelShape SHAPE = Stream.of(
            Block.createCuboidShape(6, 0, 6, 10, 16, 10),
            Block.createCuboidShape(-6, 7.5, -5, 22, 8.5, 21),
            Block.createCuboidShape(5, 8.5, -7, 11, 15.5, -3),
            Block.createCuboidShape(20, 8.5, 5, 24, 15.5, 11),
            Block.createCuboidShape(6, 8.5, 19, 12, 15.5, 23),
            Block.createCuboidShape(-8, 8.5, 5, -4, 15.5, 11)
    ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, BooleanBiFunction.OR)).get();

    @Override
    public void appendTooltip(ItemStack stack, @Nullable BlockView world, List<Text> tooltip, TooltipContext options) {
        tooltip.add(Text.translatable("block.yunbeiuc.road_light.tooltip"));
        super.appendTooltip(stack, world, tooltip, options);
    }

    public RoadLightHighMast(Settings settings) {
        super(settings.luminance(state -> state.get(LIT) ? 15 : 0));
        this.setDefaultState(this.getStateManager().getDefaultState()
                .with(LIT, true)
                .with(TF_TYPE, TFType.ON));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, TF_TYPE);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getStackInHand(hand);
        // 直接使用 ModItems.WAND 判断是否为魔杖
        if (!world.isClient()) {
            if (heldItem.isOf(ModItems.WAND)) {
                boolean newLitState = !state.get(LIT);
                TFType newLightState = state.get(TF_TYPE).next();
                world.setBlockState(pos, state.with(LIT, newLitState).with(TF_TYPE, newLightState));
            }
        }
        return ActionResult.SUCCESS;
    }

    public enum TFType implements StringIdentifiable {
        ON("on"),
        OFF("off");

        private final String name;

        TFType(String name) {
            this.name = name;
        }

        @Override
        public String asString() {
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