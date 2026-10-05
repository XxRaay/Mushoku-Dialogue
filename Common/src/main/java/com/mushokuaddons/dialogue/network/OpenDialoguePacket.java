package com.mushokuaddons.dialogue.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record OpenDialoguePacket(
        Component speaker,
        Component text,
        int entityId,
        List<ClientChoiceEntry> choices
) implements CustomPacketPayload {

    public static final Type<OpenDialoguePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mushokudialogue", "open_dialogue"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientChoiceEntry> CHOICE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientChoiceEntry::index,
            ComponentSerialization.STREAM_CODEC, ClientChoiceEntry::text,
            ByteBufCodecs.BOOL, ClientChoiceEntry::enabled,
            ComponentSerialization.STREAM_CODEC, ClientChoiceEntry::disabledTooltip,
            ClientChoiceEntry::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenDialoguePacket> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, OpenDialoguePacket::speaker,
            ComponentSerialization.STREAM_CODEC, OpenDialoguePacket::text,
            ByteBufCodecs.INT, OpenDialoguePacket::entityId,
            CHOICE_STREAM_CODEC.apply(ByteBufCodecs.list()), OpenDialoguePacket::choices,
            OpenDialoguePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record ClientChoiceEntry(int index, Component text, boolean enabled, Component disabledTooltip) {
    }
}
