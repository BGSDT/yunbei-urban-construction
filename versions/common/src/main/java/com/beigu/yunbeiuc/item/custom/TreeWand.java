package com.beigu.yunbeiuc.item.custom;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.grower.OakTreeGrower;
import com.beigu.yunbeiuc.api.mapper.VersionServices;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.chat.Component;
import com.beigu.yunbeiuc.api.text.Text;
import net.minecraft.world.InteractionResult;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.List;

public class TreeWand extends Item {
    public TreeWand(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Text.translatable("item.yunbeiuc.tree_wand.tooltip"));
        super.appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) {
            return InteractionResult.PASS;
        }

        BlockPos pos = context.getClickedPos();
        BlockPos abovePos = pos.above();

        if (context.getLevel() instanceof ServerLevel serverWorld) {
            BlockState state = serverWorld.getBlockState(pos);
            BlockState originalAboveState = serverWorld.getBlockState(abovePos);

            if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT) || state.is(Blocks.PODZOL)) {
                if (serverWorld.getBlockState(abovePos).isAir()) {
                    // 保存原始方块状态
                    BlockState originalState = serverWorld.getBlockState(pos);

                    // 使用原版树木生成机制
                    boolean success = false;

                    // 尝试使用OakTreeGrower生成树木
                    // 不直接放置树苗，使用生成器在上方位置生成完整的树
                    success = VersionServices.blocks().growOakTree(serverWorld, abovePos);

                    if (success) {
                        // 播放音效和显示消息
                        serverWorld.playSound(null, abovePos, SoundEvents.GRASS_PLACE,
                                SoundSource.BLOCKS, 1.0F, 1.0F);
                        player.displayClientMessage(Text.translatable("item.yunbeiuc.tree_wand.success"), true);

                        // 非创造模式消耗耐久
                        if (!player.getAbilities().instabuild) {
                            context.getItemInHand().hurtAndBreak(1, player, p -> p.broadcastBreakEvent(context.getHand()));
                        }

                        return InteractionResult.SUCCESS;
                    } else {
                        // 确保恢复原始方块状态，移除可能留下的树苗
                        serverWorld.setBlock(pos, originalState, 3);

                        // 如果上方变成了树苗，恢复为空气
                        if (serverWorld.getBlockState(abovePos).is(Blocks.OAK_SAPLING)) {
                            serverWorld.setBlock(abovePos, originalAboveState, 3);
                        }

                        player.displayClientMessage(Text.translatable("item.yunbeiuc.tree_wand.failed"), true);
                        return InteractionResult.FAIL;
                    }
                } else {
                    player.displayClientMessage(Text.translatable("item.yunbeiuc.tree_wand.no_space"), true);
                }
            } else {
                player.displayClientMessage(Text.translatable("item.yunbeiuc.tree_wand.invalid_block"), true);
            }
        }

        return InteractionResult.PASS;
    }
}
