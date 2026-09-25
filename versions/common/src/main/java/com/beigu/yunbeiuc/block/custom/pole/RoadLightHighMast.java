package com.beigu.yunbeiuc.block.custom.pole;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.InteractionHand;
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

public class RoadLightHighMast extends Block {
    public static final EnumProperty<TFType> TF_TYPE = EnumProperty.create("tf_type", TFType.class);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    private static final VoxelShape SHAPE = Stream.of(
            Block.box(6, 0, 6, 10, 16, 10),
            Block.box(-6, 7.5, -5, 22, 8.5, 21),
            Block.box(5, 8.5, -7, 11, 15.5, -3),
            Block.box(20, 8.5, 5, 24, 15.5, 11),
            Block.box(6, 8.5, 19, 12, 15.5, 23),
            Block.box(-8, 8.5, 5, -4, 15.5, 11)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter world, List<Component> tooltip, TooltipFlag options) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("block.yunbeiuc.road_light.tooltip"));
        super .appendHoverText(stack, world, tooltip, options);
    }

    public RoadLightHighMast(BlockBehaviour.Properties properties) {
        super(properties.lightLevel(state -> state.getValue(LIT) ? 15 : 0));
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, true).setValue(TF_TYPE, TFType.ON));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, TF_TYPE);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);
        // 直接使用 ModItems.WAND 判断是否为魔杖
        if (!world .isClientSide) {
            if (heldItem.getItem() == ModItems.WAND.get()) {
                boolean newLitState = !state.getValue(LIT);
                TFType newLightState = state.getValue(TF_TYPE).next();
                world.setBlock(pos, state.setValue(LIT, newLitState).setValue(TF_TYPE, newLightState), VersionServices.blocks().updateAll());
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
