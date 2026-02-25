package com.beigu.yunbeiuc.item.custom;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.sapling.OakSaplingGenerator;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;

public class TreeWand extends Item {
    public TreeWand(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.yunbeiuc.tree_wand.tooltip"));
        super.appendTooltip(stack, world, tooltip, context);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) {
            return ActionResult.PASS;
        }

        BlockPos pos = context.getBlockPos();
        BlockPos abovePos = pos.up();

        if (context.getWorld() instanceof ServerWorld serverWorld) {
            BlockState state = serverWorld.getBlockState(pos);
            BlockState originalAboveState = serverWorld.getBlockState(abovePos);

            if (state.isOf(Blocks.GRASS_BLOCK) || state.isOf(Blocks.DIRT) || state.isOf(Blocks.PODZOL)) {
                if (serverWorld.getBlockState(abovePos).isAir()) {
                    // 保存原始方块状态
                    BlockState originalState = serverWorld.getBlockState(pos);

                    // 使用原版树木生成机制
                    boolean success = false;

                    // 尝试使用OakSaplingGenerator生成树木
                    OakSaplingGenerator generator = new OakSaplingGenerator();
                    Random random = serverWorld.getRandom();

                    // 不直接放置树苗，使用生成器在上方位置生成完整的树
                    success = generator.generate(
                            serverWorld,
                            serverWorld.getChunkManager().getChunkGenerator(),
                            abovePos,  // 改为上方位置，而不是原来的位置
                            Blocks.OAK_SAPLING.getDefaultState(),
                            random
                    );

                    if (success) {
                        // 播放音效和显示消息
                        serverWorld.playSound(null, abovePos, SoundEvents.BLOCK_GRASS_PLACE,
                                SoundCategory.BLOCKS, 1.0F, 1.0F);
                        player.sendMessage(Text.translatable("item.yunbeiuc.tree_wand.success"), true);

                        // 非创造模式消耗耐久
                        if (!player.getAbilities().creativeMode) {
                            context.getStack().damage(1, player, p -> p.sendToolBreakStatus(context.getHand()));
                        }

                        return ActionResult.SUCCESS;
                    } else {
                        // 确保恢复原始方块状态，移除可能留下的树苗
                        serverWorld.setBlockState(pos, originalState);

                        // 如果上方变成了树苗，恢复为空气
                        if (serverWorld.getBlockState(abovePos).isOf(Blocks.OAK_SAPLING)) {
                            serverWorld.setBlockState(abovePos, originalAboveState);
                        }

                        player.sendMessage(Text.translatable("item.yunbeiuc.tree_wand.failed"), true);
                        return ActionResult.FAIL;
                    }
                } else {
                    player.sendMessage(Text.translatable("item.yunbeiuc.tree_wand.no_space"), true);
                }
            } else {
                player.sendMessage(Text.translatable("item.yunbeiuc.tree_wand.invalid_block"), true);
            }
        }

        return ActionResult.PASS;
    }
}