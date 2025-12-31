package com.beigu.yunbeiuc.item.custom;

import com.beigu.yunbeiuc.screen.DebugToolScreen;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DebugTool extends Item {
    public DebugTool(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        if (!world.isClient()) {
            return ActionResult.SUCCESS;
        }

        // 在客户端打开屏幕
        MinecraftClient.getInstance().setScreen(new DebugToolScreen(state, pos));
        return ActionResult.SUCCESS;
    }
}
