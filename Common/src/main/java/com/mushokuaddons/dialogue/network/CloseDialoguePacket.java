package com.mushokuaddons.dialogue.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record CloseDialoguePacket() implements CustomPacketPayload {
    public static final Type<CloseDialoguePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mushokudialogue", "close_dialogue"));

    public static final StreamCodec<ByteBuf, CloseDialoguePacket> STREAM_CODEC = StreamCodec.unit(new CloseDialoguePacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
