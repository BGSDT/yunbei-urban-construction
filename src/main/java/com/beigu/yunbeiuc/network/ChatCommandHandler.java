package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.item.custom.LinkWand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ChatCommandHandler {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("traffic")
                .then(CommandManager.literal("yes")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                boolean handled = LinkWand.handlePlayerInput(player, "YES");
                                if (!handled) {
                                    player.sendMessage(Text.literal("没有待确认的红绿灯组"), false);
                                }
                            }
                            return 1;
                        }))
                .then(CommandManager.literal("no")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                boolean handled = LinkWand.handlePlayerInput(player, "NO");
                                if (!handled) {
                                    player.sendMessage(Text.literal("没有待确认的红绿灯组"), false);
                                }
                            }
                            return 1;
                        }))
                .then(CommandManager.literal("cancel")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                boolean handled = LinkWand.handlePlayerInput(player, "CANCEL");
                                if (!handled) {
                                    player.sendMessage(Text.literal("没有待确认的红绿灯组"), false);
                                }
                            }
                            return 1;
                        }))
                .then(CommandManager.literal("status")
                        .executes(context -> {
                            ServerPlayerEntity player = context.getSource().getPlayer();
                            if (player != null) {
                                int count = LinkWand.getSelectionCount(player);
                                player.sendMessage(Text.literal("当前已选择 " + count + " 个红绿灯方块"), false);
                            }
                            return 1;
                        }))
        );
    }
}