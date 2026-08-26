package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.item.custom.LinkWand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

public class ChatCommandHandler {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("yunbeiuc")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    PlayerEntity player = source.getPlayer();
                    if (player != null) {
                        LinkWand.clearPlayerLinking(player);
                    }
                    source.sendFeedback(() -> Text.literal("§e已取消链接操作"), false);
                    return 1;
                })
        );
    }
}
