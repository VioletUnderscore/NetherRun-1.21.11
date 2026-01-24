package net.violetunderscore.netherrun.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.violetunderscore.netherrun.network.payloads.ToggleBoardPayload;

public class NetherrunNetwork {
    public static void ToggleBoard(boolean on, ServerPlayerEntity p) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeBoolean(on);

        ServerPlayNetworking.send(p, new ToggleBoardPayload(on));
    }
    public static void ToggleBoardForAll(boolean on, MinecraftServer server) {
        for (ServerPlayerEntity p : server.getPlayerManager().getPlayerList()) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(on);

            ServerPlayNetworking.send(p, new ToggleBoardPayload(on));
        }
    }
}
