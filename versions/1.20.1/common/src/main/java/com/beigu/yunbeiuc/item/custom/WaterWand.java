package com.beigu.yunbeiuc.item.custom;

import net.minecraft.block.Blocks;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class WaterWand extends Item {

    public WaterWand(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.yunbeiuc.water_wand.tooltip"));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos topPos = context.getBlockPos();
        PlayerEntity player = context.getPlayer();

        if (!world.isClient) {
            if (player == null){
                return ActionResult.SUCCESS;
            }
        }

        if (player != null && !player.getAbilities().creativeMode) {
            context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
        }

        int count = 0;
        // 从topPos开始向下生成3层，横向保持3x3
        for (int y = 0; y < 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = topPos.add(x, -y, z);
                    if (world.setBlockState(pos, Blocks.WATER.getDefaultState())) {
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            player.sendMessage(Text.translatable("item.yunbeiuc.water_wand.success", count), true);
        }

        return ActionResult.PASS;
    }
}