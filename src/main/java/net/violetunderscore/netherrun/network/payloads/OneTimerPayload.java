package net.violetunderscore.netherrun.network.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record OneTimerPayload(int score, int team) implements CustomPayload {
    public static final Id<OneTimerPayload> ID =
            new Id<>(Identifier.of("netherrun", "update_score"));

    public static final PacketCodec<RegistryByteBuf, OneTimerPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.VAR_INT, OneTimerPayload::score,
                    PacketCodecs.VAR_INT, OneTimerPayload::team,
                    OneTimerPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
