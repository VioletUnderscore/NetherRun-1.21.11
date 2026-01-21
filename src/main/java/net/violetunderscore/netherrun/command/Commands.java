package net.violetunderscore.netherrun.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.violetunderscore.netherrun.NetherRun;

public class Commands {
    public static void registerCommands() {
        CommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess, environment) -> {
            if (environment.dedicated || environment.integrated) {
                register(dispatcher);
            }
        }));
    }

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("netherrun")
            .executes(context -> {
                help();
                return 1;
            })

            .then(CommandManager.literal("start")
                .executes(context -> {
                    int result = NetherRun.getGame().startGame();
                    switch (result) {
                        case 0:
                            context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.start.started"), false);
                            break;
                        case 2:
                            context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.start.empty_team"), false);
                            break;
                        case 1:
                            context.getSource().getServer().getPlayerManager().broadcast(Text.translatable("cmd.netherrun.start.success"), false);
                            break;
                    }
                    return 1;
                })
            )

            .then(CommandManager.literal("joinTeam")
                .then(CommandManager.argument("teamNumber", IntegerArgumentType.integer())
                    .executes(context -> {
                        if (context.getSource().isExecutedByPlayer() && context.getSource().getPlayer() != null) {
                            int team = IntegerArgumentType.getInteger(context, "teamNumber");
                            int result = NetherRun.getGame().joinGame(context.getSource().getPlayer().getUuid(), team);
                            switch (result) {
                                case 0:
                                    context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.join.invalid", team), false);
                                    break;
                                case 1:
                                    context.getSource().getServer().getPlayerManager().broadcast(Text.translatable("cmd.netherrun.join.success", context.getSource().getPlayer().getDisplayName(), team), false);
                                    break;
                            }
                        } else {
                            context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.mustbeplayer"), false);
                        }
                        return 1;
                    })
                )
            )

            .then(CommandManager.literal("debug")
                .then(CommandManager.literal("forceStartRound")
                    .executes(context -> {
                        NetherRun.getGame().startRound(true);
                        return 1;
                    })
                )
                .then(CommandManager.literal("spawnMeHere")
                    .executes(context -> {
                        if (context.getSource().getPlayer() != null) {
                            NetherRun.getGame().spawnPlayer(context.getSource().getPlayer(), context.getSource().getPlayer().getBlockPos());
                        }
                        return 1;
                    })
                )
            )
        );
    }

    public static void help() {

    }
}
