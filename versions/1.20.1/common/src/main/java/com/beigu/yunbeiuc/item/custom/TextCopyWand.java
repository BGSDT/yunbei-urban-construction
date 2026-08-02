package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.entity.CustomSignBlockEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TextCopyWand extends Item {
    private static final String COPIED_DATA_KEY = "CopiedTextLines";

    public TextCopyWand(Settings settings) {
        super(settings);
    }

    private static boolean copySignText(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
        if (world.isClient || player == null) return false;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CustomSignBlockEntity sign)) {
            player.sendMessage(Text.translatable("item.yunbeiuc.text_copy_wand.not_supported"), true);
            return false;
        }

        List<CustomSignBlockEntity.TextLineData> textLines = sign.getTextLines();
        NbtList list = new NbtList();
        for (CustomSignBlockEntity.TextLineData line : textLines) {
            list.add(line.toNbt());
        }
        stack.getOrCreateNbt().put(COPIED_DATA_KEY, list);
        player.sendMessage(Text.translatable("item.yunbeiuc.text_copy_wand.copied", textLines.size()), true);
        return true;
    }

    private static boolean pasteSignText(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
        NbtCompound tag = stack.getNbt();
        if (tag == null || !tag.contains(COPIED_DATA_KEY)) {
            player.sendMessage(Text.translatable("item.yunbeiuc.text_copy_wand.no_data"), true);
            return false;
        }
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CustomSignBlockEntity sign)) {
            player.sendMessage(Text.translatable("item.yunbeiuc.text_copy_wand.not_supported"), true);
            return false;
        }

        NbtList list = tag.getList(COPIED_DATA_KEY, 10);
        List<CustomSignBlockEntity.TextLineData> lines = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            lines.add(CustomSignBlockEntity.TextLineData.fromNbt(list.getCompound(i)));
        }
        sign.getTextLines().addAll(lines);
        sign.markDirty();
        world.updateListeners(pos, sign.getCachedState(), sign.getCachedState(), 3);
        player.sendMessage(Text.translatable("item.yunbeiuc.text_copy_wand.pasted", lines.size()), true);
        return true;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        BlockEntity be = world.getBlockEntity(pos);
        // 对自定义路牌不能破坏（留给 onUseOnBlock 处理）
        if (be instanceof CustomSignBlockEntity) return false;
        // 其他方块正常左键破坏
        return true;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        PlayerEntity player = context.getPlayer();
        if (player == null) return ActionResult.PASS;
        World world = context.getWorld();
        if (world.isClient) return ActionResult.SUCCESS;

        ItemStack stack = context.getStack();
        BlockPos pos = context.getBlockPos();
        BlockEntity be = world.getBlockEntity(pos);

        if (be instanceof CustomSignBlockEntity) {
            boolean success = player.isSneaking()
                    ? copySignText(stack, player, world, pos)
                    : pasteSignText(stack, player, world, pos);
            return success ? ActionResult.SUCCESS : ActionResult.FAIL;
        }
        return ActionResult.PASS;
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.yunbeiuc.text_copy_wand.tooltip.copy"));
        tooltip.add(Text.translatable("item.yunbeiuc.text_copy_wand.tooltip.paste"));
        NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains(COPIED_DATA_KEY)) {
            NbtList list = tag.getList(COPIED_DATA_KEY, 10);
            tooltip.add(Text.translatable("item.yunbeiuc.text_copy_wand.tooltip.stored", list.size()));
            for (int i = 0; i < list.size() && i < 4; i++) {
                CustomSignBlockEntity.TextLineData line = CustomSignBlockEntity.TextLineData.fromNbt(list.getCompound(i));
                tooltip.add(Text.literal("  " + line.getText()).formatted(Formatting.GRAY));
            }
        }
    }
}