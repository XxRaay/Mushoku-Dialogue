package com.mushokuaddons.dialogue.network;

import com.mushokuaddons.dialogue.client.network.ClientDialogueHandler;
import com.mushokuaddons.dialogue.manager.DialogueManager;
import dev.architectury.networking.NetworkManager;
import dev.architectury.platform.Platform;
import net.fabricmc.api.EnvType;
import net.minecraft.server.level.ServerPlayer;

public class DialogueNetworking {
    public static void register() {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, OpenDialoguePacket.TYPE, OpenDialoguePacket.STREAM_CODEC, (packet, context) -> {
            if (Platform.getEnv() == EnvType.CLIENT) {
                context.queue(() -> ClientDialogueHandler.handleOpenDialogue(packet));
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.S2C, CloseDialoguePacket.TYPE, CloseDialoguePacket.STREAM_CODEC, (packet, context) -> {
            if (Platform.getEnv() == EnvType.CLIENT) {
                context.queue(ClientDialogueHandler::handleCloseDialogue);
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, SelectChoicePacket.TYPE, SelectChoicePacket.STREAM_CODEC, (packet, context) -> {
            if (context.getPlayer() instanceof ServerPlayer sp) {
                context.queue(() -> DialogueManager.handleChoice(sp, packet.choiceIndex()));
            }
        });

        NetworkManager.registerReceiver(NetworkManager.Side.C2S, CloseDialoguePacket.TYPE, CloseDialoguePacket.STREAM_CODEC, (packet, context) -> {
            if (context.getPlayer() instanceof ServerPlayer sp) {
                context.queue(() -> DialogueManager.closeDialogue(sp));
            }
        });
    }
}
