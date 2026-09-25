package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TextCopyWand extends Item {
    private static final String COPIED_DATA_KEY = "CopiedTextLines";

    public TextCopyWand(Properties settings) {
        super(settings);
    }

    private static boolean copySignText(ItemStack stack, Player player, Level world, BlockPos pos) {
        if (world.isClientSide || player == null) return false;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CustomSignBlockEntity sign)) {
            player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.text_copy_wand.not_supported"), true);
            return false;
        }

        List<CustomSignBlockEntity.TextLineData> textLines = sign.getTextLines();
        ListTag list = new ListTag();
        for (CustomSignBlockEntity.TextLineData line : textLines) {
            list.add(line.toNbt());
        }
        stack.getOrCreateTag().put(COPIED_DATA_KEY, list);
        player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.text_copy_wand.copied", textLines.size()), true);
        return true;
    }

    private static boolean pasteSignText(ItemStack stack, Player player, Level world, BlockPos pos) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(COPIED_DATA_KEY)) {
            player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.text_copy_wand.no_data"), true);
            return false;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CustomSignBlockEntity sign)) {
            player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.text_copy_wand.not_supported"), true);
            return false;
        }

        ListTag list = tag.getList(COPIED_DATA_KEY, 10);
        List<CustomSignBlockEntity.TextLineData> lines = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            lines.add(CustomSignBlockEntity.TextLineData.fromNbt(list.getCompound(i)));
        }
        sign.getTextLines().addAll(lines);
        sign.setChanged();
        world.sendBlockUpdated(pos, sign.getBlockState(), sign.getBlockState(), 3);
        player.displayClientMessage(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.text_copy_wand.pasted", lines.size()), true);
        return true;
    }

    @Override
    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        BlockEntity be = world.getBlockEntity(pos);
        // 对自定义路牌不能破坏（留给 onUseOnBlock 处理）
        if (be instanceof CustomSignBlockEntity) return false;
        // 其他方块正常左键破坏
        return true;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;
        Level world = context.getLevel();
        if (world.isClientSide) return InteractionResult.SUCCESS;

        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos();
        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof CustomSignBlockEntity) {
            boolean success = player.isShiftKeyDown()
                    ? copySignText(stack, player, world, pos)
                    : pasteSignText(stack, player, world, pos);
            return success ? InteractionResult.SUCCESS : InteractionResult.FAIL;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.text_copy_wand.tooltip.copy"));
        tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.text_copy_wand.tooltip.paste"));
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(COPIED_DATA_KEY)) {
            ListTag list = tag.getList(COPIED_DATA_KEY, 10);
            tooltip.add(com.beigu.yunbeiuc.api.text.Text.translatable("item.yunbeiuc.text_copy_wand.tooltip.stored", list.size()));
            for (int i = 0; i < list.size() && i < 4; i++) {
                CustomSignBlockEntity.TextLineData line = CustomSignBlockEntity.TextLineData.fromNbt(list.getCompound(i));
                tooltip.add(com.beigu.yunbeiuc.api.text.Text.literal("  " + line.getText()).withStyle(ChatFormatting.GRAY));
            }
        }
    }
}
