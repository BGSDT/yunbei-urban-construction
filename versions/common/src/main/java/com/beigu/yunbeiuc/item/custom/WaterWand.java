package com.beigu.yunbeiuc.item.custom;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

public class WaterWand extends Item {

    public WaterWand(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(new net.minecraft.network.chat.TranslatableComponent("item.yunbeiuc.water_wand.tooltip"));
        super .appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level world = context.getLevel();
        BlockPos topPos = context.getClickedPos();
        Player player = context.getPlayer();

        if (!world.isClientSide) {
            if (player == null){
                return InteractionResult.SUCCESS;
            }
        }

        if (player != null && !player.getAbilities().instabuild) {
            context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
        }

        int count = 0;
        // 从topPos开始向下生成3层，横向保持3x3
        for (int y = 0; y < 3; y++) {
            for (int x = -1; x <= 1; x++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos pos = topPos.offset(x, -y, z);
                    if (world.setBlock(pos, Blocks.WATER.defaultBlockState(), Block.UPDATE_ALL)) {
                        count++;
                    }
                }
            }
        }

        if (count > 0) {
            player.displayClientMessage(new net.minecraft.network.chat.TranslatableComponent("item.yunbeiuc.water_wand.success", count), true);
        }

        return InteractionResult.PASS;
    }
}
