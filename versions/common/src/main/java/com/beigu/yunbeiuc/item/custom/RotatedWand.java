package com.beigu.yunbeiuc.item.custom;
import com.beigu.yunbeiuc.api.mapper.ItemCompat;

import com.beigu.yunbeiuc.api.mapper.VersionServices;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

import java.util.List;

public class RotatedWand extends ItemCompat {
    public RotatedWand(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverTextCompat(ItemStack stack, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.rotated_wand.tooltip"));
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        Block block = state.getBlock();

        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction currentFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction newFacing = getClockwiseDirection(currentFacing);

            context.getLevel() .setBlock(
                    context.getClickedPos(),
                    state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing), VersionServices.blocks().updateAll()
            );

            if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                ItemCompat.hurtAndBreakCompat(context.getItemInHand(), 1, context.getPlayer(), context.getHand());
            }

            return InteractionResult.SUCCESS;
        }

        else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction currentFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            if (currentFacing.getAxis().isHorizontal()) {
                Direction newFacing = getClockwiseDirection(currentFacing);

                context.getLevel() .setBlock(
                        context.getClickedPos(),
                        state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing), VersionServices.blocks().updateAll()
                );

                if (context.getPlayer() != null && !context.getPlayer().isCreative()) {
                    ItemCompat.hurtAndBreakCompat(context.getItemInHand(), 1, context.getPlayer(), context.getHand());
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    private Direction getClockwiseDirection(Direction current) {
        switch (current) {
            case NORTH:
                return Direction.EAST;
            case EAST:
                return Direction.SOUTH;
            case SOUTH:
                return Direction.WEST;
            case WEST:
                return Direction.NORTH;
            default:
                return current;
        }
    }
}
