package com.mushokuaddons.dialogue.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectChoicePacket(int choiceIndex) implements CustomPacketPayload {
    public static final Type<SelectChoicePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mushokudialogue", "select_choice"));

    public static final StreamCodec<ByteBuf, SelectChoicePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SelectChoicePacket::choiceIndex,
            SelectChoicePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
