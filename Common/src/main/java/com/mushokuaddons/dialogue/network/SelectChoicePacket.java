package com.mushokuaddons.dialogue.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SelectChoicePacket(int choiceIndex, String input) implements CustomPacketPayload {
    public static final Type<SelectChoicePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mushokudialogue", "select_choice"));

    public static final StreamCodec<ByteBuf, SelectChoicePacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, SelectChoicePacket::choiceIndex,
            ByteBufCodecs.STRING_UTF8, SelectChoicePacket::input,
            SelectChoicePacket::new
    );

    public SelectChoicePacket(int choiceIndex) {
        this(choiceIndex, "");
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
