package net.violetunderscore.netherrun.network.payloads;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record ToggleBoardPayload(boolean on) implements CustomPayload {
    public static final CustomPayload.Id<ToggleBoardPayload> ID =
            new CustomPayload.Id<>(Identifier.of("netherrun", "toggle_board"));

    public static final PacketCodec<RegistryByteBuf, ToggleBoardPayload> CODEC =
            PacketCodec.tuple(
                    PacketCodecs.BOOLEAN, ToggleBoardPayload::on,
                    ToggleBoardPayload::new
            );

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
