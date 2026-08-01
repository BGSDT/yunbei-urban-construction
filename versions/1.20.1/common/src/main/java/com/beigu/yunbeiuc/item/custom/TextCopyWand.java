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
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
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

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        BlockEntity be = world.getBlockEntity(pos);
        return be instanceof CustomSignBlockEntity;
    }

    @Override
    public float getMiningSpeedMultiplier(ItemStack stack, BlockState state) {
        return 0.0F;
    }

    public static boolean copySignText(ItemStack stack, PlayerEntity player, World world, BlockPos pos) {
        if (world.isClient || player == null) return false;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof CustomSignBlockEntity sign)) return false;

        List<CustomSignBlockEntity.TextLineData> textLines = sign.getTextLines();
        NbtList list = new NbtList();
        for (CustomSignBlockEntity.TextLineData line : textLines) {
            list.add(line.toNbt());
        }
        stack.getOrCreateNbt().put(COPIED_DATA_KEY, list);
        player.sendMessage(Text.translatable("item.yunbeiuc.text_copy_wand.copied", textLines.size()), true);
        return true;
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return ActionResult.PASS;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);
        if (world.isClient) return TypedActionResult.pass(stack);

        HitResult hit = user.raycast(10, 0, false);
        if (hit.getType() == HitResult.Type.BLOCK) {
            BlockPos pos = ((BlockHitResult) hit).getBlockPos();
            BlockEntity be = world.getBlockEntity(pos);
            if (be instanceof CustomSignBlockEntity sign) {
                NbtCompound tag = stack.getOrCreateNbt();
                if (tag.contains(COPIED_DATA_KEY)) {
                    NbtList list = tag.getList(COPIED_DATA_KEY, 10);
                    List<CustomSignBlockEntity.TextLineData> lines = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        lines.add(CustomSignBlockEntity.TextLineData.fromNbt(list.getCompound(i)));
                    }
                    sign.getTextLines().addAll(lines);
                    sign.markDirty();
                    world.updateListeners(pos, sign.getCachedState(), sign.getCachedState(), 3);
                    user.sendMessage(Text.translatable("item.yunbeiuc.text_copy_wand.pasted", lines.size()), true);
                    return TypedActionResult.success(stack);
                } else {
                    user.sendMessage(Text.translatable("item.yunbeiuc.text_copy_wand.no_data"), true);
                    return TypedActionResult.fail(stack);
                }
            } else {
                user.sendMessage(Text.translatable("item.yunbeiuc.text_copy_wand.not_supported"), true);
                return TypedActionResult.fail(stack);
            }
        }
        return TypedActionResult.pass(stack);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        tooltip.add(Text.translatable("item.yunbeiuc.text_copy_wand.tooltip.left_click"));
        tooltip.add(Text.translatable("item.yunbeiuc.text_copy_wand.tooltip.right_click"));
        NbtCompound tag = stack.getNbt();
        if (tag != null && tag.contains(COPIED_DATA_KEY)) {
            int count = tag.getList(COPIED_DATA_KEY, 10).size();
            tooltip.add(Text.translatable("item.yunbeiuc.text_copy_wand.tooltip.stored", count));
        }
    }
}