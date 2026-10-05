package com.mushokuaddons.dialogue;

import com.mushokuaddons.dialogue.manager.DialogueInteractionHandler;
import com.mushokuaddons.dialogue.network.DialogueNetworking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MushokuDialogueCommon {
    public static final String MOD_ID = "mushokudialogue";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void init() {
        LOGGER.info("Initializing Mushoku Dialogue API...");
        DialogueNetworking.register();
        DialogueInteractionHandler.register();
        LOGGER.info("Mushoku Dialogue API initialized.");
    }
}
