package com.mushokuaddons.dialogue.client.network;

import com.mushokuaddons.dialogue.client.gui.DialogueScreen;
import com.mushokuaddons.dialogue.network.OpenDialoguePacket;
import net.minecraft.client.Minecraft;

public class ClientDialogueHandler {
    public static void handleOpenDialogue(OpenDialoguePacket packet) {
        Minecraft mc = Minecraft.getInstance();
        mc.setScreen(new DialogueScreen(packet.speaker(), packet.text(), packet.entityId(), packet.choices(),
                packet.hasInput(), packet.inputPlaceholder(), packet.initialInput(), packet.maxInputLength()));
    }

    public static void handleCloseDialogue() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof DialogueScreen) {
            mc.setScreen(null);
        }
    }
}
