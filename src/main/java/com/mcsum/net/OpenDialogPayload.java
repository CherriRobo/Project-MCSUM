/* Package. */
package com.mcsum.net;

/* Imports. */
import net.minecraft.network.PacketByteBuf; 
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

/* Custom payload. */
public record OpenDialogPayload() implements CustomPayload {

    public static final Id<OpenDialogPayload> ID =
            new Id<>(Identifier.of("mcsum", "open_rk800_dialog"));

    public static final PacketCodec<PacketByteBuf, OpenDialogPayload> CODEC =
            PacketCodec.unit(new OpenDialogPayload());

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}