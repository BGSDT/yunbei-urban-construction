package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.item.custom.LinkWand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class ChatCommandHandler {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("yunbeiuc")
                        // 模式选择
                        .then(CommandManager.literal("lights")
                                .then(CommandManager.argument("mode", IntegerArgumentType.integer(1, 1))
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity player = source.getPlayer();

                                            if (player == null) {
                                                source.sendError(Text.literal("该命令只能由玩家执行"));
                                                return 0;
                                            }

                                            int mode = IntegerArgumentType.getInteger(context, "mode");

                                            if (mode == 1) {
                                                LinkWand.setModeSelected(player);
                                                player.sendMessage(Text.literal("✓ 已选择模式 " + mode + "，现在可以使用链接法杖点击红绿灯了！"), false);
                                                player.sendMessage(Text.literal("需要选择8个红绿灯：每个方向各一个左转和一个直行"), false);
                                                return 1;
                                            }

                                            return 0;
                                        })
                                )
                        )
                        // 回答命令：/yunbeiuc answer <confirm/reset/cancel>
                        .then(CommandManager.literal("answer")
                                .then(CommandManager.argument("action", StringArgumentType.word())
                                        .suggests((context, builder) -> {
                                            builder.suggest("confirm");
                                            builder.suggest("reset");
                                            builder.suggest("cancel");
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity player = source.getPlayer();

                                            if (player == null) {
                                                source.sendError(Text.literal("该命令只能由玩家执行"));
                                                return 0;
                                            }

                                            String action = StringArgumentType.getString(context, "action");

                                            // 验证action是否有效
                                            if (!action.equalsIgnoreCase("confirm") &&
                                                    !action.equalsIgnoreCase("reset") &&
                                                    !action.equalsIgnoreCase("cancel")) {
                                                player.sendMessage(Text.literal("无效的操作！可用操作: confirm, reset, cancel"), false);
                                                return 0;
                                            }

                                            boolean handled = LinkWand.handleAnswerInput(player, action.toLowerCase());
                                            if (!handled) {
                                                return 0;
                                            }
                                            return 1;
                                        })
                                )
                        )
        );
    }
}