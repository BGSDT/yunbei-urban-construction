package com.beigu.yunbeiuc.network;

import com.beigu.yunbeiuc.item.custom.LinkWand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ChatCommandHandler {

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(
                CommandManager.literal("yunbeiuc")
                        .then(CommandManager.literal("lights")
                                .then(CommandManager.argument("mode", IntegerArgumentType.integer(1, 2))
                                        .executes(context -> {
                                            ServerCommandSource source = context.getSource();
                                            ServerPlayerEntity player = source.getPlayer();

                                            if (player == null) {
                                                source.sendError(Text.literal("该命令只能由玩家执行"));
                                                return 0;
                                            }

                                            int mode = IntegerArgumentType.getInteger(context, "mode");

                                            if (mode == 1) {
                                                LinkWand.setPlayerMode(player, 1);
                                                player.sendMessage(Text.literal("✓ 已选择十字路口模式，现在可以使用链接法杖点击红绿灯了！"), false);
                                                player.sendMessage(Text.literal("需要选择8个红绿灯：每个方向各一个左转和一个直行"), false);
                                                return 1;
                                            } else if (mode == 2) {
                                                LinkWand.setPlayerMode(player, 2);
                                                player.sendMessage(Text.literal("✓ 已选择T字路口模式，现在可以使用链接法杖点击红绿灯了！"), false);
                                                player.sendMessage(Text.literal("需要选择3个红绿灯：三个不同方向各一个直行红绿灯（北东南/北东西/北西南/东西南）"), false);
                                                return 1;
                                            }

                                            return 0;
                                        })
                                )
                        )
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