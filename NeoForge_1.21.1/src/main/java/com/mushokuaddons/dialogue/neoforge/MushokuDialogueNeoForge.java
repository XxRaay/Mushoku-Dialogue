package com.mushokuaddons.dialogue.neoforge;

import com.mushokuaddons.dialogue.MushokuDialogueCommon;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@Mod(MushokuDialogueCommon.MOD_ID)
public class MushokuDialogueNeoForge {
    public MushokuDialogueNeoForge(IEventBus modEventBus, ModContainer modContainer) {
        MushokuDialogueCommon.init();
    }
}
