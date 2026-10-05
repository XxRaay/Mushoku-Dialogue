package com.mushokuaddons.dialogue.fabric;

import com.mushokuaddons.dialogue.MushokuDialogueCommon;
import net.fabricmc.api.ModInitializer;

public class MushokuDialogueFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        MushokuDialogueCommon.init();
    }
}
