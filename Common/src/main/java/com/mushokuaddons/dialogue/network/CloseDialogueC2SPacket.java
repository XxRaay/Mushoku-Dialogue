package com.mushokuaddons.dialogue.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CloseDialogueC2SPacket() implements CustomPacketPayload {
    public static final Type<CloseDialogueC2SPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mushokudialogue", "c2s_close_dialogue"));

    public static final StreamCodec<ByteBuf, CloseDialogueC2SPacket> STREAM_CODEC = StreamCodec.unit(new CloseDialogueC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
