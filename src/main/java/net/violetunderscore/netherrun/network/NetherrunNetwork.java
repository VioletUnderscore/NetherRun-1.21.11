package net.violetunderscore.netherrun.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.violetunderscore.netherrun.network.payloads.AllTimersPayload;
import net.violetunderscore.netherrun.network.payloads.OneTimerPayload;
import net.violetunderscore.netherrun.network.payloads.ToggleBoardPayload;

public class NetherrunNetwork {
    public static void ToggleBoard(boolean on, ServerPlayerEntity p) {
        ServerPlayNetworking.send(p, new ToggleBoardPayload(on));
    }
    public static void ToggleBoardForAll(boolean on, MinecraftServer server) {
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(p, new ToggleBoardPayload(on));
        }
    }

    public static void UpdateScores(int team1, int team2, int max, ServerPlayerEntity p) {
        ServerPlayNetworking.send(p, new AllTimersPayload(team1, team2, max));
    }
    public static void UpdateScoresForAll(int team1, int team2, int max, MinecraftServer server){
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(p, new AllTimersPayload(team1, team2, max));
        }
    }
    public static void UpdateOneScoreForAll(int score, int team, MinecraftServer server){
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(p, new OneTimerPayload(score, team));
        }
    }
}
