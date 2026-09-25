package com.beigu.yunbeiuc.item.custom;

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

public class RotatedWand extends Item {
    public RotatedWand(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.rotated_wand.tooltip"));
        super .appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        // 获取当前方块状态
        BlockState state = context.getLevel().getBlockState(context.getClickedPos());
        Block block = state.getBlock();

        // 检查方块是否有水平朝向属性
        if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction currentFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            Direction newFacing = getClockwiseDirection(currentFacing);

            // 更新方块状态
            context.getLevel() .setBlock(
                    context.getClickedPos(),
                    state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing), Block.UPDATE_ALL
            );

            // 消耗耐久度
            if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                context.getItemInHand().hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(context.getHand()));
            }

            return InteractionResult.SUCCESS;
        }

        // 对于使用FACING属性的方块（如楼梯）
        else if (state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) {
            Direction currentFacing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);
            // 只处理水平方向
            if (currentFacing.getAxis().isHorizontal()) {
                Direction newFacing = getClockwiseDirection(currentFacing);

                context.getLevel() .setBlock(
                        context.getClickedPos(),
                        state.setValue(BlockStateProperties.HORIZONTAL_FACING, newFacing), Block.UPDATE_ALL
                );

                if (context.getPlayer() != null && !context.getPlayer().getAbilities().instabuild) {
                    context.getItemInHand().hurtAndBreak(1, context.getPlayer(), p -> p.broadcastBreakEvent(context.getHand()));
                }

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    // 关键修复：正确的顺时针方向转换
    private Direction getClockwiseDirection(Direction current) {
        switch (current) {
            case NORTH:
                return Direction.EAST;  // 北 → 东
            case EAST:
                return Direction.SOUTH; // 东 → 南
            case SOUTH:
                return Direction.WEST;  // 南 → 西
            case WEST:
                return Direction.NORTH; // 西 → 北
            default:
                return current;
        }
    }
}
