package net.violetunderscore.netherrun.network.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record AllTimersPayload(int team1, int team2, int max) implements CustomPayload {
    public static final Id<AllTimersPayload> ID =
            new Id<>(Identifier.of("netherrun", "update_scores"));

    public static final PacketCodec<RegistryByteBuf, AllTimersPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, AllTimersPayload::team1,
                    PacketCodecs.VAR_INT, AllTimersPayload::team2,
                    PacketCodecs.VAR_INT, AllTimersPayload::max,
                    AllTimersPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
