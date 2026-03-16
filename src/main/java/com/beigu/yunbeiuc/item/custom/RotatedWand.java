package com.beigu.yunbeiuc.item.custom;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class RotatedWand extends Item {
    public RotatedWand(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.yunbeiuc.rotated_wand.tooltip"));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        // 获取当前方块状态
        BlockState state = context.getWorld().getBlockState(context.getBlockPos());
        Block block = state.getBlock();

        // 检查方块是否有水平朝向属性
        if (state.contains(Properties.HORIZONTAL_FACING)) {
            Direction currentFacing = state.get(Properties.HORIZONTAL_FACING);
            Direction newFacing = getClockwiseDirection(currentFacing);

            // 更新方块状态
            context.getWorld().setBlockState(
                    context.getBlockPos(),
                    state.with(Properties.HORIZONTAL_FACING, newFacing)
            );

            // 消耗耐久度
            if (!context.getPlayer().isCreative()) {
                context.getStack().damage(1, context.getPlayer(), p -> p.sendToolBreakStatus(context.getHand()));
            }

            return ActionResult.SUCCESS;
        }

        // 对于使用FACING属性的方块（如楼梯）
        else if (state.contains(Properties.FACING)) {
            Direction currentFacing = state.get(Properties.FACING);
            // 只处理水平方向
            if (currentFacing.getAxis().isHorizontal()) {
                Direction newFacing = getClockwiseDirection(currentFacing);

                context.getWorld().setBlockState(
                        context.getBlockPos(),
                        state.with(Properties.FACING, newFacing)
                );

                if (!context.getPlayer().isCreative()) {
                    context.getStack().damage(1, context.getPlayer(), p -> p.sendToolBreakStatus(context.getHand()));
                }

                return ActionResult.SUCCESS;
            }
        }

        return ActionResult.PASS;
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
