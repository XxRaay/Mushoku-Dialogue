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
        List<ClientChoiceEntry> choices,
        InputConfig inputConfig
) implements CustomPacketPayload {

    public OpenDialoguePacket(Component speaker, Component text, int entityId, List<ClientChoiceEntry> choices,
                              boolean hasInput, Component inputPlaceholder, String initialInput, int maxInputLength) {
        this(speaker, text, entityId, choices, new InputConfig(hasInput, inputPlaceholder, initialInput, maxInputLength));
    }

    public OpenDialoguePacket(Component speaker, Component text, int entityId, List<ClientChoiceEntry> choices) {
        this(speaker, text, entityId, choices, InputConfig.NONE);
    }

    public boolean hasInput() {
        return inputConfig != null && inputConfig.enabled();
    }

    public Component inputPlaceholder() {
        return inputConfig != null && inputConfig.placeholder() != null ? inputConfig.placeholder() : Component.empty();
    }

    public String initialInput() {
        return inputConfig != null && inputConfig.initialText() != null ? inputConfig.initialText() : "";
    }

    public int maxInputLength() {
        return inputConfig != null && inputConfig.maxLength() > 0 ? inputConfig.maxLength() : 32;
    }

    public static final Type<OpenDialoguePacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath("mushokudialogue", "open_dialogue"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ClientChoiceEntry> CHOICE_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, ClientChoiceEntry::index,
            ComponentSerialization.STREAM_CODEC, ClientChoiceEntry::text,
            ByteBufCodecs.BOOL, ClientChoiceEntry::enabled,
            ComponentSerialization.STREAM_CODEC, ClientChoiceEntry::disabledTooltip,
            ClientChoiceEntry::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, InputConfig> INPUT_STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.BOOL, InputConfig::enabled,
            ComponentSerialization.STREAM_CODEC, InputConfig::placeholder,
            ByteBufCodecs.STRING_UTF8, InputConfig::initialText,
            ByteBufCodecs.VAR_INT, InputConfig::maxLength,
            InputConfig::new
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, OpenDialoguePacket> STREAM_CODEC = StreamCodec.composite(
            ComponentSerialization.STREAM_CODEC, OpenDialoguePacket::speaker,
            ComponentSerialization.STREAM_CODEC, OpenDialoguePacket::text,
            ByteBufCodecs.INT, OpenDialoguePacket::entityId,
            CHOICE_STREAM_CODEC.apply(ByteBufCodecs.list()), OpenDialoguePacket::choices,
            INPUT_STREAM_CODEC, OpenDialoguePacket::inputConfig,
            OpenDialoguePacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public record ClientChoiceEntry(int index, Component text, boolean enabled, Component disabledTooltip) {
    }

    public record InputConfig(boolean enabled, Component placeholder, String initialText, int maxLength) {
        public static final InputConfig NONE = new InputConfig(false, Component.empty(), "", 32);
    }
}
