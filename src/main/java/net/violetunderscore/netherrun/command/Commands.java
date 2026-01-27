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
                            context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.start.started").withColor(0xFF7777), false);
                            break;
                        case 2:
                            context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.start.empty_team").withColor(0xFF7777), false);
                            break;
                        case 1:
                            break;
                    }
                    return 1;
                })
            )

            .then(CommandManager.literal("stop")
                .executes(context -> {
                    int result = NetherRun.getGame().endGame(false);
                    switch (result) {
                        case 0:
                            context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.end.stopped").withColor(0xFF7777), false);
                            break;
                        case 1:
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
                                    context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.join.invalid", team).withColor(0xFF7777), false);
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

            .then(CommandManager.literal("setTarget")
                .then(CommandManager.argument("score", IntegerArgumentType.integer())
                    .executes(context -> {
                        int score = IntegerArgumentType.getInteger(context, "score");
                        NetherRun.getGame().setTarget(score);
                        context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.set_target", score), false);
                        return 1;
                    })
                    .then(CommandManager.argument("team", IntegerArgumentType.integer())
                        .executes(context -> {
                            int score = IntegerArgumentType.getInteger(context, "score");
                            int team = IntegerArgumentType.getInteger(context, "team");
                            int result = NetherRun.getGame().setTarget(score, team);
                            switch (result) {
                                case 1:
                                    context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.set_team_target", score, team), false);
                                    break;
                                case 0:
                                    context.getSource().sendFeedback(() -> Text.translatable("cmd.netherrun.set_team_target.invalid", team).withColor(0xFF7777), false);
                                    break;
                            }
                            return 1;
                        })
                    )
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
